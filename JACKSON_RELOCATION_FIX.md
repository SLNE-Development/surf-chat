# Jackson Relocation Conflict - Root Cause Analysis

## Problem Summary

After adding the OpenAI Java client dependency, Spring Boot starts successfully but fails to register any user-defined beans (`@Service`, `@Component`, etc.) from packages like `dev.slne.surf.chat.paper.*`. Only Spring's auto-configuration beans are registered, leading to `NoSuchBeanDefinitionException` when attempting to inject user beans like `ChannelService`.

## Root Cause: Jackson Relocation Conflict

The issue occurs due to a **package relocation conflict** in the ShadowJar build process:

### The Conflict Chain

1. **Jackson Relocation** (`surf-chat-paper/build.gradle.kts`):
   ```kotlin
   tasks.shadowJar {
       relocate("com.fasterxml.jackson", "dev.slne.surf.chat.shadow.jackson")
   }
   ```
   This moves all Jackson classes from `com.fasterxml.jackson.*` to `dev.slne.surf.chat.shadow.jackson.*`

2. **OpenAI Client Dependency** (`surf-chat-core-common/build.gradle.kts`):
   ```kotlin
   implementation("com.openai:openai-java:4.11.0")
   ```
   The OpenAI client depends on Jackson for JSON serialization/deserialization

3. **The Problem**:
   - OpenAI client classes are compiled with bytecode references to `com.fasterxml.jackson.*`
   - ShadowJar relocates Jackson classes to `dev.slne.surf.chat.shadow.jackson.*`
   - OpenAI client tries to load Jackson classes from original package
   - **Jackson classes not found at original location** → `NoClassDefFoundError`

4. **Silent Failure During Component Scanning**:
   - Spring tries to scan and register beans (like `OpenAiService`, `ChannelService`, etc.)
   - These beans transitively depend on OpenAI client classes
   - Loading these classes triggers `NoClassDefFoundError` for Jackson
   - Spring's component scanner **gracefully handles this** by:
     - Logging at DEBUG level
     - Skipping the problematic bean
     - Continuing with other beans
   - No exception is thrown during startup!

5. **Only Auto-Configuration Beans Load**:
   - Spring's auto-configuration beans don't depend on OpenAI client
   - They load successfully using the relocated Jackson
   - User beans that depend (directly or transitively) on OpenAI are skipped

## Why No Visible Exception?

Spring's component scanning is designed to be resilient:

```java
// Pseudo-code of Spring's scanning behavior
try {
    Class<?> beanClass = classLoader.loadClass(candidateClassName);
    // Register bean...
} catch (Throwable ex) {
    logger.debug("Failed to load bean class: " + candidateClassName, ex);
    // Continue scanning other classes
}
```

- `NoClassDefFoundError` is caught as `Throwable`
- Spring logs at DEBUG level (not visible by default)
- ApplicationContext initialization completes successfully
- Exceptions only appear when code tries to inject the missing beans

## Debugging Evidence

From the user's log output:

```
[22:22:22 INFO]: Find class in this classloader: class dev.slne.surf.chat.paper.channel.ChannelService
[22:22:22 INFO]: Find class in context loader: class dev.slne.surf.chat.paper.channel.ChannelService
```

The class **can** be loaded by the classloader, proving it's not a classpath issue.

```
[22:22:22 INFO]: Bean names: org.springframework.context.annotation.internalConfigurationAnnotationProcessor, 
org.springframework.context.annotation.internalAutowiredAnnotationProcessor, ..., jacksonObjectMapper, ...
```

Only Spring internal and auto-configuration beans are registered, **no user beans**.

```
org.springframework.beans.factory.NoSuchBeanDefinitionException: No bean named 'channelService' available
```

Bean injection fails at runtime, not during startup.

## The Fix

Exclude Jackson from the OpenAI client dependency, allowing it to use the Jackson provided by `surf-cloud`:

