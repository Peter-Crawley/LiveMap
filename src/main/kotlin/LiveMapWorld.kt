package io.github.petercrawley.livemap

import org.bukkit.Chunk
import org.bukkit.Material
import org.bukkit.World

internal class LiveMapWorld(bukkitWorld: World) {
	private val worldDirectory = bukkitWorld.worldFolder.resolve("livemap")

	private val loadedRegions = mutableMapOf<Position2D<Short>, LiveMapRegion>()

	private val blockPaletteFile = worldDirectory.resolve("palette.lmp")
	private val blockPalette: MutableList<Material>

	init {
		worldDirectory.mkdirs()

		// When reloading, we won't be told about chunks which are already loaded, so we need to check for them.
		bukkitWorld.loadedChunks.forEach { chunk -> registerChunk(chunk) }

		blockPalette =
			if (blockPaletteFile.exists()) {
				blockPaletteFile.readLines().mapTo(mutableListOf()) { Material.valueOf(it) }
			} else {
				blockPaletteFile.createNewFile()
				mutableListOf()
			}
	}

	internal fun registerChunk(chunk: Chunk) {
		val (regionPosition, regionChunkPosition) = getPositions(chunk)

		val region = loadedRegions.getOrPut(regionPosition) { LiveMapRegion(regionPosition, worldDirectory) }

		region.loadedChunks.add(regionChunkPosition)

		updateChunk(region, regionChunkPosition, chunk)
	}

	internal fun unregisterChunk(chunk: Chunk) {
		val (regionPosition, regionChunkPosition) = getPositions(chunk)

		val region = loadedRegions[regionPosition]!!

		region.loadedChunks.remove(regionChunkPosition)

		updateChunk(region, regionChunkPosition, chunk)

		if (region.loadedChunks.isEmpty()) {
			loadedRegions.remove(regionPosition)

			region.close()
		}
	}

	private fun getPositions(chunk: Chunk): Pair<Position2D<Short>, Position2D<Byte>> {
		val regionX = chunk.x.floorDiv(32).toShort()
		val regionZ = chunk.z.floorDiv(32).toShort()

		val regionChunkX = (chunk.x - (regionX * 32)).toByte()
		val regionChunkZ = (chunk.z - (regionZ * 32)).toByte()

		return Pair(Position2D(regionX, regionZ), Position2D(regionChunkX, regionChunkZ))
	}

	private fun updateChunk(liveMapRegion: LiveMapRegion, regionChunkPosition: Position2D<Byte>, chunk: Chunk) {
		val snapshot = chunk.getChunkSnapshot(true, false, false)

		for (x in 0..15) for (z in 0..15) {
			val y = snapshot.getHighestBlockYAt(x, z)
			val material = if (y > chunk.world.minHeight) snapshot.getBlockType(x, y, z) else Material.AIR

			var id = blockPalette.indexOf(material)

			if (id == -1) {
				blockPalette.add(material)
				id = blockPalette.lastIndex
				writeBlockPaletteFile()
			}

			liveMapRegion.data.putShort(((regionChunkPosition.x * 16 + x) * 32 * 16 + regionChunkPosition.z * 16 + z) * 2, id.toShort())
		}
	}

	private fun writeBlockPaletteFile() =
		blockPaletteFile.writeText(blockPalette.joinToString("\n", "", "") { it.name })

	internal fun close() {
		loadedRegions.forEach { (_, region) -> region.close() }

		writeBlockPaletteFile()

		loadedRegions.clear()
	}
}