package io.github.petercrawley.livemap

import org.bukkit.Chunk
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.event.world.WorldInitEvent
import org.bukkit.event.world.WorldUnloadEvent

internal object WorldManager : Listener {
	private val worlds: MutableMap<World, LiveMapWorld> = mutableMapOf()

	internal fun close() {
		worlds.forEach { (_, liveMapWorld) ->
			liveMapWorld.close()
		}

		worlds.clear()
	}

	@EventHandler(priority = EventPriority.MONITOR)
	@Suppress("unused") // Entrypoint
	fun onWorldInitEvent(event: WorldInitEvent) {
		worlds[event.world] = LiveMapWorld(event.world)
	}

	@EventHandler(priority = EventPriority.MONITOR)
	@Suppress("unused") // Entrypoint
	fun onWorldUnloadEvent(event: WorldUnloadEvent) {
		worlds.remove(event.world)!!.close()
	}

	@EventHandler(priority = EventPriority.MONITOR)
	@Suppress("unused") // Entrypoint
	fun onChunkLoadEvent(event: ChunkLoadEvent) {
		val liveMapWorld = worlds[event.world]!!

		val (regionPosition, regionChunkPosition) = getPositions(event.chunk)

		val region = liveMapWorld.loadedRegions.getOrPut(regionPosition) {
			LiveMapRegion(regionPosition, liveMapWorld.worldDirectory)
		}

		region.loadedChunks.add(regionChunkPosition)
	}

	@EventHandler(priority = EventPriority.MONITOR)
	@Suppress("unused") // Entrypoint
	fun onChunkUnloadEvent(event: ChunkUnloadEvent) {
		val liveMapWorld = worlds[event.world]!!

		val (regionPosition, regionChunkPosition) = getPositions(event.chunk)

		val region = liveMapWorld.loadedRegions[regionPosition]!!

		region.loadedChunks.remove(regionChunkPosition)

		if (region.loadedChunks.isEmpty()) {
			region.close()

			liveMapWorld.loadedRegions.remove(regionPosition)
		}
	}

	// This is inlined because this function just exists to de-duplicate the math.
	@Suppress("nothing_to_inline")
	private inline fun getPositions(chunk: Chunk): Pair<Position2D<Short>, Position2D<Byte>> {
		val regionX = chunk.x.floorDiv(32).toShort()
		val regionZ = chunk.z.floorDiv(32).toShort()

		val regionChunkX = (chunk.x - (regionX * 32)).toByte()
		val regionChunkZ = (chunk.z - (regionZ * 32)).toByte()

		return Pair(Position2D(regionX, regionZ), Position2D(regionChunkX, regionChunkZ))
	}
}