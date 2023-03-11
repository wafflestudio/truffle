dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.slack.api:slack-api-client:1.27.2")

    // r2dbc
    api("org.springframework.boot:spring-boot-starter-data-r2dbc")
    runtimeOnly("io.r2dbc:r2dbc-h2")
    runtimeOnly("com.github.jasync-sql:jasync-r2dbc-mysql:2.1.23")
}
