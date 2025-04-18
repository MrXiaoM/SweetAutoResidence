package top.mrxiaom.sweet.autores.api;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

/**
 * 领地选择区域
 */
public class Selection {
    public Object tag;
    public int x1, y1, z1, x2, y2, z2;
    public boolean ignoreY;

    public Selection(Object tag, int x1, int y1, int z1, int x2, int y2, int z2, boolean ignoreY) {
        this.tag = tag;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.ignoreY = ignoreY;
    }

    public Selection(Object tag, int x1, int y1, int z1, int x2, int y2, int z2) {
        this(tag, x1, y1, z1, x2, y2, z2, false);
    }

    public Selection(Object tag, int x1, int z1, int x2, int z2) {
        this(tag, x1, 0, z1, x2, 0, z2, true);
    }

    public Selection setPos1(int x1, int y1, int z1) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        return this;
    }

    public Selection setPos2(int x2, int y2, int z2) {
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        return this;
    }

    public Location getPos1(World world) {
        return new Location(world, x1, y1, z1);
    }

    public Location getPos2(World world) {
        return new Location(world, x2, y2, z2);
    }

    public Location getPos1(Entity entity) {
        return getPos1(entity.getWorld());
    }

    public Location getPos2(Entity entity) {
        return getPos2(entity.getWorld());
    }
}
