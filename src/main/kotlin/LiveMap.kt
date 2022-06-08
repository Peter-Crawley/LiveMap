package io.github.petercrawley.livemap

import org.bstats.bukkit.Metrics
import org.bukkit.Chunk
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.event.world.WorldLoadEvent
import org.bukkit.event.world.WorldUnloadEvent
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused") // Entrypoint
class LiveMap : JavaPlugin(), Listener {
	private val worldsFile = dataFolder.resolve("worlds")

	init {
		worldsFile.mkdirs()
	}

	override fun onEnable() {
		Metrics(this, 15261)

		server.pluginManager.registerEvents(this, this)
	}

	private val worlds = mutableMapOf<World, LiveMapWorld>()

	@EventHandler(priority = EventPriority.MONITOR)
	fun onWorldLoadEvent(event: WorldLoadEvent) {
		val worldFile = worldsFile.resolve(event.world.name)

		worldFile.mkdirs()

		worlds[event.world] = LiveMapWorld(worldFile)
	}

	@EventHandler(priority = EventPriority.MONITOR)
	fun onWorldUnloadEvent(event: WorldUnloadEvent) {
		worlds.remove(event.world)
	}

	@EventHandler(priority = EventPriority.MONITOR)
	fun onChunkLoadEvent(event: ChunkLoadEvent) {
		processChunk(event.chunk) { world, regionKey, chunkKey ->
			world.loadedRegions.getOrPut(regionKey) {
				LiveMapRegion(world.worldFile.resolve("$regionKey.lmr"))
			}.loadedChunks.add(chunkKey)
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	fun onChunkUnloadEvent(event: ChunkUnloadEvent) {
		processChunk(event.chunk) { world, regionKey, chunkKey ->
			val region = world.loadedRegions[regionKey]!!

			region.loadedChunks.remove(chunkKey)

			if (region.loadedChunks.isEmpty()) {
				region.close()

				world.loadedRegions.remove(regionKey)
			}
		}
	}

	private fun processChunk(chunk: Chunk, handle: (world: LiveMapWorld, regionKey: Int, chunkKey: Short) -> Unit) {
		val world = worlds[chunk.world]!!

		val regionX = chunk.x.floorDiv(32)
		val regionZ = chunk.z.floorDiv(32)
		val regionKey = regionX shl 16 + regionZ

		val chunkKey = ((chunk.x - (regionX * 32)) shl 8 + (chunk.z - (regionZ * 32))).toShort()

		handle(world, regionKey, chunkKey)
	}
}