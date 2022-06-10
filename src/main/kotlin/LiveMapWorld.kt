package io.github.petercrawley.livemap

import org.bukkit.Material
import java.io.File

internal class LiveMapWorld(
	worldsDirectory: File
) {
	internal val worldDirectory = worldsDirectory.resolve("livemap")

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