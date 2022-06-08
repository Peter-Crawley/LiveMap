package io.github.petercrawley.livemap

import java.io.File

internal class LiveMapWorld(
	internal val worldFile: File
) {
	internal val loadedRegions = mutableMapOf<Position2D<Short>, LiveMapRegion>()
}