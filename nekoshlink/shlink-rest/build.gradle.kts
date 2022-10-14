import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	id("org.springframework.boot")
	id("io.spring.dependency-management")
	kotlin("jvm")
	kotlin("plugin.spring")
}

dependencies {

	implementation(project(":shlink-core"))
	implementation("org.nekosoft.utils:crawler-detect:1.0.0")
	api("org.springframework.boot:spring-boot-starter-actuator")

	developmentOnly("org.springframework.boot:spring-boot-devtools")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")

}

tasks.withType<BootJar>().configureEach() {
	launchScript()
	mainClass.set("org.nekosoft.shlink.rest.ShlinkRestApiServerKt")
}

tasks.getByName<Jar>("jar") {
	enabled = false
}
