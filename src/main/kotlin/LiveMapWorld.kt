package io.github.petercrawley.livemap

import java.io.File

internal class LiveMapWorld(
	worldsDirectory: File,
	worldName: String
) {
	internal val worldDirectory = worldsDirectory.resolve(worldName)

	init { worldDirectory.mkdirs() }

	internal val loadedRegions = mutableMapOf<Position2D<Short>, LiveMapRegion>()
}