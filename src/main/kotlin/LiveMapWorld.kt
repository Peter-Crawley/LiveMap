package io.github.petercrawley.livemap

import org.bukkit.Material
import org.bukkit.World

internal class LiveMapWorld(bukkitWorld: World) {
	internal val worldDirectory = bukkitWorld.worldFolder.resolve("livemap")

	internal val loadedRegions = mutableMapOf<Position2D<Short>, LiveMapRegion>()

	private val blockPalette: MutableList<Material>

	init {
		worldDirectory.mkdirs()

		val paletteFile = worldDirectory.resolve("palette.lmp")

		blockPalette = if (paletteFile.exists()) {
			paletteFile.readLines().mapTo(mutableListOf()) { Material.valueOf(it) }
		} else {
			paletteFile.createNewFile()
			mutableListOf()
		}
	}
}