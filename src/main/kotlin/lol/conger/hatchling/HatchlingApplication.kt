package lol.conger.hatchling

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@OpenAPIDefinition(
	info = Info(
		title = "Hatchling API",
		version = "1.0",
		description = "A knockabout API built with Spring Boot and Kotlin."
	)
)

@SpringBootApplication
class HatchlingApplication

fun main(args: Array<String>) {
	runApplication<HatchlingApplication>(*args)
}
