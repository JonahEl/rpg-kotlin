package weft.display

import weft.game.map.GameMap
import weft.game.map.generators.CaveMapGenerator
import weft.game.map.generators.EmptyMapGenerator
import weft.messages.KeyType
import weft.messages.Message
import java.awt.Point
import java.awt.Rectangle

class PlayScreen(terminal: Display) : Screen(terminal) {
	private var world: GameMap = GameMap(EmptyMapGenerator())
	private val viewport: Rectangle = Rectangle(0, 0, screenSize.width, screenSize.height - 2)
	private val bottomUIFrame: Rectangle = Rectangle(1, screenSize.height - 2, screenSize.width - 2, screenSize.height)
	private var scroll: Point = Point(0, 0)

	init {
		world = GameMap(CaveMapGenerator(screenSize.width + 10, screenSize.height + 10))
		world.update()
	}

	private fun updateScroll() {
		scroll = Point(Math.max(0, Math.min(world.player.position.x - viewport.width / 2, world.size.width - viewport.width))
				, Math.max(0, Math.min(world.player.position.y - viewport.height / 2, world.size.height - viewport.height)))
	}

	override fun render() {
		updateScroll()
		world.render(viewport.size, scroll, { w, l -> display.write(w, l.x, l.y) })

		display.write("Turn: ${world.turn}", bottomUIFrame.x, bottomUIFrame.y)
		display.write("Player: HP ${world.player.currentHealth}/${world.player.maxHealth} AT ${world.player.attack}", bottomUIFrame.x, bottomUIFrame.y + 1)
		display.write("Char: ${world.player.glyph.character}", bottomUIFrame.x + 40, bottomUIFrame.y)

		display.write("Monsters: ${world.creatures.size}", bottomUIFrame.width - 30, bottomUIFrame.y)
		//val pt = world.player.ai.target;
		//display.write(if (pt != null) "$pt: HP ${pt.currentHealth}/${pt.maxHealth} AT ${pt.attack}" else "",
		//        bottomUIFrame.width - 30,
		//        bottomUIFrame.y + 1)
	}

	override fun update() {
		world.update()

		if (world.player.currentHealth <= 0)
			nextScreen = MessageScreen(display, listOf("You died"))
	}

	override fun processKeyPress(key: Message.KeyPress) {
		if (world.player.processKeyPress(key))
			return;

		nextScreen = when (key.key) {
			KeyType.Escape -> MessageScreen(display, listOf("You lost"))
			KeyType.Enter -> MessageScreen(display, listOf("You won"))
			else -> nextScreen
		}
	}
}
