package kun.minecraft_plugin.dead_body;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class room_in implements Listener {
    private final Dead_body m;
    public room_in(Dead_body dead_body) {
        m=dead_body;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){

    }
}
