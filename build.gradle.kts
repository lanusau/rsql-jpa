import org.gradle.api.JavaVersion.VERSION_11

plugins {
    `java-library`
    `maven-publish`
    id("net.researchgate.release") version "2.8.1"
}

group = "com.conversantmedia.mpub"

java {
    sourceCompatibility = VERSION_11
    withSourcesJar()
}

enum class RepositoryDefinitions(val url: String) {
    PUBLIC    ("https://vault.cnvrmedia.net/nexus/content/groups/public"),
    SNAPSHOTS ("https://vault.cnvrmedia.net/nexus/content/repositories/snapshots"),
    RELEASES  ("https://vault.cnvrmedia.net/nexus/content/repositories/releases")
}

repositories {
    maven { url = uri(RepositoryDefinitions.PUBLIC.url) }
    maven { url = uri(RepositoryDefinitions.RELEASES.url) }
    maven { url = uri(RepositoryDefinitions.SNAPSHOTS.url) }
}

enum class Versions(val text: String) {
    HIBERNATE             ("5.4.32.Final"),
    H2                    ("1.4.200"),
    JETBRAINS_ANNOTATIONS ( "21.0.1"),
    LOMBOK                ( "1.18.20"),
    PERSISTENCE_API       ("2.2.3"),
    RSQL_PARSER           ( "2.1.0"),
    SPRING_CONTEXT        ("5.3.9"),
    SPRING_BOOT           ("2.5.4"),
    SPRING_DATA_JPA       ("2.5.4"),
    JUNIT                 ("5.8.2")
}

enum class Libs(val text: String) {
    HIBERNATE             ("org.hibernate:hibernate-core:${Versions.HIBERNATE.text}"),
    H2                    ("com.h2database:h2:${Versions.H2.text}"),
    JETBRAINS_ANNOTATIONS ( "org.jetbrains:annotations:${Versions.JETBRAINS_ANNOTATIONS.text}"),
    LOMBOK                ( "org.projectlombok:lombok:${Versions.LOMBOK.text}"),
    PERSISTENCE_API       ("jakarta.persistence:jakarta.persistence-api:${Versions.PERSISTENCE_API.text}"),
    RSQL_PARSER           ( "cz.jirutka.rsql:rsql-parser:${Versions.RSQL_PARSER.text}"),
    SPRING_BOOT_AUTOCONFIGURE ("org.springframework.boot:spring-boot-autoconfigure:${Versions.SPRING_BOOT.text}"),
    SPRING_BOOT_STARTER_TEST  ( "org.springframework.boot:spring-boot-starter-test:${Versions.SPRING_BOOT.text}"),
    SPRING_CONTEXT        ("org.springframework:spring-context:${Versions.SPRING_CONTEXT.text}"),
    SPRING_DATA_JPA       ("org.springframework.data:spring-data-jpa:${Versions.SPRING_DATA_JPA.text}"),
    JUNIT                 ("org.junit.jupiter:junit-jupiter:${Versions.JUNIT.text}")
}

dependencies {

    api(Libs.HIBERNATE.text)
    api(Libs.PERSISTENCE_API.text)
    api(Libs.SPRING_CONTEXT.text)
    api(Libs.SPRING_DATA_JPA.text)

    compileOnly(Libs.LOMBOK.text)
    compileOnly(Libs.JETBRAINS_ANNOTATIONS.text)
    compileOnly(Libs.SPRING_BOOT_AUTOCONFIGURE.text)

    annotationProcessor(Libs.LOMBOK.text)

    implementation(Libs.RSQL_PARSER.text)

    testCompileOnly(Libs.LOMBOK.text)
    testCompileOnly(Libs.JETBRAINS_ANNOTATIONS.text)

    testAnnotationProcessor(Libs.LOMBOK.text)

    testImplementation(Libs.H2.text)
    testImplementation(Libs.JUNIT.text)
    testImplementation(Libs.SPRING_BOOT_STARTER_TEST.text)

}

tasks.test {
    useJUnitPlatform()
}

// Setup release plugin to also run publish task
release {
    buildTasks = listOf("build","publish")
}

// Setup Maven publication
publishing {
    publications {
        create<MavenPublication>("maven") {

            from(components["java"])

            pom {
                name.set("rSQL jpa")
                description.set("rSQL backend for JPA")
                developers {
                    developer {
                        id.set("laianusa")
                        name.set("Laimonas Anusauskas")
                        email.set("laianusa@publicisgroupe.net")
                    }
                }
                scm {
                    url.set("https://git.cnvrmedia.net/projects/MPUB/repos/rsql-jpa/browse")
                }
            }
        }
    }

    repositories {
        maven {
            credentials {
                username = property("cnvrNexusUsername").toString()
                password = property("cnvrNexusPassword").toString()
            }

            // Where artifact will be published to depends on whether its a snapshot or not
            if (".*-SNAPSHOT".toRegex().matches(version.toString())) {
                url = uri(RepositoryDefinitions.SNAPSHOTS.url)
            } else {
                url = uri(RepositoryDefinitions.RELEASES.url)
            }
        }
    }
}

// Get more details on unchecked warnings
tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(arrayOf("-parameters", "-Xlint:unchecked", "-Xlint:deprecation"))
}
