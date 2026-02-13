plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.2"
    id("org.jetbrains.grammarkit") version "2022.3.2"
}

group = properties["pluginGroup"]!!
version = properties["pluginVersion"]!!

repositories {
    mavenCentral()
}

// Configure Java to match IntelliJ requirements
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

// Configure Gradle IntelliJ Plugin
intellij {
    version.set(properties["platformVersion"] as String)
    type.set(properties["platformType"] as String)
    plugins.set(listOf())
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
    patchPluginXml {
        version.set(properties["pluginVersion"] as String)
        sinceBuild.set("232")
        untilBuild.set("242.*")
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    buildSearchableOptions {
        enabled = false
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
