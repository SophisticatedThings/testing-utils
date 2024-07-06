package artem.strelcov.ideas.objects

import kotlinx.serialization.Serializable


@Serializable
data class Dto(
    val number: Int,
    val str: String,
    val innerDto: InnerDto
)

@Serializable
data class InnerDto(
    val number: Int,
    val assen: String
)

@Serializable
data class CustomObject(
    val attributes: Int
)