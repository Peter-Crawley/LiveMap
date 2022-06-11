package io.github.petercrawley.livemap

import org.bukkit.Bukkit
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

	internal fun enable() {
		// When reloading, we won't be told about worlds which are already loaded, so we need to check for them.
		Bukkit.getWorlds().forEach { world -> registerWorld(world) }
	}

	private fun registerWorld(world: World) {
		worlds[world] = LiveMapWorld(world)
	}

	private fun unregisterWorld(world: World) {
		worlds.remove(world)!!.close()
	}

	@EventHandler(priority = EventPriority.MONITOR)
	@Suppress("unused") // Entrypoint
	fun onWorldInitEvent(event: WorldInitEvent) =
		registerWorld(event.world)

	@EventHandler(priority = EventPriority.MONITOR)
	@Suppress("unused") // Entrypoint
	fun onWorldUnloadEvent(event: WorldUnloadEvent) =
		unregisterWorld(event.world)

	@EventHandler(priority = EventPriority.MONITOR)
	@Suppress("unused") // Entrypoint
	fun onChunkLoadEvent(event: ChunkLoadEvent) =
		worlds[event.world]!!.registerChunk(event.chunk)

	@EventHandler(priority = EventPriority.MONITOR)
	@Suppress("unused") // Entrypoint
	fun onChunkUnloadEvent(event: ChunkUnloadEvent) =
		worlds[event.world]!!.unregisterChunk(event.chunk)

	internal fun close() {
		worlds.forEach { (_, liveMapWorld) -> liveMapWorld.close() }

		worlds.clear()
	}
}