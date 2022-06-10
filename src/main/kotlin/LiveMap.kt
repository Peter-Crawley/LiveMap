package io.github.petercrawley.livemap

import org.bstats.bukkit.Metrics
import org.bukkit.Chunk
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.event.world.WorldInitEvent
import org.bukkit.event.world.WorldUnloadEvent
import org.bukkit.plugin.java.JavaPlugin

internal val worlds: MutableMap<World, LiveMapWorld> = mutableMapOf()

@Suppress("unused") // Entrypoint
class LiveMap : JavaPlugin(), Listener {
	override fun onEnable() {
		Metrics(this, 15261)

		server.pluginManager.registerEvents(this, this)
	}

	@EventHandler(priority = EventPriority.MONITOR)
	fun onWorldInitEvent(event: WorldInitEvent) {
		worlds[event.world] = LiveMapWorld(event.world.worldFolder)
	}

	@EventHandler(priority = EventPriority.MONITOR)
	fun onWorldUnloadEvent(event: WorldUnloadEvent) {
		worlds.remove(event.world)
	}

	@EventHandler(priority = EventPriority.MONITOR)
	fun onChunkLoadEvent(event: ChunkLoadEvent) {
		// Somehow a ChunkLoadEvent is called before a WorldLoadEvent, so add the world if it does not already exist.
		val world = worlds.getOrPut(event.chunk.world) { LiveMapWorld(event.chunk.world.worldFolder) }

		processChunk(event.chunk) { regionPosition, regionChunkPosition ->
			val region = world.loadedRegions.getOrPut(regionPosition) {
				LiveMapRegion(regionPosition, world.worldDirectory)
			}

			region.loadedChunks.add(regionChunkPosition)
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	fun onChunkUnloadEvent(event: ChunkUnloadEvent) {
		val world = worlds[event.chunk.world]!!

		processChunk(event.chunk) { regionPosition, regionChunkPosition ->
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
		handle: (regionPosition: Position2D<Short>, regionChunkPosition: Position2D<Byte>) -> Unit
	) {
		val regionX = chunk.x.floorDiv(32).toShort()
		val regionZ = chunk.z.floorDiv(32).toShort()

		val regionChunkX = (chunk.x - (regionX * 32)).toByte()
		val regionChunkZ = (chunk.z - (regionZ * 32)).toByte()

		handle(Position2D(regionX, regionZ), Position2D(regionChunkX, regionChunkZ))
	}
}