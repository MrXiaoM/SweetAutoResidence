package top.mrxiaom.sweet.autores.impl.residence;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.ResidencePlayer;
import com.bekvon.bukkit.residence.containers.lm;
import com.bekvon.bukkit.residence.permissions.PermissionGroup;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.CuboidArea;
import com.bekvon.bukkit.residence.selection.SelectionManager;
import net.Zrips.CMILib.Container.CMIWorld;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import top.mrxiaom.sweet.autores.api.Selection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <a href="https://github.com/Zrips/Residence/blob/master/src/com/bekvon/bukkit/residence/commands/auto.java">来自 Github</a>
 */
public class auto {
    public static Selection genSelection(Residence plugin, Player player, int configMaxX, int configMaxY, int configMaxZ) {
        Residence.getInstance().getPlayerManager().getResidencePlayer(player).forceUpdateGroup();
        Location loc = player.getLocation();

        int minY = loc.getBlockY();
        int maxY = loc.getBlockY();

        boolean ignoreY = plugin.getConfigManager().isSelectionIgnoreY();
        if (ignoreY) {
            minY = plugin.getSelectionManager().getSelection(player).getMinYAllowed();
            maxY = plugin.getSelectionManager().getSelection(player).getMaxYAllowed();
        }
        Location loc1 = loc.clone();
        loc1.setY(minY);
        Location loc2 = loc.clone();
        loc2.setY(maxY);
        World world = player.getWorld();
        CuboidArea cuboid = getResizedArea(plugin, world, loc1, loc2);

        boolean result = optimizedResize(plugin, player, cuboid, true, configMaxX, configMaxY, configMaxZ);

        //plugin.getSelectionManager().afterSelectionUpdate(player, true);

        if (!result) {
            Residence.getInstance().msg(player, lm.Area_SizeLimit);
            return null;
        }

        ClaimedResidence collision = Residence.getInstance()
                .getResidenceManager()
                .collidesWithResidence(plugin.getSelectionManager().getSelectionCuboid(player));

        if (collision != null) {
            Residence.getInstance().msg(player, lm.Area_Collision, collision.getResidenceName());
            return null;
        }

        Location low = cuboid.getLowLocation();
        Location high = cuboid.getHighLocation();
        return new Selection(null, low.getBlockX(), low.getBlockY(), low.getBlockZ(), high.getBlockX(), high.getBlockY(), high.getBlockZ(), ignoreY);
    }

    public static SelectionManager.selectionType getSelectionRestrictions(Residence plugin, Location loc1, Location loc2) {
        ClaimedResidence res1 = plugin.getResidenceManager().getByLoc(loc1.clone());
        boolean inSameResidence;
        if (res1 == null) {
            inSameResidence = false;
        } else {
            ClaimedResidence res2 = plugin.getResidenceManager().getByLoc(loc2.clone());
            inSameResidence = res2 != null && res1.getName().equals(res2.getName());
        }
        if (inSameResidence) {
            if (plugin.getConfigManager().isSelectionIgnoreYInSubzone()) {
                return SelectionManager.selectionType.residenceBounds;
            }
        } else if (plugin.getConfigManager().isSelectionIgnoreY()) {
            return SelectionManager.selectionType.ignoreY;
        }

        return SelectionManager.selectionType.noLimits;
    }
    public static CuboidArea getResizedArea(Residence plugin, World world, Location loc1, Location loc2) {
        CuboidArea area = new CuboidArea(loc1, loc2);
        SelectionManager.selectionType type = getSelectionRestrictions(plugin, loc1, loc2);
        switch (type) {
            case ignoreY:
            case residenceBounds: {
                area.setHighLocation(area.getHighLocation());
                int maxYAllowed;
                switch (type) {
                    case residenceBounds:
                        ClaimedResidence res1 = plugin.getResidenceManager().getByLoc(loc2);
                        if (res1 != null) {
                            CuboidArea area1 = res1.getAreaByLoc(loc2);
                            if (area1 != null) {
                                maxYAllowed = area1.getHighVector().getBlockY();
                                break;
                            }
                        }
                        maxYAllowed = getMaxWorldHeight(plugin, world);
                        break;
                    case ignoreY:
                    default:
                        maxYAllowed = getMaxWorldHeight(plugin, world);
                        break;
                }
                area.getHighVector().setY(maxYAllowed);
                area.setLowLocation(area.getLowLocation());
                int minYAllowed;
                switch (type) {
                    case residenceBounds:
                        ClaimedResidence res1 = plugin.getResidenceManager().getByLoc(loc1);
                        if (res1 != null) {
                            CuboidArea area1 = res1.getAreaByLoc(loc1);
                            if (area1 != null) {
                                minYAllowed = area1.getLowVector().getBlockY();
                                break;
                            }
                        }
                        minYAllowed = getMinWorldHeight(world);
                        break;
                    case ignoreY:
                    default:
                        minYAllowed = getMinWorldHeight(world);
                        break;
                }
                area.getLowVector().setY(minYAllowed);
            }
            case noLimits:
            default:
                return area;
        }
    }

