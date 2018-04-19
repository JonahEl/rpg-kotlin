package weft.display

class Display(val widthInCharacters: Int = 80, val heightInCharacters: Int = 40) {
	private val cells: Array<Array<Glyph>>

	init {
		require(widthInCharacters > 1) { "width $widthInCharacters must be greater than 0." }
		require(heightInCharacters > 1) { "height $heightInCharacters must be greater than 0." }

		cells = Array(widthInCharacters) { Array(heightInCharacters) { Glyph('*') } }
	}

	fun all(resetDirtyFlag: Boolean = true): List<GlyphCell> {
		val changed = mutableListOf<GlyphCell>()
		for (x in 0 until widthInCharacters) {
			for (y in 0 until heightInCharacters) {
				val cell = cells[x][y]
				changed.add(GlyphCell(x, y, cell.copy()))
				if (resetDirtyFlag)
					cell.dirty = false
			}
		}
		return changed
	}

	fun changes(resetDirtyFlag: Boolean = true): List<GlyphCell> {
		val changed = mutableListOf<GlyphCell>()
		for (x in 0 until widthInCharacters) {
			for (y in 0 until heightInCharacters) {
				val cell = cells[x][y]
				if (!cell.dirty)
					continue

				changed.add(GlyphCell(x, y, cell.copy()))

				if (resetDirtyFlag)
					cell.dirty = false
			}
		}

		return changed
	}

	fun clear(cell: Glyph): Boolean {
		return clear(cell, 0, 0, widthInCharacters, heightInCharacters)
	}

	private fun clear(cell: Glyph, x: Int, y: Int, width: Int, height: Int): Boolean {
		require(x in 0..(widthInCharacters - 1)) { "x $x must be within range [0,$widthInCharacters)" }
		require(y in 0..(heightInCharacters - 1)) { "y $y must be within range [0,$heightInCharacters)" }
		require(width > 0) { "width $width must be greater than 0." }
		require(height > 0) { "height $height must be greater than 0." }
		require(x + width <= widthInCharacters) { "x + width " + (x + width) + " must be less than " + (widthInCharacters + 1) + "." }
		require(y + height <= heightInCharacters) { "y + height " + (y + height) + " must be less than " + (heightInCharacters + 1) + "." }

		var changed = false
		for (xo in x until x + width) {
			for (yo in y until y + height) {
				if (write(cell, xo, yo))
					changed = true
			}
		}

		return changed
	}

	fun write(glyph: Glyph, x: Int, y: Int): Boolean {
		require(x in 0..(widthInCharacters - 1)) { "x $x must be within range [0,$widthInCharacters)" }
		require(y in 0..(heightInCharacters - 1)) { "y $y must be within range [0,$heightInCharacters)" }

		val g = glyph.copy()
		return if (g != cells[x][y]) {
			g.dirty = true
			cells[x][y] = g
			true
		} else
			false
	}

	fun write(string: String, x: Int, y: Int): Boolean {
		return write(GlyphString(string), x, y)
	}

	private fun write(string: GlyphString, x: Int, y: Int): Boolean {
		require(x + string.length < widthInCharacters) { "x + string.length() " + (x + string.length) + " must be less than " + widthInCharacters + "." }
		require(x in 0..(widthInCharacters - 1)) { "x $x must be within range [0,$widthInCharacters)" }
		require(y in 0..(heightInCharacters - 1)) { "y $y must be within range [0,$heightInCharacters)" }

		var changed = false
		for (i in 0 until string.length) {
			if (write(string[i], x + i, y))
				changed = true
		}
		return changed
	}

	fun writeCenter(string: String, y: Int): Boolean {
		return writeCenter(GlyphString(string), y)
	}

	private fun writeCenter(string: GlyphString, y: Int): Boolean {
		require(string.length < widthInCharacters) { "string.length() " + string.length + " must be less than " + widthInCharacters + "." }

		val x = (widthInCharacters - string.length) / 2
		require(x in 0..(widthInCharacters - 1)) { "x $x must be within range [0,$widthInCharacters)" }
		require(y in 0..(heightInCharacters - 1)) { "y $y must be within range [0,$heightInCharacters)" }

		var changed = false
		for (i in 0 until string.length) {
			if (write(string[i], x + i, y))
				changed = true
		}
		return changed
	}
}