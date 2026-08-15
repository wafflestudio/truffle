package io.wafflestudio.truffle.core.transport

import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.send.WebhookEmbedBuilder
import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookMessageBuilder
import io.wafflestudio.truffle.core.TruffleEvent
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

@ConditionalOnProperty("transport.discord.enabled", matchIfMissing = false)
@Component
class DiscordTransport : TruffleTransport {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val clients = ConcurrentHashMap<String, WebhookClient>()

    override suspend fun send(event: TruffleEvent) {
        val client = event.client ?: return
        val webhookUrl = client.discordWebhookUrl ?: return
        if (event !is TruffleEvent.V1) return

        logger.info(
            "[DiscordTransport] sending event. clientId={}, clientName={}, clientPhase={}, webhook={}, " +
                "exceptionClass={}, exceptionMessage={}",
            client.id,
            client.name,
            client.phase,
            mask(webhookUrl),
            event.exception.className,
            event.exception.message
        )

        val filename = "${client.name}-${client.phase}_${LocalDateTime.now()}.txt".replace(':', '.')
        val stacktrace = buildString {
            val elements = event.exception.elements
            if (elements.isNotEmpty()) {
                elements.forEach {
                    appendLine("${it.className} in ${it.methodName} at line ${it.lineNumber}")
                }
            } else {
                appendLine()
            }
        }

        // https://discord.com/developers/docs/resources/message#embed-object-limits
        val embed = WebhookEmbedBuilder()
            .setTitle(WebhookEmbed.EmbedTitle(truncate(event.exception.className, 256), null))
            .setDescription(truncate("${event.exception.message}\n${event.description ?: ""}", 4096))
            .build()

        val message = WebhookMessageBuilder()
            .addFile(filename, stacktrace.toByteArray())
            .addEmbeds(embed)
            .build()

        clients.computeIfAbsent(webhookUrl) { WebhookClient.withUrl(it) }
            .send(message)
            .whenComplete { _, throwable ->
                if (throwable == null) {
                    logger.info(
                        "[DiscordTransport] send succeeded. clientId={}, clientName={}, clientPhase={}, webhook={}",
                        client.id,
                        client.name,
                        client.phase,
                        mask(webhookUrl),
                    )
                } else {
                    logger.error(
                        "[DiscordTransport] send failed. clientId={}, clientName={}, clientPhase={}, webhook={}",
                        client.id,
                        client.name,
                        client.phase,
                        mask(webhookUrl),
                        throwable,
                    )
                }
            }
    }

    // webhook token is a credential; keep only the id part in logs
    private fun mask(webhookUrl: String): String = "${webhookUrl.substringBeforeLast('/')}/***"

    private fun truncate(value: String, maxLength: Int): String =
        if (value.length <= maxLength) value else value.take(maxLength - 1) + "…"
}
