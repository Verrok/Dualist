package verrok.dualist

import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import verrok.dualist.Helpers.Messages
import verrok.dualist.Helpers.log
import verrok.dualist.Helpers.mcformat
import java.util.logging.Logger


class DualistEventHandler(val plugin: JavaPlugin, val logger: Logger, val config: FileConfiguration) : Listener {


    @EventHandler
    fun onDamagePlayer(e: EntityDamageByEntityEvent) {
        if (e.damager is Player && e.entity is Player) {
            val name = e.damager.name
            val targetName = e.entity.name
            if (Dualist.isInDuel(targetName)) {
                if (!Dualist.isInDuelWith(targetName, name)) {
                    e.isCancelled = true
                    return
                }
            }
        }
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        val name = e.player.name;
        if (Dualist.isInDuel(name)) {
            if (Dualist.isInitiator(name)) {
                val player = Bukkit.getPlayer(Dualist.duelList[name]!!)
                player.sendTitle(Messages["playerQuit"],Messages["playerQuitSub"], 5, 30, 5)
                Dualist.duelList.remove(name)
            } else if (Dualist.isParticipant(name)) {
                Dualist.duelList.keys.forEach lit@{
                    if (Dualist.duelList[it] == name) {
                        val player = Bukkit.getPlayer(it)
                        player.sendTitle(Messages["playerQuit"],Messages["playerQuitSub"], 5, 30, 5)
                        Dualist.duelList.remove(it, name)
                        return@lit;
                    }
                }
            }
        }
    }

}