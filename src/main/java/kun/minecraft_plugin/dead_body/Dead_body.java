package kun.minecraft_plugin.dead_body;

import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;

public final class Dead_body extends JavaPlugin {
    private Location first_sp=null;
    private int big=3;
    private final String deth_path="plugins\\dead_body\\dethLoc.dat";
    private final String safe_path="plugins\\dead_body\\safeLoc.dat";
    private HashSet<XZLocation> dethLoc;
    private HashSet<XZLocation> safeZone;

    @Override
    public void onEnable() {
        dethLoc=new HashSet<>();
        safeZone =new HashSet<>();
        getServer().getPluginManager().registerEvents(new room_in(this), this);
        Objects.requireNonNull(getCommand("set-safezone")).setExecutor((sender, command, label, args) -> {
            if(!(sender instanceof Player)){
                sender.sendMessage("[!!!ERR!!!]ゲーム内から実行してください");
                return false;
            }
            first_sp=null;
            setFirst_sp(((Player) sender).getLocation());
            return true;
        });
        Objects.requireNonNull(getCommand("set-safezone")).setTabCompleter((sender, command, alias, args) -> new ArrayList<>());
        dethLoc=loadDeth(deth_path);
        safeZone=loadDeth(safe_path);

        if (safeZone!=null){
            Iterator<XZLocation> iterator=safeZone.iterator();
            first_sp=Objects.requireNonNull(getServer().getWorld(iterator.next().getWorldUid())).getSpawnLocation();
        }

        getLogger().info("読み込み完了！");
    }

    @Override
    public void onDisable(){
        saveDeth(deth_path,dethLoc);
        saveDeth(safe_path,safeZone);
    }

    public boolean killChecker(Location l){
        return dethLoc.contains(new XZLocation(l));
    }

    public void addDethLoc(Location l){
        XZLocation XZl=new XZLocation(l);
        if(!safeZone.contains(XZl)) {
            dethLoc.add(XZl);
        }else{
            getLogger().info("安全地帯で死亡イベントが発生しました。");
        }
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
                            if((x==0||x==2*big+1)&(z==0||z==2*big+1)){
                                m.getBlock().setType(Material.BEDROCK);
                            }
                        }
                    }
                    safeZone.add(new XZLocation(l));
                    l.setZ(l.getZ()+1);
                }
                l.setX(first_sp.getX()+x-big);
                l.setZ(first_sp.getZ()-big-1);
            }
        }
    }
    public Location getFirst_sp(){
        return first_sp;
    }
    private boolean datFilecheck(boolean flag,String check_path){
        File DethLog=new File(check_path);
        boolean output = DethLog.exists();
        if(flag&&(!output)){
            try {
                boolean a=new File("plugins\\dead_body").mkdirs();
                output=DethLog.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return !output;
    }
    public void saveDeth(String save_path,HashSet<XZLocation> savedata){
        if(datFilecheck(true,save_path)){
            getLogger().info("ファイルをセーブするのに失敗しました。");
            return;
        }
        try(FileOutputStream f = new FileOutputStream(save_path);
            BufferedOutputStream b = new BufferedOutputStream(f);
            ObjectOutputStream out = new ObjectOutputStream(b)){
            out.writeObject(savedata);
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public HashSet<XZLocation> loadDeth(String load_path){
        if (datFilecheck(false,load_path)){return null;}
        try(FileInputStream f = new FileInputStream(load_path);
            BufferedInputStream b = new BufferedInputStream(f);
            ObjectInputStream in = new ObjectInputStream(b)){
            return (HashSet<XZLocation>) in.readObject();
        } catch ( IOException | ClassNotFoundException e ) {
            e.printStackTrace();
        }
        return null;
    }
}
