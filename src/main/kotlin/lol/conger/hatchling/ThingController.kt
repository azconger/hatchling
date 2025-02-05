package lol.conger.hatchling

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ThingController {

    @GetMapping("/api/v1/response/{responseId}")
    fun getResponse(@PathVariable responseId: String): ResponseEntity<String> {
        return ResponseEntity.ok("Response $responseId found")
    }
    @PostMapping("/api/v1/response/{responseId}")
    fun postResponse(@PathVariable responseId: String): ResponseEntity<String> {
        return ResponseEntity.ok("Response $responseId posted")
    }
    @DeleteMapping("/api/v1/response/{responseId}")
    fun deleteResponse(@PathVariable responseId: String): ResponseEntity<String> {
        return ResponseEntity.ok("Response $responseId deleted")
    }

    /*
    @GetMapping("/api/v1/thing")
    fun getThing(@RequestParam(required = false) thingId: String?): ResponseEntity<String> {
        return if (thingId != null) {
            ResponseEntity.ok("Thing $thingId found")
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("thingId is required")
        }
    }
    @PostMapping("/api/v1/thing")
    fun postThing(@RequestParam(required = false) thingId: String?): ResponseEntity<String> {
        return if (thingId != null) {
            ResponseEntity.ok("Thing $thingId posted")
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("thingId is required")
        }
    }
    @DeleteMapping("/api/v1/thing")
    fun deleteThing(@RequestParam(required = false) thingId: String?): ResponseEntity<String> {
        return if (thingId != null) {
            ResponseEntity.ok("Thing $thingId deleted")
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("thingId is required")
        }
    }
    */
}
