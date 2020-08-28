package kun.minecraft_plugin.dead_body;

import org.bukkit.Location;
import org.bukkit.World;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class XZLocation implements Serializable {
    private final UUID world;
    private int X;
    private int Z;

    /**
     * 新しいXZLocationを生成する.
     * @param world ワールドインスタンス
     * @param X X座標
     * @param Z Z座標
     */
    public XZLocation(World world,int X,int Z){
        this.world=world.getUID();
        this.X=X;
        this.Z=Z;
    }

    /**
     * 新しいXZLocationを生成する.
     * <p>Y座標は削除する。また、Int型で保持する。</p>
     * @param location 位置情報
     */
    public XZLocation(Location location){
        this.world= Objects.requireNonNull(location.getWorld()).getUID();
        this.X= (int) location.getX();
        this.Z= (int) location.getZ();
    }

    /**
     * X座標のみ増加させる.
     * @param r 増加させる距離
     */
    public void addX(int r){
        this.X+=r;
    }

    /**
     * Z座標のみ増加させる.
     * @param r 増加させる距離
     */
    public void addZ(int r){
        this.Z+=r;
    }

    /**
     * X座標とZ座標を両方、増加させる.
     * <p>拡張性維持のため、残しているが実際には利用されていない。</p>
     * @param r 増加させる距離
     */
    public void addXZ(int r){
        this.addX(r);
        this.addZ(r);
    }

    /**
     * X座標を取得する.
     * <p>拡張性維持のため、残しているが実際には利用されていない。</p>
     * @return X座標の値
     */
    public int getX() {
        return X;
    }

    /**
     * Z座標を取得する.
     * <p>拡張性維持のため、残しているが実際には利用されていない。</p>
     * @return Z座標の値
     */
    public int getZ() {
        return Z;
    }

    /**
     * X座標を設定する.
     * <p>拡張性維持のため、残しているが実際には利用されていない。</p>
     */
    public void setX(int x) {
        X = x;
    }

    /**
     * Z座標を設定する.
     * <p>拡張性維持のため、残しているが実際には利用されていない。</p>
     */
    public void setZ(int z) {
        Z = z;
    }

    /**
     * worldのUUID（一意の値）を取得する.
     * @return UUID
     */
    public UUID getWorldUid() {
        return world;
    }

    /**
     * このオブジェクトと同じ値ならtrueを返す.
     * @param o 判定するオブジェクト
     * @return 結果
     */
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
