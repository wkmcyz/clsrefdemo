import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.3.72"
}


// Required since Gradle 4.10+.
repositories {
    maven(url = "https://maven.zhenguanyu.com/content/repositories/releases")
    maven(url = "https://maven.zhenguanyu.com/content/repositories/snapshots")
    google()
    maven(url = "https://plugins.gradle.org/m2/")
    mavenCentral()
    // 引入 aar 的时候需要
    maven(url = "https://maven.aliyun.com/repository/jcenter")

}

sourceSets {
    main {
        java {
            srcDir("src/common/java")
        }
    }
}

dependencies {
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.squareup.okhttp3:okhttp:4.9.2")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.apiVersion = "1.3"
}

dependencies {
    implementation(kotlin("stdlib"))
    gradleApi()
}
