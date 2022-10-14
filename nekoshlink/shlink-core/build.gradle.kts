import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	id("org.springframework.boot")
	id("io.spring.dependency-management")
	kotlin("jvm")
	kotlin("plugin.spring")
	kotlin("plugin.jpa")
}

dependencies {

	api(project(":shlink-security"))

	api("org.jetbrains.kotlin:kotlin-reflect")
	api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	api("org.springframework.boot:spring-boot-starter-web")
	api("org.springframework.boot:spring-boot-starter-data-jpa")
	api("com.fasterxml.jackson.module:jackson-module-kotlin")

	api("org.nekosoft.utils:utils-common:main-SNAPSHOT")

	api("org.javers:javers-spring-boot-starter-sql:6.6.5")

	runtimeOnly("com.h2database:h2:2.1.214")
	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
	runtimeOnly("mysql:mysql-connector-java")
	runtimeOnly("com.microsoft.sqlserver:mssql-jdbc")

	api("com.maxmind.geoip2:geoip2:3.0.1")

	api("org.hashids:hashids:1.0.3")

	api("info.picocli:picocli:4.6.3")

	api("io.github.g0dkar:qrcode-kotlin-jvm:3.2.0")

	api("io.github.microutils:kotlin-logging-jvm:2.1.23")

	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")

}

tasks.withType<BootJar>().configureEach() {
	enabled = false
}

tasks.getByName<Jar>("jar") {
	enabled = true
}
