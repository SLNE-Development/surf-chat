# Spring Bean Registration Fix - Root Cause Analysis

## Problem Summary

Spring Boot successfully starts but fails to register any user-defined beans (`@Service`, `@Component`, etc.) from packages like `dev.slne.*`. Only Spring's auto-configuration beans are present in the ApplicationContext, leading to `NoSuchBeanDefinitionException` when attempting to inject user beans.

## Root Cause

The issue was caused by an overly broad exclusion pattern in the ShadowJar configuration:

```kotlin
tasks {
    withType<ShadowJar>() {
        exclude("kotlin/**")  // ❌ TOO BROAD - excludes metadata too!
    }
}
```

### Why This Breaks Spring Component Scanning

1. **Kotlin Metadata Files**: When Kotlin classes are compiled, the compiler generates `.kotlin_module` metadata files that contain information about the classes, their annotations, and other metadata.

2. **Storage Location**: These metadata files are stored in the JAR under paths like:
   - `META-INF/<module-name>.kotlin_module`
   - These files are technically under paths matching `kotlin/**` patterns

3. **Spring's Dependency**: Spring Boot's component scanning for Kotlin classes relies on these `.kotlin_module` files to:
   - Discover classes and their annotations (`@Service`, `@Component`, etc.)
   - Properly handle Kotlin-specific features (data classes, companion objects, etc.)
   - Map Kotlin types to Spring's type system

4. **Silent Failure**: When the metadata is missing:
   - Spring's component scanner silently skips Kotlin classes
   - No exception is thrown (graceful degradation)
   - Auto-configuration beans still work (they don't rely on `.kotlin_module` files)
   - User beans are simply not discovered

## Why No Visible Exception?

Spring's component scanning is designed to be resilient:
- If it encounters a class it can't properly scan, it logs a debug message and continues
- Missing Kotlin metadata is treated as "class not scannable" rather than an error
- The application context still initializes successfully with just the beans it could discover
- Exceptions only occur later when trying to inject the missing beans

## Debugging Steps

If you encounter this issue in the future, here's how to diagnose it:

### 1. Verify Shadow JAR Contents

```bash
# Extract and inspect the shadow JAR
unzip -l build/libs/your-plugin.jar | grep kotlin_module

# You should see entries like:
# META-INF/surf-chat-paper.kotlin_module
# META-INF/surf-chat-core-common.kotlin_module
```

If these files are missing, your ShadowJar configuration is excluding them.

### 2. Enable Spring Debug Logging

Add to your application configuration:
```yaml
logging:
  level:
    org.springframework.context.annotation: DEBUG
    org.springframework.core.type: DEBUG
```

Look for messages about component scanning - if Kotlin classes are being skipped, you'll see it here.

### 3. Check ClassLoader

```kotlin
// Verify class can be loaded
val clazz = Class.forName("dev.slne.surf.chat.paper.channel.ChannelService")
println("Class loaded: ${clazz.name}")
println("Annotations: ${clazz.annotations.joinToString()}")
println("ClassLoader: ${clazz.classLoader}")
```

If the class loads but annotations aren't visible, it's likely a metadata issue.

### 4. Inspect Kotlin Metadata

```kotlin
@Service
class ChannelService {
    init {
        // This should only print if Spring instantiates the bean
        println("ChannelService initialized!")
    }
}
```

If the init block never executes, the bean wasn't created.

## The Fix

Replace the broad exclusion with specific patterns:

```kotlin
tasks {
    withType<ShadowJar>() {
        // Exclude Kotlin stdlib classes but preserve Kotlin metadata files (.kotlin_module)
        // that Spring Boot needs for component scanning of Kotlin classes
        exclude("kotlin/**/*.class")
        exclude("kotlin/**/*.kotlin_builtins")
        // Do NOT exclude META-INF/*.kotlin_module - Spring needs these for scanning!
    }
}
```

This approach:
- ✅ Excludes Kotlin stdlib `.class` files (avoiding duplication)
- ✅ Excludes Kotlin built-in definitions (not needed at runtime)
- ✅ Preserves `.kotlin_module` metadata files (critical for Spring)

## Similar Issues to Watch For

### 1. ServiceLoader Files

ShadowJar can also accidentally exclude:
- `META-INF/services/*` (Java ServiceLoader)
- `META-INF/spring.factories` (Spring Boot auto-configuration)
- `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` (Spring Boot 3.x)

### 2. Relocations

When relocating packages, ensure metadata files are also relocated:

```kotlin
tasks.shadowJar {
    relocate("com.fasterxml.jackson", "dev.slne.surf.chat.shadow.jackson") {
        // Metadata files will be automatically relocated
    }
}
```

### 3. Merging Strategies

Multiple `.kotlin_module` files with the same name need proper merge strategy:

```kotlin
tasks.shadowJar {
    mergeServiceFiles() // Handles META-INF/services
    append("META-INF/*.kotlin_module") // Append rather than overwrite
}
```

## Testing the Fix

To verify the fix works:

1. **Build the shadow JAR**:
   ```bash
   ./gradlew shadowJar
   ```

2. **Verify metadata is present**:
   ```bash
   unzip -l build/libs/*.jar | grep kotlin_module
   ```

3. **Check for user beans at runtime**:
   ```kotlin
   val context = SpringApplication.run(ChatApplication::class.java)
   val channelService = context.getBean(ChannelService::class.java)
   println("Bean found: $channelService")
   ```

4. **Confirm no duplicate Kotlin stdlib**:
   ```bash
   # Should NOT see kotlin-stdlib classes
   unzip -l build/libs/*.jar | grep "kotlin/collections" | head -5
   ```

## References

- [Kotlin Metadata and Reflection](https://kotlinlang.org/docs/reflection.html)
- [Spring Framework Kotlin Support](https://docs.spring.io/spring-framework/reference/languages/kotlin.html)
- [Shadow Plugin Documentation](https://imperceptiblethoughts.com/shadow/)
- [Spring Boot Component Scanning](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.developing-auto-configuration)

## Prevention

To avoid this issue in future projects:

1. **Be specific with exclusions** - Never use broad patterns like `exclude("kotlin/**")`
2. **Test shadow JARs** - Always verify metadata files are present after shadow JAR creation
3. **Use assertions** - Add a test that verifies critical beans can be injected
4. **Document patterns** - If using custom exclusion patterns, document why each pattern is needed

## Summary

**Problem**: `exclude("kotlin/**")` removed `.kotlin_module` metadata files needed for Spring's component scanning of Kotlin classes.

**Solution**: Use specific patterns like `exclude("kotlin/**/*.class")` to exclude only the class files while preserving metadata.

**Key Insight**: Spring silently skips classes without proper metadata rather than throwing exceptions, making this issue difficult to diagnose without understanding the relationship between Kotlin metadata and Spring's component scanning.
