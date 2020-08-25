package kun.minecraft_plugin.dead_body;

import org.bukkit.Location;
import org.bukkit.World;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;

public class XZLocation implements Serializable {
    private final World world;
    private int X;
    private int Z;
    public XZLocation(World world,int X,int Z){
        this.world=world;
        this.X=X;
        this.Z=Z;
    }
    public XZLocation(Location location){
        this.world=location.getWorld();
        this.X= (int) location.getX();
        this.Z= (int) location.getZ();
    }
    public HashSet<XZLocation> fill(XZLocation l){
        HashSet<XZLocation> output=new HashSet<>();
        if(this.world.equals(l.world)) {
            for (int tx = Math.min(this.X, l.X); tx <= Math.max(this.X, l.X); tx++) {
                for (int tz = Math.min(this.Z, l.Z); tz <= Math.max(this.X, l.Z); tz++) {
                    output.add(new XZLocation(this.world,tx,tz));
                }
            }
        }
        return output;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XZLocation that = (XZLocation) o;
        return X == that.X &&
                Z == that.Z &&
                Objects.equals(world, that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, X, Z);
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

    public World getWorld() {
        return world;
    }
}
