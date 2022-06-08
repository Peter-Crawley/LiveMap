package io.github.petercrawley.livemap

import org.bstats.bukkit.Metrics
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldLoadEvent
import org.bukkit.event.world.WorldUnloadEvent
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused") // Entrypoint
class LiveMap : JavaPlugin(), Listener {
	override fun onEnable() {
		Metrics(this, 15261)
	}

	private val worlds = mutableMapOf<World, LiveMapWorld>()

	@EventHandler(priority = EventPriority.MONITOR)
	fun onWorldLoadEvent(event: WorldLoadEvent) {
		worlds[event.world] = LiveMapWorld()
	}

	@EventHandler(priority = EventPriority.MONITOR)
	fun onWorldUnloadEvent(event: WorldUnloadEvent) {
		worlds.remove(event.world)
	}
}