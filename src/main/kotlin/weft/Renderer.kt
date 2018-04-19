package weft

import weft.display.Glyph
import weft.display.GlyphCell
import java.awt.*
import java.awt.image.BufferedImage
import javax.swing.JPanel

class Renderer constructor(widthInCharacters: Int = 80, heightInCharacters: Int = 40) : JPanel() {

	private val offscreenBuffer: Image
	private val offscreenGraphics: Graphics

	private val charWidth = 9
	private val charHeight = 16

	private val mainFont = Font("Code 2003", Font.PLAIN, 13)
	//private val mainFont = Font("Courier New", Font.PLAIN, 13)

	private val glyphs: MutableMap<String, BufferedImage> = mutableMapOf()

	init {
		preferredSize = Dimension(charWidth * widthInCharacters, charHeight * heightInCharacters)
		offscreenBuffer = BufferedImage(preferredSize.width, preferredSize.height, BufferedImage.TYPE_INT_RGB)
		offscreenGraphics = offscreenBuffer.graphics
	}

	override fun update(g: Graphics) {
		paint(g)
	}

	override fun paint(g: Graphics?) {
		requireNotNull(g)
		g?.drawImage(offscreenBuffer, 0, 0, this)
	}

	fun applyChanges(changes: List<GlyphCell>) {
		changes.forEach { cell ->
			offscreenGraphics.drawImage(getGlyph(cell.glyph), cell.x * charWidth, cell.y * charHeight, null)
		}
	}

	private fun getGlyph(charData: Glyph): BufferedImage {
		val g = glyphs[charData.hashCode().toString()]
		if (g != null)
			return g

		val l = loadGlyph(charData)
		glyphs[charData.hashCode().toString()] = l
		return l
	}

	private fun loadGlyph(charData: Glyph): BufferedImage {
		val chars = Character.toChars(charData.character)
		//val chars = Character.toChars(0x26F5)

		val glyph = BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB)
		val g = glyph.graphics as Graphics2D
		g.font = mainFont

		g.color = charData.backgroundColor
		g.fillRect(0, 0, charWidth, charHeight)

		g.color = charData.foregroundColor

		val metrics = g.getFontMetrics(mainFont)
		// Determine the X coordinate for the text
		val x = 0 + (charWidth - metrics.stringWidth("X")) / 2
		// Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
		val y = 0 + (charHeight - metrics.height) / 2 + metrics.ascent

		g.drawChars(chars, 0, chars.size, x, y)
		//val bytes = text.array();
		//g.drawBytes(chars, 0, bytes.size, x, y)
		//g.drawString("\u26F5", x, y)

		return glyph
	}
}
