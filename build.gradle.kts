plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.11.0"
    id("org.jetbrains.grammarkit") version "2022.3.2.1"
}

group = properties["pluginGroup"]!!
version = properties["pluginVersion"]!!

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

// Configure Java to match IntelliJ requirements
// Build with Java 21, but compile to Java 17 bytecode for compatibility with IntelliJ 2023.2
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    intellijPlatform {
        create(properties["platformType"] as String, properties["platformVersion"] as String)

        // Required dependencies
        instrumentationTools()
    }

    // Test dependencies
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.1")
}

// Configure IntelliJ Platform
intellijPlatform {
    buildSearchableOptions = false

    pluginConfiguration {
        version = properties["pluginVersion"] as String

        ideaVersion {
            sinceBuild = "232"
            untilBuild = "242.*"
        }
    }
}

// Configure Grammar-Kit
sourceSets {
    main {
        java {
            srcDirs("src/main/java", "src/main/gen")
        }
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    test {
        useJUnitPlatform()
    }

    generateLexer {
        sourceFile.set(file("src/main/java/ch/erzberger/sharpbasic/lexer/SharpBasic.flex"))
        targetDir.set("src/main/gen/ch/erzberger/sharpbasic/lexer")
        targetClass.set("SharpBasicLexer")
        purgeOldFiles.set(true)
    }

    generateParser {
        sourceFile.set(file("src/main/java/ch/erzberger/sharpbasic/parser/SharpBasic.bnf"))
        targetRoot.set("src/main/gen")
        pathToParser.set("/ch/erzberger/sharpbasic/parser/SharpBasicParser.java")
        pathToPsiRoot.set("/ch/erzberger/sharpbasic/psi")
        purgeOldFiles.set(true)
    }

    withType<JavaCompile> {
        dependsOn(generateLexer, generateParser)
    }
}
