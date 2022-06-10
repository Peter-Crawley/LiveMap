package io.github.petercrawley.livemap

import java.io.File
import java.io.RandomAccessFile
import java.nio.channels.FileChannel

internal class LiveMapRegion(regionPosition: Position2D<Short>, worldDirectory: File) {
	init {
		worldDirectory.mkdirs()
	}

	internal val loadedChunks = mutableListOf<Position2D<Byte>>()

	private val regionFile = RandomAccessFile(worldDirectory.resolve("${regionPosition.x}-${regionPosition.z}.lmr"), "rwd")

	private val data = regionFile.channel.map(FileChannel.MapMode.READ_WRITE, 0, 16*16*32*32*2)

	internal fun close() = regionFile.close()
}