plugins {
    id("dev.slne.surf.surfapi.gradle.core")
}

surfCoreApi {
    withCloudCommon()
}

dependencies {
    api(project(":surf-chat-api"))
    implementation("com.openai:openai-java:4.11.0") {
        exclude(group = "org.apache.httpcomponents.client5")
        exclude(group = "org.apache.httpcomponents.core5")
        exclude(group = "org.jetbrains.kotlin")
        // Exclude Jackson to avoid conflicts with relocated Jackson in shadow JAR
        // Spring Cloud provides Jackson, so OpenAI client will use that version
        exclude(group = "com.fasterxml.jackson.core")
        exclude(group = "com.fasterxml.jackson.databind")
        exclude(group = "com.fasterxml.jackson.datatype")
        exclude(group = "com.fasterxml.jackson.module")
    }
}