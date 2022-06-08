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
		processChunk(event.chunk) { world, regionPosition, regionChunkPosition ->
			world.loadedRegions.getOrPut(regionPosition) {
				LiveMapRegion(world.worldFile.resolve("$regionPosition.lmr"))
			}.loadedChunks.add(regionChunkPosition)
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	fun onChunkUnloadEvent(event: ChunkUnloadEvent) {
		processChunk(event.chunk) { world, regionPosition, regionChunkPosition ->
			val region = world.loadedRegions[regionPosition]!!

			region.loadedChunks.remove(regionChunkPosition)

			if (region.loadedChunks.isEmpty()) {
				region.close()

				world.loadedRegions.remove(regionPosition)
			}
		}
	}

	private inline fun processChunk(
		chunk: Chunk,
		handle: (world: LiveMapWorld, regionPosition: Position2D<Short>, regionChunkPosition: Position2D<Byte>) -> Unit
	) {
		val regionX = chunk.x.floorDiv(32).toShort()
		val regionZ = chunk.z.floorDiv(32).toShort()

		val regionChunkX = (chunk.x - (regionX * 32)).toByte()
		val regionChunkZ = (chunk.z - (regionZ * 32)).toByte()

		handle(worlds[chunk.world]!!, Position2D(regionX, regionZ), Position2D(regionChunkX, regionChunkZ))
	}
}