plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij.platform") version "2.3.0"
}

group = "dev.dalex"
version = "0.1.2"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        create("IC", "2024.3.5")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
    }

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.11.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}

intellijPlatform {
    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
    }

    pluginConfiguration {
        id = "dev.dalex.textpolisher"
        name = "AI Text Polisher"
        version = project.version.toString()
        vendor {
            name = "dalex"
            url = "https://dalex.dev"
        }
        ideaVersion {
            sinceBuild = "243"
        }
    }
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "21"
    }
    test {
        useJUnitPlatform()
    }
}
