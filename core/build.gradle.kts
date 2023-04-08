dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // r2dbc
    api("org.springframework.boot:spring-boot-starter-data-r2dbc")
    runtimeOnly("io.r2dbc:r2dbc-h2")
    runtimeOnly("com.github.jasync-sql:jasync-r2dbc-mysql:2.1.23")
    // r2dbc querydsl
    api("com.infobip:infobip-spring-data-r2dbc-querydsl-boot-starter:8.1.0")
    kapt("com.infobip:infobip-spring-data-jdbc-annotation-processor-common:8.1.0")

    implementation("com.slack.api:slack-api-client:1.27.2")
    implementation("com.github.ben-manes.caffeine:caffeine:3.0.3")
}