    private static int getMinWorldHeight(World world) {
        try {
            return CMIWorld.getMinHeight(world);
        } catch (Throwable var4) {
            return 0;
        }
    }

    private static int getMaxWorldHeight(Residence plugin, World world) {
        if (world == null) {
            return 319;
        } else {
            switch (world.getEnvironment()) {
                case NETHER:
                    return plugin.getConfigManager().getSelectionNetherHeight();
                case NORMAL:
                case THE_END:
                default:
                    try {
                        return CMIWorld.getMaxHeight(world) - 1;
                    } catch (Throwable var4) {
                        return 319;
                    }
            }
        }
    }

    public static int getMax(int max) {
        if (!Residence.getInstance().getConfigManager().isARCSizeEnabled())
            return max;
        int arcMin = Residence.getInstance().getConfigManager().getARCSizeMin();
        int arcMax = Residence.getInstance().getConfigManager().getARCSizeMax();
        int maxV = (int) (max * (Residence.getInstance().getConfigManager().getARCSizePercentage() / 100D));
        maxV = maxV < arcMin && arcMin < max ? arcMin : maxV;
        maxV = Math.min(maxV, arcMax);
        return maxV;
    }

    public static void fillMaps(HashMap<SelectionDirection, Integer> directionMap, HashMap<SelectionDirection, Integer> maxMap, SelectionDirection dir, int max, int cubeSize) {
        int maxV = (int) ((max / 2D) - (cubeSize / 2D));
        directionMap.put(dir, maxV);
        maxMap.put(dir, maxV);
    }

