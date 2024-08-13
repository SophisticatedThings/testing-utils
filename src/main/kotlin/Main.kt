import artem.strelcov.ideas.objects.*
import artem.strelcov.ideas.objects.Enum
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant

fun main(args: Array<String>) {
    val jacksonMapper = ObjectMapper()
    println(jacksonMapper.writeValueAsString(DtoWithInstant(Instant.now())))

}