plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "me.obsilabor"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-simple:2.0.0")
}

tasks {
    compileJava {
        options.release.set(8)
        options.encoding = "UTF-8"
    }
    shadowJar {
        manifest {
            attributes(
                "Main-Class" to "me.obsilabor.forgelauncher.ForgeLauncher"
            )
        }
    }
}