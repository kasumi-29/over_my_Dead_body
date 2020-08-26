package kun.minecraft_plugin.dead_body;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
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
        if(m.killChecker(event.getTo())){
            event.getPlayer().setHealth(0d);
        }
    }

    @EventHandler
    public void onGetCommand(PlayerCommandSendEvent event){
        //Todo setworldspawnをキャッチできないか試行錯誤
        String s[]=event.getCommands().toArray(new String[0]);
        System.out.println(s[0]);
    }
}
