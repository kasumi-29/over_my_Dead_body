package kun.minecraft_plugin.dead_body;

import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public final class Dead_body extends JavaPlugin {
    private Location first_sp=null;
    private int big=3;
    private String death_path="plugins\\dead_body\\deathLoc.dat";
    private String safe_path="plugins\\dead_body\\safeLoc.dat";
    private HashSet<XZLocation> deathLoc;
    private HashSet<XZLocation> safeZone;
    static private class zeroArray implements TabCompleter {
        @Override
        public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args){
            return new ArrayList<>();
        }
    }

    @Override
    public void onEnable() {
        deathLoc=new HashSet<>();
        safeZone =new HashSet<>();

        saveDefaultConfig();
        big=getConfig().getInt("big",big);
        death_path=getConfig().getString("death_path",death_path);
        safe_path=getConfig().getString("safe_path",safe_path);

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
        Objects.requireNonNull(getCommand("save-locate")).setExecutor((sender, command, label, args) -> {
            sl_comp(true);
            sender.sendMessage("Save完了......");
            return true;
        });
        Objects.requireNonNull(getCommand("load-locate")).setExecutor((sender, command, label, args) -> {
            sl_comp(false);
            sender.sendMessage("Load完了......");
            return true;
        });
        Objects.requireNonNull(getCommand("set-safezone")).setTabCompleter(new zeroArray());
        Objects.requireNonNull(getCommand("save-locate")).setTabCompleter(new zeroArray());
        Objects.requireNonNull(getCommand("load-locate")).setTabCompleter(new zeroArray());
        sl_comp(false);

        if (safeZone.size()!=0){
            Iterator<XZLocation> iterator=safeZone.iterator();
            first_sp=Objects.requireNonNull(getServer().getWorld(iterator.next().getWorldUid())).getSpawnLocation();
        }

        getLogger().info("読み込み完了！");
    }

    @Override
    public void onDisable(){
        sl_comp(true);
    }

    private void sl_comp(boolean flag){
        if(flag){//save側
            savedeath(death_path,deathLoc);
            savedeath(safe_path,safeZone);
        }else{//load側
            HashSet<XZLocation> tempD=loaddeath(death_path);
            HashSet<XZLocation> tempS=loaddeath(safe_path);
            if(tempD!=null) {
                deathLoc = tempD;
            }
            if(tempS!=null) {
                safeZone = tempS;
            }
        }
    }

    public boolean killChecker(Location l){
        return deathLoc.contains(new XZLocation(l));
    }

    public void addDeathLoc(Location l){
        XZLocation XZl=new XZLocation(l);
        if(!safeZone.contains(XZl)) {
            deathLoc.add(XZl);
        }else{
            getLogger().info("安全地帯で死亡イベントが発生しました。");
        }
    }
    public void setFirst_sp(Location l){
        if(first_sp==null) {
            first_sp = l.clone();
            Objects.requireNonNull(l.getWorld()).setSpawnLocation(l);
            l.getWorld().setGameRule(GameRule.SPAWN_RADIUS, 0);

            l.setX(l.getX()-big);
            l.setZ(l.getZ()-big);
            l.setY(l.getY()-1);
            final int n=big*2+1;
            for(int ox=0; ox<n; ox++) {
                for (int oz=0; oz<n; oz++) {
                    l.getBlock().setType(Material.BEDROCK);
                    if(checkP(ox,oz,n)){
                        getStepY(l,1).getBlock().setType(Material.BEDROCK);
                    }else {
                        getStepY(l, 1).getBlock().setType(Material.AIR);
                    }
                    getStepY(l,2).getBlock().setType(Material.AIR);
                    getNextZ(l);
                }
                resetZ(l,n);
                getNextX(l);
            }

        }
    }

    private boolean checkP(int ox,int oz,int n){
        return (ox==0||ox==n-1)&&(oz==0||oz==n-1);
    }
    private void resetZ(Location l, int b){
        l.setZ(l.getZ()-b);
    }
    private void getNextZ(Location l){
        l.setZ(l.getZ()+1);
    }
    private void getNextX(Location l){
        l.setX(l.getX()+1);
    }
    private Location getStepY(Location location,int y){
        Location l=location.clone();
        l.setY(l.getY()+y);
        return l;
    }

    public Location getFirst_sp(){
        return first_sp;
    }
    private boolean datFilecheck(boolean flag,String check_path){
        File deathLog=new File(check_path);
        boolean output = deathLog.exists();
        if(flag&&(!output)){
            try {
                boolean a=new File("plugins\\dead_body").mkdirs();
                output=deathLog.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return !output;
    }
    public void savedeath(String save_path,HashSet<XZLocation> savedata){
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
    public HashSet<XZLocation> loaddeath(String load_path){
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
