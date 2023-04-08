plugins {
    id("org.springframework.boot")
    id("org.unbroken-dome.test-sets") version "4.0.0"
}

testSets {
    register("integrationTest")
}

dependencies {
    implementation(project(":core"))
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // swagger
    val springdocVersion = "2.0.2"
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:$springdocVersion")
}

tasks.bootJar {
    archiveFileName.set("truffle-consumer.jar")
}
