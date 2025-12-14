plugins {
    id("dev.slne.surf.surfapi.gradle.core")
}

surfCoreApi {
    withCloudCommon()
}

// Force consistent Jackson versions to prevent NoSuchMethodError
configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group.startsWith("com.fasterxml.jackson")) {
            // Use Jackson 2.18.2 which is required by OpenAI client
            // and compatible with jackson-module-kotlin's OptBoolean usage
            useVersion("2.18.2")
            because("OpenAI client requires Jackson 2.18+ for OptBoolean support in JsonProperty.isRequired()")
        }
    }
}

dependencies {
    api(project(":surf-chat-api"))
    implementation("com.openai:openai-java:4.11.0") {
        exclude(group = "org.apache.httpcomponents.client5")
        exclude(group = "org.apache.httpcomponents.core5")
        exclude(group = "org.jetbrains.kotlin")
    }
}