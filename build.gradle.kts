group = "com.example.kyros"
version = "1.0-SNAPSHOT"

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.21")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}