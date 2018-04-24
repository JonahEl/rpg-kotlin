package rpgserver.game.creatures

import rpg.common.display.Glyph
import rpgserver.game.map.TileLoc
import java.awt.Dimension
import java.awt.Point

class EntityList<T : Entity>(private val blocking: Boolean = false, private val tileBlockCheck: (loc: TileLoc) -> Boolean, override val size: Int = 0) : MutableCollection<T> {
	private val items: MutableList<T> = mutableListOf()
	private val toAdd: MutableList<T> = mutableListOf()
	private val toRemove: MutableList<T> = mutableListOf()

	fun render(viewport: Dimension, scroll: Point, draw: (Glyph, Point) -> Unit) {
		items.forEach { i -> i.render(viewport, scroll, draw) }
	}

	fun update() {
		if (toRemove.isNotEmpty()) {
			items.removeAll(toRemove)
			toRemove.clear()
		}

		items.forEach { i -> i.update() }

		if (toAdd.isNotEmpty()) {
			items.addAll(toAdd)
			toAdd.clear()
		}
	}

	fun at(loc: TileLoc): T? {
		return items.firstOrNull { i -> i.position == loc }
	}

	fun nearby(pos: TileLoc, range: Int): List<T> {
		return items.filter { c -> c.position.distance(pos) <= range }
	}

	fun occupied(loc: TileLoc): Boolean {
		return blocking && at(loc) != null
	}

	override fun containsAll(elements: Collection<T>): Boolean {
		return items.containsAll(elements)
	}

	override fun isEmpty(): Boolean {
		return items.isEmpty()
	}

	override fun contains(element: T): Boolean {
		return items.contains(element)
	}

	override fun iterator(): MutableIterator<T> {
		return items.iterator()
	}

	override fun add(element: T): Boolean {
		return toAdd.add(element)
	}

	fun addAt(element: T, loc: TileLoc): Boolean {
		if (occupied(loc))
			return false
		if (tileBlockCheck(loc))
			return false


		element.position = loc
		return add(element)
	}

	override fun addAll(elements: Collection<T>): Boolean {
		return toAdd.addAll(elements)
	}

	override fun clear() {
		toRemove.addAll(items)
	}

	override fun remove(element: T): Boolean {
		return toRemove.add(element)
	}

	override fun removeAll(elements: Collection<T>): Boolean {
		return toRemove.removeAll(elements)
	}

	override fun retainAll(elements: Collection<T>): Boolean {
		return items.retainAll(elements)
	}
}