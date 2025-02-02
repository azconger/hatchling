package lol.conger.hatchling

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ThingController {

    @DeleteMapping("/api/v1/thing")
    fun deleteThing(@RequestParam(required = false) thingId: String?): ResponseEntity<String> {
        return if (thingId != null) {
            ResponseEntity.ok("Thing $thingId deleted")
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

}
