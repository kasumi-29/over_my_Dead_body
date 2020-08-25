package kun.minecraft_plugin.dead_body;

import org.bukkit.plugin.java.JavaPlugin;

public final class Dead_body extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new room_in(this), this);
    }
}
