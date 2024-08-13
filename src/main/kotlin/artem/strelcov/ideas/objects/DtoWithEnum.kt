package artem.strelcov.ideas.objects

import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Serializable
data class DtoWithEnum(
    val enum: Enum
)

enum class Enum(val description: String){
    FIRST("first"), SECOND("second")
}