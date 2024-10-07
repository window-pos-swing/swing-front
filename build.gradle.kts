plugins {
    id("java")
    kotlin("jvm") version "1.8.0"  // 최신 Kotlin 플러그인 버전으로 업데이트
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") } // JitPack 리포지토리 추가
}

dependencies {
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

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(8))
    }
}
