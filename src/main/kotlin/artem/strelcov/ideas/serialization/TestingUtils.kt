package artem.strelcov.ideas.serialization

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.reflections.Reflections
import java.lang.StringBuilder
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

object TestingUtils {

    @OptIn(InternalSerializationApi::class)
    private fun checkSerialization(packagePath: String) : List<SerializationErrorContainer> {

        return listOf<SerializationErrorContainer>().plus(
            Reflections(packagePath).getTypesAnnotatedWith(Serializable::class.java).mapNotNull {
                val kclass = Reflection.createKotlinClass(it)
                val ew = Json.decodeFromString(
                    kclass.serializer(), createStringRepresentationOfEntityGraph(
                        createEntityGraph(
                            kclass
                        )
                    )
                )
                if (Json.decodeFromString(kclass.serializer(), serialize(kclass, ew)) != ew)
                    SerializationErrorContainer("Failed to deserialize $kclass")
                else null
            }
        )
    }

    @OptIn(InternalSerializationApi::class)
    private inline fun <reified CapturedClassType: Any> serialize(clazz: KClass<CapturedClassType>, obj: Any) : String {
        return Json.encodeToString(clazz.serializer(), clazz.cast(obj))
    }

    data class SerializationErrorContainer(
        val errorMessage: String
    )

    /*private fun createDesiredObject(entityGraph: EntityGraph): Any {
        val desiredKClass = entityGraph.actualClass
        val primaryConstructor = desiredKClass.constructors.first()
        val constructorArguments = primaryConstructor.parameters.associateBy { it.name }
        return desiredKClass.constructors.first().callBy(
            entityGraph.innerEntities.map {
                val innerArgumentName = it.key.name
                val innerArgumentKClass = it.value.first
                when(innerArgumentKClass){
                    EntityGraph::class -> constructorArguments[innerArgumentName]!! to createDesiredObject(it.value.second as EntityGraph)
                    else -> constructorArguments[innerArgumentName]!! to it.value.second
                }
            }.toMap()
        )
    }*/

    //"""{"str":"3","number":3,"customObject":{"attributes":4}}"""
    //{"number":3,"str":"eqw","innerDto":{"number":3,"assen":"ewqr"}}
    fun createStringRepresentationOfEntityGraph(graph: EntityGraph) : String{
        val resultStringRepresentation = StringBuilder()
        resultStringRepresentation.append("{")
        /*graph.innerEntities.forEach {
            val str = createString(it)
            resultStringRepresentation.append(str)
        }*/
        return resultStringRepresentation.append(graph.innerEntities.map {
            when(it.value){
                is Primitive -> createString(it)
                is EntityGraph -> createEntityGraphString(it.key.name, it.value as EntityGraph)
            }
        }.joinToString(",").plus("}")).toString()

    }

    private fun createEntityGraphString(graphName: String, graph: EntityGraph) : String {
        return "\"$graphName\":{".plus(
            graph.innerEntities.map {
                when(it.value){
                    is Primitive -> createString(it)
                    is EntityGraph -> createEntityGraphString(it.key.name, it.value as EntityGraph)
                }
            }
        ).replace("[","").replace("]","").plus("}")

    }

    private fun createString(innerEntity: Map.Entry<EntityName, Entity>) : String {
        return "\"${innerEntity.key.name}\":${innerEntity.value}"
    }
    fun createEntityGraph(kClass: KClass<*>) : EntityGraph {
        val graph = EntityGraph(kClass)
        kClass.memberProperties.forEach { kProperty ->
            val propertyKClass = kProperty.returnType.jvmErasure
            val propertyName = EntityName(kProperty.name)
            if(propertyKClass.isPrimitive()){
                graph.innerEntities[propertyName] = Primitive(propertyKClass, provideValueForPrimitive(propertyKClass))
            }
            else {
                val childGraph = createEntityGraph(propertyKClass)
                graph.innerEntities[propertyName] = childGraph
            }
        }
        return graph
    }

    private fun KClass<*>.isPrimitive() : Boolean {
        return primitiveTypeValues.containsKey(this)
    }

    private fun provideValueForPrimitive(primitiveClass: KClass<*>) : Any{
        return when(primitiveClass) {
            Int::class -> Int.MAX_VALUE
            String::class -> "SomeString"
            else -> error("Such primitive is not exist")
        }
    }

    private val primitiveTypeValues = mapOf(
        Int::class to Int.MAX_VALUE,
        String::class to "ejqwrjq",
        Integer::class to Integer.MAX_VALUE
    )

}

data class PrimitiveTestClass(
    val number: Int,
    val str: String
)

data class EntityGraph(
    override val actualClass: KClass<*>,
    val innerEntities: MutableMap<EntityName, Entity> =  LinkedHashMap()
) : Entity
sealed interface Entity{
    abstract val actualClass: KClass<*>
}

data class Primitive(
    override val actualClass: KClass<*>,
    val value: Any
) : Entity {
    override fun toString(): String {
        return when(value){
            is String -> "\"$value\""
            else -> "$value"
        }
    }
}

data class EntityName(val name: String)