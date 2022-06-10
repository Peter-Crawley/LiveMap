package io.github.petercrawley.livemap

import org.bukkit.Material
import org.bukkit.World

internal class LiveMapWorld(bukkitWorld: World) {
	internal val worldDirectory = bukkitWorld.worldFolder.resolve("livemap")

	internal val loadedRegions = mutableMapOf<Position2D<Short>, LiveMapRegion>()

	private val paletteFile = worldDirectory.resolve("palette.lmp")
	private val blockPalette: MutableList<Material>

	init {
		worldDirectory.mkdirs()

		blockPalette = if (paletteFile.exists()) {
			paletteFile.readLines().mapTo(mutableListOf()) { Material.valueOf(it) }
		} else {
			paletteFile.createNewFile()
			mutableListOf()
		}
	}

	internal fun close() {
		loadedRegions.forEach { (_, region) ->
			region.close()

			paletteFile.writeText(blockPalette.joinToString("\n", "", "") { it.name })
		}

		loadedRegions.clear()
	}
}