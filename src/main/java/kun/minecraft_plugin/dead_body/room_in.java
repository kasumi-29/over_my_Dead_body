package kun.minecraft_plugin.dead_body;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class room_in implements Listener {
    private final Dead_body m;
    public room_in(Dead_body dead_body) {
        m=dead_body;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player p=event.getPlayer();
        m.setFirst_sp(p.getLocation());
        p.teleport(m.getFirst_sp());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        m.addDethLoc(event.getEntity().getLocation());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        m.killChecker(event.getTo());
    }
}
