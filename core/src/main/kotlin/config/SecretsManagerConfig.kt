package io.wafflestudio.truffle.core.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest

@Configuration
@Profile("!test")
class SecretsManagerConfig(
    @Value("\${secret-names}") private val secretNames: String,
    private val objectMapper: ObjectMapper,
) : BeanFactoryPostProcessor {
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        val region = Region.AP_NORTHEAST_2

        secretNames.split(",").forEach { secretName ->
            val secretString = getSecretString(secretName, region)
            val map = objectMapper.readValue<Map<String, String>>(secretString)
        }
    }

    fun getSecretString(secretName: String, region: Region): String {
        val client = SecretsManagerClient.builder().region(region).build()
        val request = GetSecretValueRequest.builder().secretId(secretName).build()
        return client.getSecretValue(request).secretString()
    }
}
