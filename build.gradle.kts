/*
 * PrivateRooms is a bot managing vocal channels in a server
 * Copyright (C) 2022 RedsTom
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

plugins {
    java
    application

    id("com.diffplug.spotless").version("6.12.0")
    id("com.github.johnrengelman.shadow").version("7.1.0")
    id("io.freefair.lombok").version("6.5.0.3")
}

// Genera
group = "org.gravendev"
version = "4.0"


val main = "org.gravendev.privaterooms.Main"

// Dependencies
repositories {
    mavenCentral()
}

dependencies {
    // JDA
    implementation("net.dv8tion:JDA:5.0.0-beta.2")

    // Spring Context
    implementation("org.springframework:spring-context:6.0.3")

    // Spring Data JPA + Hibernate
    implementation("org.postgresql:postgresql:42.5.1")
    implementation("org.springframework.data:spring-data-jpa:3.0.0")
    implementation("org.hibernate:hibernate-core:6.1.6.Final")

    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.4.1")

    // Other Utils
    implementation("org.apache.commons:commons-dbcp2:2.9.0")
    implementation("ch.qos.logback:logback-classic:1.4.5")
}

// Runner
application {
    mainClass.set(main)
}

// Compiling and packaging
tasks.withType(JavaCompile::class) {
    sourceCompatibility = "17"
    targetCompatibility = "17"
    options.encoding = "UTF-8"
}

tasks.withType(Jar::class) {
    manifest {
        attributes(
            "Main-Class" to main,
        )
    }
}

// Linting
spotless {
    format("misc") {
        target("*.gradle", "*.md", "*.txt", ".gitignore")

        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }

    java {
        googleJavaFormat("1.15.0")
            .reflowLongStrings()
            .aosp()

        indentWithSpaces(4)
        importOrder()
        removeUnusedImports()
        formatAnnotations()
        trimTrailingWhitespace()
    }
}
