plugins {
    id("application")
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("java")
}

group = "com.smarthome"
version = "1.0"

repositories {
    mavenCentral()
}

javafx {
    version = "17.0.8"
    modules = listOf(
        "javafx.controls",
        "javafx.fxml",
        "javafx.media"
    )
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass.set("com.smarthome.Main")
}

tasks.test {
    useJUnitPlatform()
}