```kotlin
dependencies {
    api(project(":surf-chat-api"))
    implementation("com.openai:openai-java:4.11.0") {
        exclude(group = "org.apache.httpcomponents.client5")
        exclude(group = "org.apache.httpcomponents.core5")
        exclude(group = "org.jetbrains.kotlin")
        // Exclude Jackson to avoid conflicts with relocated Jackson in shadow JAR
        // surf-cloud provides Jackson, so OpenAI client will use that version
        exclude(group = "com.fasterxml.jackson.core")
        exclude(group = "com.fasterxml.jackson.databind")
        exclude(group = "com.fasterxml.jackson.datatype")
        exclude(group = "com.fasterxml.jackson.module")
    }
}
```

### Why This Works

1. OpenAI client no longer bundles its own Jackson JAR
2. At runtime, it uses the Jackson provided by `surf-cloud`
3. `surf-cloud`'s Jackson gets relocated by ShadowJar
4. OpenAI client classes resolve Jackson classes via classloader delegation
5. Classloader finds Jackson at relocated package: `dev.slne.surf.chat.shadow.jackson.*`
6. All classes use the same (relocated) Jackson → No `NoClassDefFoundError`
7. Spring component scanning succeeds for all beans

## Similar Issues and Prevention

### Other Libraries That May Cause Relocation Conflicts

Any dependency that uses relocated packages could have this issue:
- Jackson (JSON processing)
- Guava (utilities)
- ASM (bytecode manipulation)
- Netty (networking)
- OkHttp (HTTP client)

### Best Practices

1. **Check Transitive Dependencies Before Relocating**:
   ```bash
   ./gradlew :surf-chat-paper:dependencies --configuration runtimeClasspath | grep jackson
   ```

2. **Exclude Relocated Dependencies From All Dependents**:
   If you relocate a package, ensure all dependencies that use it are excluded and use your provided version.

3. **Test Component Scanning**:
   ```kotlin
   @Test
   fun `verify all expected beans are registered`() {
       val context = SpringApplication.run(ChatApplication::class.java)
       assertNotNull(context.getBean(ChannelService::class.java))
       assertNotNull(context.getBean(OpenAiService::class.java))
   }
   ```

4. **Enable Debug Logging During Development**:
   ```yaml
   logging:
     level:
       org.springframework.context.annotation: DEBUG
       org.springframework.beans.factory.support: DEBUG
   ```

5. **Use Shadow Plugin's `dependencies` Block**:
   Instead of manual exclusions, let ShadowJar handle it:
   ```kotlin
   tasks.shadowJar {
       relocate("com.fasterxml.jackson", "dev.slne.surf.chat.shadow.jackson")
       
       // Automatically handle relocated dependencies
       dependencies {
           exclude(dependency("com.fasterxml.jackson.core:.*"))
       }
   }
   ```

## Debugging Steps for Similar Issues

If you encounter Spring bean registration issues after adding a new dependency:

1. **Check for Package Relocations**:
   ```bash
   grep -r "relocate" --include="*.gradle.kts"
   ```

2. **Check New Dependency's Transitives**:
   ```bash
   ./gradlew :module:dependencies --configuration runtimeClasspath
   ```

3. **Look for Overlap**:
   Compare relocated packages with transitive dependencies

4. **Enable Spring Debug Logging**:
   Set `logging.level.org.springframework.context.annotation=DEBUG`

5. **Check Shadow JAR Contents**:
   ```bash
   unzip -l build/libs/*.jar | grep -E "(relocated-package|original-package)"
   ```

6. **Test Class Loading**:
   ```kotlin
   val clazz = Class.forName("com.openai.client.OpenAIClient")
   println("Class loader: ${clazz.classLoader}")
   clazz.getDeclaredMethods().forEach { method ->
       method.parameterTypes.forEach { param ->
           println("Parameter type: $param from ${param.classLoader}")
       }
   }
   ```

## Key Takeaway

**Package relocation in ShadowJar must be coordinated with dependency exclusions**. When you relocate a package:

1. Identify all dependencies that use the relocated package
2. Exclude the original package from those dependencies  
3. Ensure the relocated version is provided and accessible
4. Test that component scanning and classloading work correctly

This issue demonstrates that **library conflicts can cause silent failures** in Spring Boot when they occur during component scanning rather than at application startup.
