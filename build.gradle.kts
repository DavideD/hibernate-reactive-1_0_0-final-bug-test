import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  application
  id("com.github.johnrengelman.shadow") version "7.1.0"
  id("io.freefair.lombok") version "6.2.0"
}

group = "com.ukonnra.wonderland"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "4.1.5"
val junitJupiterVersion = "5.7.0"

val mainVerticleName = "io.vertx.hibernate.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClass.set(launcherClassName)
}

object Versions {
  const val HIBERNATE = "5.6.0.Final"
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation(platform("org.apache.logging.log4j:log4j-bom:2.14.1"))

//  implementation("org.hibernate.reactive:hibernate-reactive-core:1.0.0.CR10")
  implementation("org.hibernate.reactive:hibernate-reactive-core:1.0.0.Final")
  annotationProcessor("org.hibernate:hibernate-jpamodelgen:${Versions.HIBERNATE}")

  implementation("io.vertx:vertx-pg-client")
  implementation("io.vertx:vertx-rx-java3")
  runtimeOnly ("com.ongres.scram:client:2.1")

  implementation("org.apache.logging.log4j:log4j-api")
  implementation("org.apache.logging.log4j:log4j-core")
  implementation("org.apache.logging.log4j:log4j-slf4j-impl")

  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  args = listOf("run", mainVerticleName, "--redeploy=$watchForChange", "--launcher-class=$launcherClassName", "--on-redeploy=$doOnChange")
}
