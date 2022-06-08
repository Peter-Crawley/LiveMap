package io.github.petercrawley.livemap

import java.io.File
import java.io.RandomAccessFile

internal class LiveMapRegion(
	file: File
) {
	internal val loadedChunks = mutableListOf<Short>()

	private val regionFile = RandomAccessFile(file, "rwd")

	internal fun close() = regionFile.close()
}