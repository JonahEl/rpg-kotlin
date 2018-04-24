package rpg.common.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule
import io.vertx.core.json.Json
import java.awt.Color


sealed class ColorSerialization {
	class ColorSerializer : JsonSerializer<Color>() {
		override fun serialize(value: Color?, gen: JsonGenerator?, provider: SerializerProvider?) {
			if (gen == null)
				return

			gen.writeStartObject()
			gen.writeNumberField("red", value?.red ?: 0)
			gen.writeNumberField("green", value?.green ?: 0)
			gen.writeNumberField("blue", value?.blue ?: 0)
			gen.writeEndObject()
		}
	}

	class ColorDeserializer : JsonDeserializer<Color>() {
		override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Color {
			assert(p != null) { "JsonParser shouldn't be null" }
			val node = p?.codec?.readTree<JsonNode>(p) ?: error("JsonNode shouldn't be null")
			assert(!node.has("red")) { "JsonNode missing red" }
			assert(!node.has("green")) { "JsonNode missing green" }
			assert(!node.has("blue")) { "JsonNode missing blue" }

			return Color(node.get("red").asInt(0), node.get("green").asInt(0), node.get("blue").asInt(0))
		}
	}

	companion object {
		fun register() {
			val mod = SimpleModule(" ${ColorSerialization::javaClass.name}Module")
			mod.addSerializer(Color::class.java, ColorSerializer())
			mod.addDeserializer(Color::class.java, ColorDeserializer())
			Json.mapper.registerModule(mod)
			Json.prettyMapper.registerModule(mod)
		}
	}
}