    public static boolean optimizedResize(Residence plugin, Player player, CuboidArea cuboid, boolean checkBalance, int configMaxX, int configMaxY, int configMaxZ) {

        ResidencePlayer rPlayer = plugin.getPlayerManager().getResidencePlayer(player);
        PermissionGroup group = rPlayer.getGroup();

        SelectionDirection dir = SelectionDirection.Top;

        List<SelectionDirection> locked = new ArrayList<>();
        List<SelectionDirection> permaLocked = new ArrayList<>();

        boolean checkCollision = plugin.getConfigManager().isARCCheckCollision();

        if (checkCollision && plugin.getResidenceManager().collidesWithResidence(cuboid) != null) {
            return false;
        }

        int skipped = 0;
        int done = 0;

        int maxWorldY = 320;//group.getMaxYSize();
        int minWorldY = 3;//group.getMinYSize();
        int lowestY = -64;
        int highestY = 320;

        int groupMaxX = rPlayer.getMaxX();
        int groupMaxZ = rPlayer.getMaxZ();

        int maxX = getMax(groupMaxX);
        int maxY = getMax(maxWorldY);

        int maxZ = getMax(groupMaxZ);

        if (maxX > configMaxX && configMaxX > 0)
            maxX = configMaxX;
        if (!Residence.getInstance().getConfigManager().isSelectionIgnoreY() && maxY > configMaxY && configMaxY > 0)
            maxY = configMaxY;
        if (maxZ > configMaxZ && configMaxZ > 0)
            maxZ = configMaxZ;

        if (maxX <= 1)
            maxX = (groupMaxX - group.getMinX()) / 2 + group.getMinX();

        if (maxY <= 1)
            maxY = (maxWorldY - minWorldY) / 2 + minWorldY;

        if (maxZ <= 1)
            maxZ = (groupMaxZ - group.getMinZ()) / 2 + group.getMinZ();

        int gap = plugin.getConfigManager().getAntiGreefRangeGaps(cuboid.getWorldName());

        HashMap<SelectionDirection, Integer> directionMap = new HashMap<>();
        HashMap<SelectionDirection, Integer> maxMap = new HashMap<>();
        CuboidArea originalCuboid = new CuboidArea(cuboid.getLowLocation(), cuboid.getHighLocation());

        int smallestRange = Math.min(maxX - cuboid.getXSize(), maxY - cuboid.getYSize());
        smallestRange = Math.min(smallestRange, maxZ - cuboid.getZSize());
        smallestRange = smallestRange / 4;

        int worldMinY = CMIWorld.getMinHeight(cuboid.getWorld());

        int minYAltitude = Math.max(worldMinY, lowestY);

        while (true) {
            done++;
            // fail-safe if loop keeps going on
            if (done > 100) {
                break;
            }

            if (Math.abs(smallestRange) < 1) {
                break;
            }

            CuboidArea c = new CuboidArea();
            c.setLowLocation(cuboid.getLowLocation().clone().add(-smallestRange, -smallestRange, -smallestRange));
            c.setHighLocation(cuboid.getHighLocation().clone().add(smallestRange, smallestRange, smallestRange));

            if (c.getHighVector().getBlockY() > highestY) {
                c.setHighVector(c.getHighVector().setY(highestY));
            }

            if (c.getLowVector().getBlockY() > maxWorldY - 1) {
                c.setLowVector(c.getLowVector().setY(maxWorldY - 1));
            } else if (c.getLowVector().getBlockY() < minYAltitude) {
                c.setLowVector(c.getLowVector().setY(minYAltitude));
            }
            if (checkCollision) {

                if (gap > 0) {
                    CuboidArea temp = new CuboidArea(c.getLowLocation().clone().add(-gap, -gap, -gap), c.getHighLocation().clone().add(gap, gap, gap));

                    if (plugin.getResidenceManager().collidesWithResidence(temp) != null) {
                        smallestRange = (int) -(Math.ceil(Math.abs(smallestRange) / 2D));
                        cuboid.setLowLocation(c.getLowLocation());
                        cuboid.setHighLocation(c.getHighLocation());
                        continue;
                    }

                } else {
                    if (plugin.getResidenceManager().collidesWithResidence(c) != null) {
                        smallestRange = (int) -(Math.ceil(Math.abs(smallestRange) / 2D));
                        cuboid.setLowLocation(c.getLowLocation());
                        cuboid.setHighLocation(c.getHighLocation());
                        continue;
                    }
                }

                if (smallestRange == -1) {
                    cuboid.setLowLocation(cuboid.getLowLocation().clone().add(1, 1, 1));
                    cuboid.setHighLocation(cuboid.getHighLocation().clone().add(-1, -1, -1));
                    break;
                }
            }

            int sr = (int) Math.ceil(Math.abs(smallestRange) / 2D);

            if (maxX > 0 && maxX < c.getXSize() || c.getXSize() > groupMaxX) {
                break;
            }

            if (!Residence.getInstance().getConfigManager().isSelectionIgnoreY() && (maxY > 0 && maxY < c.getYSize() || c.getYSize() > maxWorldY + (-minWorldY))) {
                break;
            }

            if (maxZ > 0 && maxZ < c.getZSize() || c.getZSize() > groupMaxZ) {
                break;
            }

            cuboid.setLowLocation(c.getLowLocation());
            cuboid.setHighLocation(c.getHighLocation());

            smallestRange = sr;
        }

        if (cuboid.getXSize() < 1) {
            int center = (int) (originalCuboid.getLowVector().getX() + ((originalCuboid.getHighVector().getX() - originalCuboid.getLowVector().getX()) / 2));
            cuboid.getLowVector().setX(center);
            cuboid.getHighVector().setX(center);
        }

        if (cuboid.getZSize() < 1) {
            int center = (int) (originalCuboid.getLowVector().getZ() + ((originalCuboid.getHighVector().getZ() - originalCuboid.getLowVector().getZ()) / 2));
            cuboid.getLowVector().setZ(center);
            cuboid.getHighVector().setZ(center);
        }

        if (cuboid.getYSize() < 1) {
            int center = (int) (originalCuboid.getLowVector().getY() + ((originalCuboid.getHighVector().getY() - originalCuboid.getLowVector().getY()) / 2));
            cuboid.getLowVector().setY(center);
            cuboid.getHighVector().setY(center);
        }

        fillMaps(directionMap, maxMap, SelectionDirection.East, maxX, cuboid.getXSize());
        fillMaps(directionMap, maxMap, SelectionDirection.West, maxX + 1, cuboid.getXSize());

        fillMaps(directionMap, maxMap, SelectionDirection.South, maxZ, cuboid.getZSize());
        fillMaps(directionMap, maxMap, SelectionDirection.North, maxZ + 1, cuboid.getZSize());

        fillMaps(directionMap, maxMap, SelectionDirection.Top, maxY, cuboid.getYSize());
        fillMaps(directionMap, maxMap, SelectionDirection.Bottom, maxY + 1, cuboid.getYSize());

        while (true) {
            if (Residence.getInstance().getConfigManager().isSelectionIgnoreY() && (dir.equals(SelectionDirection.Top) || dir.equals(SelectionDirection.Bottom))) {
                dir = dir.getNext();
                continue;
            }
            done++;

            if (skipped >= 6) {
                break;
            }

            // fail-safe if loop keeps going on
            if (done > 100) {
                break;
            }

            if (locked.contains(dir)) {
                dir = dir.getNext();
                skipped++;
                continue;
            }

            skipped = 0;

            Integer offset = directionMap.get(dir);

            if (Math.abs(offset) == 0) {

                if (dir == SelectionDirection.East && locked.contains(SelectionDirection.West) && !permaLocked.contains(SelectionDirection.West)) {
                    maxMap.put(SelectionDirection.West, maxX - (player.getLocation().getBlockX() - cuboid.getHighVector().getBlockX()));
                    directionMap.put(SelectionDirection.West, maxMap.get(SelectionDirection.West) / 2);
                    locked.remove(SelectionDirection.West);
                    permaLocked.add(dir);
                }

                if (dir == SelectionDirection.West && locked.contains(SelectionDirection.East) && !permaLocked.contains(SelectionDirection.East)) {
                    maxMap.put(SelectionDirection.East, maxX - (player.getLocation().getBlockX() - cuboid.getHighVector().getBlockX()));
                    directionMap.put(SelectionDirection.East, maxMap.get(SelectionDirection.East) / 2);
                    locked.remove(SelectionDirection.East);
                    permaLocked.add(dir);
                }

                if (dir == SelectionDirection.North && locked.contains(SelectionDirection.South) && !permaLocked.contains(SelectionDirection.South)) {
                    maxMap.put(SelectionDirection.South, maxX - (player.getLocation().getBlockZ() - cuboid.getHighVector().getBlockZ()));
                    directionMap.put(SelectionDirection.South, maxMap.get(SelectionDirection.South) / 2);
                    locked.remove(SelectionDirection.South);
                    permaLocked.add(dir);
                }

                if (dir == SelectionDirection.South && locked.contains(SelectionDirection.North) && !permaLocked.contains(SelectionDirection.North)) {
                    maxMap.put(SelectionDirection.North, maxX - (player.getLocation().getBlockZ() - cuboid.getHighVector().getBlockZ()));
                    directionMap.put(SelectionDirection.North, maxMap.get(SelectionDirection.North) / 2);
                    locked.remove(SelectionDirection.North);
                    permaLocked.add(dir);
                }

                if (dir == SelectionDirection.Top && locked.contains(SelectionDirection.Bottom) && !permaLocked.contains(SelectionDirection.Bottom)) {
                    maxMap.put(SelectionDirection.Bottom, maxY - Math.abs(player.getLocation().getBlockY() - cuboid.getLowVector().getBlockY()));
                    directionMap.put(SelectionDirection.Bottom, maxMap.get(SelectionDirection.Bottom) / 2);
                    locked.remove(SelectionDirection.Bottom);
                    permaLocked.add(dir);
                }

                if (dir == SelectionDirection.Bottom && locked.contains(SelectionDirection.Top) && !permaLocked.contains(SelectionDirection.Top)) {
                    maxMap.put(SelectionDirection.Top, maxY - Math.abs(player.getLocation().getBlockY() - cuboid.getHighVector().getBlockY()));
                    directionMap.put(SelectionDirection.Top, maxMap.get(SelectionDirection.Top) / 2);
                    locked.remove(SelectionDirection.Top);
                    permaLocked.add(dir);
                }

                locked.add(dir);
                dir = dir.getNext();
                continue;
            }

            CuboidArea c = new CuboidArea();
            c.setLowLocation(cuboid.getLowLocation().clone().add(-dir.getLow().getX() * offset, -dir.getLow().getY() * offset, -dir.getLow().getZ() * offset));
            c.setHighLocation(cuboid.getHighLocation().clone().add(dir.getHigh().getX() * offset, dir.getHigh().getY() * offset, dir.getHigh().getZ() * offset));

            if (c.getHighVector().getBlockY() > highestY) {
                c.setHighVector(c.getHighVector().setY(highestY));
                if (locked.contains(SelectionDirection.Top) && !locked.contains(SelectionDirection.Bottom) && !permaLocked.contains(SelectionDirection.Top)) {
                    maxMap.put(SelectionDirection.Top, maxY - Math.abs(c.getHighVector().getBlockY() - player.getLocation().getBlockY()));
                    directionMap.put(SelectionDirection.Top, maxMap.get(SelectionDirection.Top) / 2);
                    locked.remove(SelectionDirection.Top);
                    permaLocked.add(SelectionDirection.Bottom);
                }
            }

            if (c.getLowVector().getBlockY() > highestY - 1) {
                c.setLowVector(c.getLowVector().setY(highestY - 1));
            } else if (c.getLowVector().getBlockY() < minYAltitude) {
                c.setLowVector(c.getLowVector().setY(minYAltitude));
                if (!locked.contains(SelectionDirection.Top) && !locked.contains(SelectionDirection.Bottom) && !permaLocked.contains(SelectionDirection.Bottom)) {
                    maxMap.put(SelectionDirection.Bottom, maxY - Math.abs(player.getLocation().getBlockY() - c.getLowVector().getBlockY()));
                    directionMap.put(SelectionDirection.Bottom, maxMap.get(SelectionDirection.Bottom) / 2);
                    permaLocked.add(SelectionDirection.Top);
                }
            }

            if (checkCollision) {
                boolean collides;
                if (gap > 0) {
                    CuboidArea temp = new CuboidArea(c.getLowLocation().clone().add(-gap, -gap, -gap), c.getHighLocation().clone().add(gap, gap, gap));
                    collides = plugin.getResidenceManager().collidesWithResidence(temp) != null;
                } else {
                    collides = plugin.getResidenceManager().collidesWithResidence(c) != null;
                }
                if (collides) {
                    int newOffset = (int) (Math.abs(offset) / 2D);
                    if (newOffset < 1)
                        newOffset = 1;
                    directionMap.put(dir, -(newOffset));
                    cuboid.setLowLocation(c.getLowLocation());
                    cuboid.setHighLocation(c.getHighLocation());
                    continue;
                }
            }

            if (maxMap.get(dir).equals(Math.abs(offset))) {
                locked.add(dir);
            }

            double newOffset = (Math.abs(offset) / 2D);

            offset = newOffset > 1 ? (int) Math.ceil(newOffset) : (int) newOffset;

            directionMap.put(dir, offset);

            if (maxX > 0 && maxX < c.getXSize() || c.getXSize() > groupMaxX) {
                if (Math.abs(offset) < 1)
                    locked.add(dir);
                dir = dir.getNext();
                continue;
            }

            if (!Residence.getInstance().getConfigManager().isSelectionIgnoreY() && (maxY > 0 && maxY < c.getYSize() || c.getYSize() > maxY)) {
                if (Math.abs(offset) < 1) {
                    locked.add(dir);
                }
                dir = dir.getNext();
                continue;
            }

            if (maxZ > 0 && maxZ < c.getZSize() || c.getZSize() > groupMaxZ) {
                if (Math.abs(offset) < 1)
                    locked.add(dir);
                dir = dir.getNext();
                continue;
            }

            if (checkBalance && plugin.getConfigManager().enableEconomy() && !Residence.getInstance().getEconomyManager().canAfford(player, c.getCost(group))) {
                plugin.msg(player, lm.Economy_NotEnoughMoney);
                return false;
            }

            cuboid.setLowLocation(c.getLowLocation());
            cuboid.setHighLocation(c.getHighLocation());

            dir = dir.getNext();
        }

        plugin.getSelectionManager().placeLoc1(player, cuboid.getLowLocation());
        plugin.getSelectionManager().placeLoc2(player, cuboid.getHighLocation());

        cuboid = plugin.getSelectionManager().getSelectionCuboid(player);

        return cuboid.getXSize() <= groupMaxX && cuboid.getYSize() <= maxY && cuboid.getZSize() <= groupMaxZ;
    }

    public enum SelectionDirection {
        Top(new Vector(0, 1, 0), new Vector(0, 0, 0)),
        Bottom(new Vector(0, 0, 0), new Vector(0, 1, 0)),
        East(new Vector(1, 0, 0), new Vector(0, 0, 0)),
        West(new Vector(0, 0, 0), new Vector(1, 0, 0)),
        North(new Vector(0, 0, 1), new Vector(0, 0, 0)),
        South(new Vector(0, 0, 0), new Vector(0, 0, 1));

        private final Vector low;
        private final Vector high;

        SelectionDirection(Vector low, Vector high) {
            this.low = low;
            this.high = high;
        }

        public Vector getLow() {
            return low;
        }

        public Vector getHigh() {
            return high;
        }

        public SelectionDirection getNext() {
            boolean next = false;
            SelectionDirection dir = SelectionDirection.Top;
            for (SelectionDirection one : SelectionDirection.values()) {
                if (next) {
                    dir = one;
                    break;
                }
                if (this.equals(one)) {
                    next = true;
                }
            }
            return dir;
        }

    }

}
