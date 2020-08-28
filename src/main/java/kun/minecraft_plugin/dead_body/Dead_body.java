package kun.minecraft_plugin.dead_body;

import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public final class Dead_body extends JavaPlugin {
    private Location first_sp=null;
    private int big=3;
    private String death_path="plugins\\over_my_Dead_body\\deathLoc.dat";
    private String safe_path="plugins\\over_my_Dead_body\\safeLoc.dat";
    private HashSet<XZLocation> deathLoc;
    private HashSet<XZLocation> safeZone;
    static private class zeroArray implements TabCompleter {
        @Override
        public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args){
            return new ArrayList<>();
        }
    }

    /**
     * 読み込み時の初期設定を行う.
     * <p>コンフィグファイルから読み込む、コマンドの登録など</p>
     */
    @Override
    public void onEnable() {
        deathLoc=new HashSet<>();
        safeZone =new HashSet<>();

        saveDefaultConfig();
        big=getConfig().getInt("default_safeZone");
        death_path=getConfig().getString("death_path");
        safe_path=getConfig().getString("safe_path");
        big=Math.max(big, 0);
        big=Math.min(big,16);

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
        Objects.requireNonNull(getCommand("info-locate")).setExecutor((sender, command, label, args) -> {
            ArrayList<String> msgList=info();
            if(!(sender instanceof ConsoleCommandSender)){
                for (String msg:msgList) {
                    sender.sendMessage(msg);
                }
            }
            for (String msg:msgList) {
                getLogger().info(msg);
            }
            return true;
        });
        Objects.requireNonNull(getCommand("set-safezone")).setTabCompleter(new zeroArray());
        Objects.requireNonNull(getCommand("save-locate")).setTabCompleter(new zeroArray());
        Objects.requireNonNull(getCommand("load-locate")).setTabCompleter(new zeroArray());
        Objects.requireNonNull(getCommand("info-locate")).setTabCompleter(new zeroArray());
        sl_comp(false);

        if (safeZone.size()!=0){
            Iterator<XZLocation> iterator=safeZone.iterator();
            first_sp=Objects.requireNonNull(getServer().getWorld(iterator.next().getWorldUid())).getSpawnLocation();
        }
        for (String msg:info()) {
            getLogger().info(msg);
        }
        getLogger().info("読み込み完了！");
    }

    /**
     * 情報を返却する.
     * <p>初期スポーン、危険地帯の数、安全地帯の数をこの順にセットしたArrayListを返却する</p>
     * @return セットした情報のリスト
     */
    public ArrayList<String> info(){
        ArrayList<String> output=new ArrayList<>();
        if(first_sp!=null){
            output.add("初期スポーンは、x="+((int)first_sp.getX())+",y=?,z="+((int)first_sp.getZ())+"です");
        }else{
            output.add("初期スポーンは、まだ決定していません");
        }
        output.add("危険地帯："+deathLoc.size()+"箇所");
        output.add("安全地帯："+safeZone.size()+"箇所");
        return output;
    }

    /**
     * 終了時の処理を行う.
     * <p>ファイルへの書き出しを行う。<br>ただしファイルの書き出しを行う際は、 sl_comp を用いること。</p>
     */
    @Override
    public void onDisable(){
        sl_comp(true);
    }

    /**
     * 危険地帯と安全地帯の情報を入出力する.
     * <p>ファイルへの書き出しまたは読み込みを行う。なお、値はそのままclass内で保持されます。</p>
     * @param flag trueの場合saveを行い、falseの場合はloadを行う
     */
    public void sl_comp(boolean flag){
        if(flag){//save側
            saveDeath(death_path,deathLoc);
            saveDeath(safe_path,safeZone);
        }else{//load側
            HashSet<XZLocation> tempD= loadDeath(death_path);
            HashSet<XZLocation> tempS= loadDeath(safe_path);
            if(tempD!=null) {
                deathLoc = tempD;
                getLogger().info("危険地帯をファイルから正常に読み込みました。");
            }
            if(tempS!=null) {
                safeZone = tempS;
                getLogger().info("安全地帯をファイルから正常に読み込みました。");
            }
        }
    }

    /**
     * 危険地帯（これまでに死んだ場所）に居るかどうか判定する.
     * @param l 判定する位置情報
     * @return 危険地帯の位置に居れば真を返す
     */
    public boolean killChecker(Location l){
        return deathLoc.contains(new XZLocation(l));
    }

    /**
     * 危険地帯に新しく位置情報を登録する.
     * @param l 登録する位置情報
     */
    public void addDeathLoc(Location l){
        XZLocation XZl=new XZLocation(l);
        if(!safeZone.contains(XZl)) {
            deathLoc.add(XZl);
        }else{
            getLogger().info("安全地帯で死亡イベントが発生しました。");
        }
    }

    /**
     * ログイン時の処理.
     * <p>
     *     スポーン範囲の調整、安全地帯の生成を行う。<br>
     *     ただしワールド生成後、初めてログインするプレイヤーのみ実行される。
     * </p>
     * @param l 安全地帯の中心となる位置情報
     */
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
                    XZLocation nowXZl=new XZLocation(l);
                    safeZone.add(nowXZl);
                    deathLoc.remove(nowXZl);
                    if(checkP(ox,oz,n)&&(n>1)){
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

    /**
     * 初期スポーンの位置情報を取得する.
     * @return 初期スポーンの位置情報
     */
    public Location getFirst_sp(){
        return first_sp;
    }
    private boolean datFilecheck(boolean flag,String check_path){
        File deathLog=new File(check_path);
        boolean output = deathLog.exists();
        if(flag&&(!output)){
            try {
                boolean a=deathLog.getParentFile().mkdirs();
                output=deathLog.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return !output;
    }

    /**
     * ファイルへセーブする.
     * <p>危険地帯と安全地帯を一括でセーブする際は、 sl_comp を利用すること。</p>
     * @param save_path ファイルのパス。eula.txtからの相対パス、または絶対パスを指定する。
     * @param save_data セーブする実データ。
     */
    public void saveDeath(String save_path, HashSet<XZLocation> save_data){
        if(datFilecheck(true,save_path)){
            getLogger().info("ファイルをセーブするのに失敗しました。");
            return;
        }
        try(FileOutputStream f = new FileOutputStream(save_path);
            BufferedOutputStream b = new BufferedOutputStream(f);
            ObjectOutputStream out = new ObjectOutputStream(b)){
            out.writeObject(save_data);
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     * ファイルからロードする.
     * <p>危険地帯と安全地帯を一括でロードする際は、 sl_comp を利用すること。</p>
     * @param load_path ファイルのパス。eula.txtからの相対パス、または絶対パスを指定する。
     * @return ロードしたデータ。
     */
    @SuppressWarnings("unchecked")
    public HashSet<XZLocation> loadDeath(String load_path){
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
