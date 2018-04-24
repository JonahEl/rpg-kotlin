package rpg.common.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule
import io.vertx.core.json.Json
import java.time.Instant


sealed class InstantSerialization {
	class InstantSerializer : JsonSerializer<Instant>() {
		override fun serialize(value: Instant?, gen: JsonGenerator?, provider: SerializerProvider?) {
			if (gen == null)
				return

			gen.writeStartObject()
			gen.writeNumberField("epoch", value?.toEpochMilli() ?: 0)
			gen.writeEndObject()
		}
	}

	class InstantDeserializer : JsonDeserializer<Instant>() {
		override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Instant {
			assert(p != null) { "JsonParser shouldn't be null" }
			val node = p?.codec?.readTree<JsonNode>(p) ?: error("JsonNode shouldn't be null")
			assert(!node.has("epoch")) { "JsonNode missing epoch" }

			return Instant.ofEpochMilli(node.get("epoch").asLong(0))
		}
	}

	companion object {
		fun register() {
			val mod = SimpleModule(" ${InstantSerialization::javaClass.name}Module")
			mod.addSerializer(Instant::class.java, InstantSerializer())
			mod.addDeserializer(Instant::class.java, InstantDeserializer())
			Json.mapper.registerModule(mod)
			Json.prettyMapper.registerModule(mod)
		}
	}
}

