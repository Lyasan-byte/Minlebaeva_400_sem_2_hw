import java.io.FileInputStream
import java.util.Properties

plugins {
    id("java")
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.liquibase.gradle") version "2.2.2"
}

group = "com.lays"
version = "1.0-SNAPSHOT"

//val springVersion: String by project
//val jakartaVersion: String by project
//val hibernateVersion: String by project
val postgresVersion: String by project
//val freemarkerVersion: String by project
//val hikariVersion: String by project
//val springDataVersion: String by project
val springSecurityVersion: String by project

repositories {
    mavenCentral()
}

dependencies {
    //implementation("org.springframework:spring-webmvc:$springVersion")
    implementation("org.springframework.boot:spring-boot-starter-web")
//    implementation("org.springframework:spring-jdbc:$springVersion")
//    implementation("org.springframework:spring-orm:$springVersion")
//    implementation("org.springframework:spring-context-support:$springVersion")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//    implementation("org.springframework:spring-webmvc:${springVersion}")
//    implementation("org.springframework.security:spring-security-core:$springSecurityVersion")
//    implementation("org.springframework.security:spring-security-web:${springSecurityVersion}")
    implementation("org.springframework.boot:spring-boot-starter-security")
//    implementation("org.springframework.security:spring-security-config:${springSecurityVersion}")
    implementation("org.springframework.security:spring-security-taglibs:${springSecurityVersion}")
//    implementation("jakarta.servlet:jakarta.servlet-api:$jakartaVersion")
//    implementation("org.hibernate.orm:hibernate-core:$hibernateVersion")
    implementation("org.postgresql:postgresql:$postgresVersion")
//    implementation("org.freemarker:freemarker:$freemarkerVersion")
//    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("org.springframework.boot:spring-boot-starter-freemarker")
    implementation("org.springframework.boot:spring-boot-starter-mail")

//    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.3")
    implementation("org.liquibase:liquibase-core:4.33.0")
    liquibaseRuntime("org.liquibase:liquibase-core:4.33.0")
    liquibaseRuntime("org.postgresql:postgresql:$postgresVersion")
    liquibaseRuntime("info.picocli:picocli:4.6.3")
}

tasks.test {
    useJUnitPlatform()
}

val props = Properties()
props.load(file("src/main/resources/db/liquibase.properties").inputStream())
liquibase {
    activities.register("main") {
        arguments = mapOf(
            "changeLogFile" to props.get("change-log-file").toString() + ".xml",
            "url" to props.get("url"),
            "username" to props.get("username"),
            "password" to props.get("password"),
            "driver" to  props.get("driver-class-name"),
        )
    }
    runList = "main"
}