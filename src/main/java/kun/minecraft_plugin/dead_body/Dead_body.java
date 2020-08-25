package kun.minecraft_plugin.dead_body;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

import static org.bukkit.Bukkit.getConsoleSender;

public final class Dead_body extends JavaPlugin {
    private Location first_sp=null;
    private int big=3;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new room_in(this), this);
    }

    public void setFirst_sp(Location l){
        if(first_sp==null) {
            first_sp = l;
            Objects.requireNonNull(l.getWorld()).setSpawnLocation(l);
            l.setY(l.getY()-1);
            Bukkit.dispatchCommand(getConsoleSender(),"fill "+locate_toString(l,true)+" "+locate_toString(l,false)+" bedrock");
        }
    }
    private String locate_toString(Location l,boolean d){
        if (d) {
            big = -1 * big;
        }
        return ((int)(l.getX()+big))+" "+((int)l.getY())+" "+((int)l.getZ()+big);
    }
    public Location getFirst_sp(){
        return first_sp;
    }
}
