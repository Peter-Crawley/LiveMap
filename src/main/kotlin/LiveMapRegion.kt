package io.github.petercrawley.livemap

import java.io.File
import java.io.RandomAccessFile

internal class LiveMapRegion(
	regionPosition: Position2D<Short>,
	worldDirectory: File
) {
	init {
		worldDirectory.mkdirs()
	}

	internal val loadedChunks = mutableListOf<Position2D<Byte>>()

	private val regionFile = RandomAccessFile(worldDirectory.resolve("${regionPosition.x}-${regionPosition.z}.lmr"), "rwd")

	internal fun close() = regionFile.close()
}