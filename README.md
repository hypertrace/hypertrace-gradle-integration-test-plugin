# Hypertrace Integration Test Plugin
###### org.hypertrace.integration-test-plugin
[![CircleCI](https://circleci.com/gh/hypertrace/hypertrace-gradle-integration-test-plugin.svg?style=svg)](https://circleci.com/gh/hypertrace/hypertrace-gradle-integration-test-plugin)

### Purpose
This plugin creates a source set, configurations and a task to support integration tests. It works off
the `java` plugin, and will only run after it has been applied.

### Example

```kotlin
plugins {
  id("org.hypertrace.integration-test-plugin") version "<version>"
}

dependencies {
  // Use JUnit 5 for testing (or whatever test lib you'd like - the plugin is agnostic)
  integrationTestImplementation("org.junit.jupiter:junit-jupiter:5.5.2")
}

tasks.integrationTest {
  // Because we used junit 5 above, we have to switch gradle over. This can be omitted with the default, junit 4.
  useJUnitPlatform()
}
```
