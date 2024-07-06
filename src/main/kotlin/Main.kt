import artem.strelcov.ideas.objects.CustomObject
import artem.strelcov.ideas.objects.Dto
import artem.strelcov.ideas.objects.InnerDto
import artem.strelcov.ideas.serialization.GeneralSerializator
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun main(args: Array<String>) {

    GeneralSerializator().checkSerialization("artem.strelcov.ideas")


}