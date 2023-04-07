plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":core"))
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    val springdocVersion = "2.0.2"
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:$springdocVersion")
}

tasks.bootJar {
    archiveFileName.set("truffle-dashboard.jar")
}
