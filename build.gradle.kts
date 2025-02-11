plugins {
    id("java")
    kotlin("jvm") version "1.8.0"  // 최신 Kotlin 플러그인 버전으로 업데이트
}

group = "org.grr"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") } // JitPack 리포지토리 추가
}

dependencies {

    implementation("org.scream3r:jssc:2.8.0")// 포트 찾는 라이브러리


    implementation(files("libs/swingx-1.6.4.jar"))
    implementation(files("TimingFramework-0.55.jar"))

    // escpos-coffee 라이브러리 추가 (JitPack에서 가져옴)
    implementation("com.github.anastaciocintra:escpos-coffee:master-SNAPSHOT")

    implementation(kotlin("stdlib-jdk8"))

    // 테스트 의존성
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("com.fazecast:jSerialComm:2.9.3")

    implementation("javazoom:jlayer:1.0.1")

    implementation("com.github.lgooddatepicker:LGoodDatePicker:11.2.1")

//    okhttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
//    json
    implementation("com.google.code.gson:gson:2.11.0")
//    jsonObject
    implementation("org.json:json:20240303")

    // 코루틴
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.3")

//    // firebase
//    implementation("com.google.firebase:firebase-admin:9.4.2")

//    websocket
    implementation("org.java-websocket:Java-WebSocket:1.6.0")

//    slf4j
    testImplementation("org.slf4j:slf4j-simple:2.0.16")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(8))
    }
}
