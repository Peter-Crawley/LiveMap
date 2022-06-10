package io.github.petercrawley.livemap

import org.bstats.bukkit.Metrics
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused") // Entrypoint
class LiveMap : JavaPlugin() {
	override fun onEnable() {
		Metrics(this, 15261)

		server.pluginManager.registerEvents(WorldManager, this)
	}

	override fun onDisable() {
		WorldManager.close()
	}
}