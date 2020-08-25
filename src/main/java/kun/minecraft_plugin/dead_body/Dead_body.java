package kun.minecraft_plugin.dead_body;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
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
    private HashSet<XZLocation> dethLoc;
    private HashSet<XZLocation> safeZoon;

    public Dead_body() {
    }

    @Override
    public void onEnable() {
        dethLoc=new HashSet<>();
        safeZoon=new HashSet<>();
        getServer().getPluginManager().registerEvents(new room_in(this), this);
        getLogger().info("読み込み完了！");
    }

    @Override
    public void onDisable(){
        saveDeth();
    }

    public void addDethLoc(Location l){
        dethLoc.add(new XZLocation(l));
        System.out.println(l.getX()+"::"+l.getZ());
    }
    public void setFirst_sp(Location l){
        if(first_sp==null) {
            l.setX(Math.round(l.getX()));
            l.setY(Math.round(l.getY()));
            l.setZ(Math.round(l.getZ()));
            first_sp = l.clone();
            Objects.requireNonNull(l.getWorld()).setSpawnLocation(l);
            l.getWorld().setGameRule(GameRule.SPAWN_RADIUS, 0);

            l.setX(l.getX()-big-1);
            l.setY(l.getY()-1);
            l.setZ(l.getZ()-big-1);
            for (int x=0;x<=2*big+1;x++){
                for(int z=0;z<=2*big+1;z++){
                    l.getBlock().setType(Material.BEDROCK);
                    Location m=l.clone();
                    for (int q=1;q<4;q++){
                        m.setY(m.getY()+1);
                        m.getBlock().setType(Material.AIR);
                        if(q==1){
                            if((x==0|x==2*big+1)&(z==0|z==2*big+1)){
                                m.getBlock().setType(Material.BEDROCK);
                            }
                        }
                    }
                    safeZoon.add(new XZLocation(l));
                    l.setZ(l.getZ()+1);
                }
                l.setX(first_sp.getX()+x-big);
                l.setZ(first_sp.getZ()-big-1);
            }

            getServer().getScheduler().runTask(this,()->{
                locate_toString();
            });
        }
    }
    private void locate_toString(){

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
