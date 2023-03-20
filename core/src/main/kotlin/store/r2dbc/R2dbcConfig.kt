package io.wafflestudio.truffle.core.store.r2dbc

import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.spi.ConnectionFactoryOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Configuration
class R2dbcConfig(
    @Value("\${spring.r2dbc.url}") private val url: String,
    @Value("\${spring.r2dbc.username}") private val username: String,
    @Value("\${spring.r2dbc.password}") private val password: String,
    private val allConverters: List<Converter<*, *>>,
) : AbstractR2dbcConfiguration() {

    @Bean
    override fun connectionFactory() =
        ConnectionFactoryBuilder.withOptions(
            ConnectionFactoryOptions.parse(url)
                .mutate()
                .option(ConnectionFactoryOptions.USER, username)
                .option(ConnectionFactoryOptions.PASSWORD, password)
                .option(ConnectionFactoryOptions.SSL, false)
        )
            .build()
            .let {
                ConnectionPool(
                    ConnectionPoolConfiguration.builder(it)
                        .validationQuery("SELECT 1")
                        .acquireRetry(5)
                        .initialSize(1)
                        .maxSize(2)
                        .build()
                )
            }

    @Service
    @ReadingConverter
    class InstantReadConverter : Converter<LocalDateTime, Instant> {
        override fun convert(source: LocalDateTime): Instant = source.atOffset(ZoneOffset.UTC).toInstant()
    }

    @Service
    @WritingConverter
    class InstantWriteConverter : Converter<Instant, LocalDateTime> {
        override fun convert(source: Instant): LocalDateTime = source.atOffset(ZoneOffset.UTC).toLocalDateTime()
    }

    override fun getCustomConverters(): List<Any> {
        return allConverters
    }
}
