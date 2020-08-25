package kun.minecraft_plugin.dead_body;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

import static org.bukkit.Bukkit.getConsoleSender;

public final class Dead_body extends JavaPlugin {
    private Location first_sp=null;
    private int big=3;
    private HashSet<Location> dethLoc;

    @Override
    public void onEnable() {
        dethLoc=new HashSet<>();
        getServer().getPluginManager().registerEvents(new room_in(this), this);
    }

    @Override
    public void onDisable(){
        saveDeth();
    }

    public void addDethLoc(Location l){
        dethLoc.add(l);
        System.out.println(l.getX()+"::"+l.getZ());
    }
    public void setFirst_sp(Location l){
        if(first_sp==null) {
            l.setX(Math.round(l.getX()));
            l.setY(Math.round(l.getY()));
            l.setZ(Math.round(l.getZ()));
            first_sp = l;
            Objects.requireNonNull(l.getWorld()).setSpawnLocation(l);
            Bukkit.dispatchCommand(getConsoleSender(),"fill "+locate_toString(l,true)+" "+locate_toString(l,false)+" bedrock");
        }
    }
    private String locate_toString(Location l,boolean d){
        big=Math.abs(big);
        if (d) {
            big = -1 * big;
        }
        return ((int)(l.getX()+big))+" "+((int)l.getY()-1)+" "+((int)l.getZ()+big);
    }
    public Location getFirst_sp(){
        return first_sp;
    }
    public void saveDeth(){
        try(FileOutputStream f = new FileOutputStream("dethLoc.dat");
            BufferedOutputStream b = new BufferedOutputStream(f);
            ObjectOutputStream out = new ObjectOutputStream(b)){
                out.writeObject(dethLoc);
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}
