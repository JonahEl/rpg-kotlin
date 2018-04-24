//package rpg.common
//
//import io.vertx.core.buffer.Buffer
//import io.vertx.core.eventbus.EventBus
//import io.vertx.core.eventbus.MessageCodec
//import io.vertx.core.json.Json
//import rpg.common.display.GlyphCell
//
//sealed class MessageCodecs {
//	class ArrayListCodec<T> : MessageCodec<ArrayList<T>, ArrayList<T>> {
//		override fun encodeToWire(buffer: Buffer, msg: ArrayList<T>) {
//			encode(buffer, msg, { um -> Json.encode(um) })
//		}
//
//		override fun decodeFromWire(position: Int, buffer: Buffer): ArrayList<T> {
//			return decode(position, buffer, { json -> Json.decodeValue(json, ArrayList<T>().javaClass) })
//		}
//
//		override fun transform(msg: ArrayList<T>): ArrayList<T> {
//			return msg
//		}
//
//		override fun name(): String {
//			return this.javaClass.simpleName
//		}
//
//		override fun systemCodecID(): Byte {
//			return -1 // Always -1
//		}
//	}
//
//	class GlyphCellCodec : MessageCodec<GlyphCell, GlyphCell> {
//		override fun encodeToWire(buffer: Buffer, msg: GlyphCell) {
//			encode(buffer, msg, { um -> Json.encode(um) })
//		}
//
//		override fun decodeFromWire(position: Int, buffer: Buffer): GlyphCell {
//			return decode(position, buffer, { json -> Json.decodeValue(json, GlyphCell::class.java) })
//		}
//
//		override fun transform(msg: GlyphCell): GlyphCell {
//			return msg
//		}
//
//		override fun name(): String {
//			return this.javaClass.simpleName
//		}
//
//		override fun systemCodecID(): Byte {
//			return -1 // Always -1
//		}
//	}
//
//	class UpdateMessageCodec : MessageCodec<Messages.Update, Messages.Update> {
//		override fun encodeToWire(buffer: Buffer, msg: Messages.Update) {
//			encode(buffer, msg, { um: Messages.Update -> Json.encode(um) })
//		}
//
//		override fun decodeFromWire(position: Int, buffer: Buffer): Messages.Update {
//			return decode(position, buffer, { json -> Json.decodeValue(json, Messages.Update::class.java) })
//		}
//
//		override fun transform(msg: Messages.Update): Messages.Update {
//			return msg
//		}
//
//		override fun name(): String {
//			return this.javaClass.simpleName
//		}
//
//		override fun systemCodecID(): Byte {
//			return -1 // Always -1
//		}
//	}
//
//	class KeyPressMessageCodec : MessageCodec<Messages.KeyPress, Messages.KeyPress> {
//		override fun encodeToWire(buffer: Buffer, msg: Messages.KeyPress) {
//			encode(buffer, msg, { um -> Json.encode(um) })
//		}
//
//		override fun decodeFromWire(position: Int, buffer: Buffer): Messages.KeyPress {
//			return decode(position, buffer, { json -> Json.decodeValue(json, Messages.KeyPress::class.java) })
//		}
//
//		override fun transform(msg: Messages.KeyPress): Messages.KeyPress {
//			return msg
//		}
//
//		override fun name(): String {
//			return this.javaClass.simpleName
//		}
//
//		override fun systemCodecID(): Byte {
//			return -1 // Always -1
//		}
//	}
//
//
//	companion object {
//		fun register(eventBus: EventBus) {
//			eventBus.registerDefaultCodec(GlyphCell::class.java, GlyphCellCodec())
//			eventBus.registerDefaultCodec(ArrayList<GlyphCell>().javaClass, ArrayListCodec())
//			eventBus.registerDefaultCodec(Messages.Update::class.java, UpdateMessageCodec())
//			eventBus.registerDefaultCodec(Messages.KeyPress::class.java, KeyPressMessageCodec())
//		}
//
//		fun <T : Any> encode(buffer: Buffer, msg: T, encode: (msg: T) -> String) {
//
//			val str = encode(msg)
//			// Length of JSON: is NOT characters count
//			val length = str.toByteArray().size
//
//			// Write data into given buffer
//			buffer.appendInt(length)
//			buffer.appendString(str)
//		}
//
//		fun <T : Any> decode(position: Int, buffer: Buffer, decode: (json: String) -> T): T {
//			// Length of JSON
//			val length = buffer.getInt(position)
//
//			// Get JSON string by it`s length
//			// Jump 4 because getInt() == 4 bytes
//			return decode(buffer.getString(position + 4, position + 4 + length))
//		}
//	}
//}