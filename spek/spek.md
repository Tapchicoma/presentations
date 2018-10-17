class: center, middle

# A test framework for Kotlin

https://spekframework.org/

---

# What is spek?

---

# History of spek creation

Possibly not needed...

---

# How to add it?

Minimal supported Gradle version - `4.7`

---

# How to add it?

Add dependencies to `build.gradle`:

```gradle
ext.spek = <spek_version>

dependencies {
  testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spek")
  testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spek")
}
```

---

# How to add it?

Use `spek2` engine for tests:

```gradle
test {
    useJUnitPlatform {
        includeEngines 'spek2'
    }
}
```

---

# How to add it?

Optionally install plugin:

https://plugins.jetbrains.com/plugin/10915-spek-framework

---

# Core concepts

---

# Test Styles

---

# Why it is better then Junit 5?

---

# Questions?
