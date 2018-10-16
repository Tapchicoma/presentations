--

Make you tests better with spek framework.

https://spekframework.org/

--

What is spek?

--

Small history of spek creation

--

How to add it?

Minimal supported Gradle version - `4.7`

Add:

```gradle
dependencies {
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:<spek_version>")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:<spek_version>")
}
```

---

Define:

``` gradle
test {
    useJUnitPlatform {
        includeEngines 'spek2'
    }
}
```

---

Install plugin: https://plugins.jetbrains.com/plugin/10915-spek-framework

--

Core concepts

--

Test Styles

--

Why it is better then Junit 5?

--

Questions?

--
