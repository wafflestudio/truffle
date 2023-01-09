dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.slack.api:slack-api-client:1.27.2")
    implementation("software.amazon.awssdk:secretsmanager:2.19.8")
    implementation("software.amazon.awssdk:sts:2.19.8")

    // sentry FIXME
    implementation("io.sentry:sentry-spring-boot-starter:6.11.0")
    implementation("io.sentry:sentry-logback:6.11.0")
}
