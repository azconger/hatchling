package lol.conger.hatchling

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HatchlingApplication

fun main(args: Array<String>) {
	runApplication<HatchlingApplication>(*args)
}
