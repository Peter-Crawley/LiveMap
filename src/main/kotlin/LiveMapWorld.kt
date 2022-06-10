package io.github.petercrawley.livemap

import org.bukkit.Material
import java.io.File

internal class LiveMapWorld(
	worldsDirectory: File,
	worldName: String
) {
	internal val worldDirectory = worldsDirectory.resolve(worldName)

	internal val loadedRegions = mutableMapOf<Position2D<Short>, LiveMapRegion>()

	internal val blockPalette = {
		worldDirectory.mkdirs()

		val paletteFile = worldDirectory.resolve("pallete.lmp")

		if (paletteFile.exists()) paletteFile.readLines().mapTo(mutableListOf()) { Material.valueOf(it) }
		else mutableListOf()
	}
}