package kun.minecraft_plugin.dead_body;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

public class XZLocation implements Serializable {
    private final UUID world;
    private int X;
    private int Z;
    public XZLocation(World world,int X,int Z){
        this.world=world.getUID();
        this.X=X;
        this.Z=Z;
    }
    public XZLocation(Location location){
        this.world= Objects.requireNonNull(location.getWorld()).getUID();
        this.X= (int) location.getX();
        this.Z= (int) location.getZ();
    }

    public void addX(int r){
        this.X+=r;
    }
    public void addZ(int r){
        this.Z+=r;
    }
    public XZLocation addXZ(int r){
        this.addX(r);
        this.addZ(r);
        return this;
    }

    public int getX() {
        return X;
    }

    public int getZ() {
        return Z;
    }

    public void setX(int x) {
        X = x;
    }

    public void setZ(int z) {
        Z = z;
    }

    public UUID getWorldUid() {
        return world;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XZLocation that = (XZLocation) o;
        return X == that.X &&
                Z == that.Z &&
                world.equals(that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, X, Z);
    }
}
