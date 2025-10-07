package top.mrxiaom.sweet.autores.impl.dominion;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.DominionInterface;
import cn.lunadeer.dominion.api.DominionAPI;
import cn.lunadeer.dominion.api.dtos.CuboidDTO;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.configuration.Limitation;
import cn.lunadeer.dominion.misc.Others;
import cn.lunadeer.dominion.providers.DominionProvider;
import cn.lunadeer.dominion.utils.ParticleUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.sweet.autores.SweetAutoResidence;
import top.mrxiaom.sweet.autores.api.IResidenceAdapter;
import top.mrxiaom.sweet.autores.api.Selection;
import top.mrxiaom.sweet.autores.func.AbstractPluginHolder;

import java.util.List;
import java.util.UUID;

@SuppressWarnings({"unused"})
public class AdapterDominion extends AbstractPluginHolder implements IResidenceAdapter {
    DominionAPI dominionAPI;
    Dominion dominion;
    public AdapterDominion(SweetAutoResidence plugin) {
        super(plugin);
        dominion = Dominion.instance;
        dominionAPI = DominionInterface.getInstance();
    }

    @Override
    public @NotNull String getName() {
        return "Dominion " + dominion.getDescription().getVersion();
    }

    @Override
    public @Nullable Selection genAutoSelection(Player player, int xSize, int ySize, int zSize) {
        World world = player.getWorld();
        UUID worldUID = world.getUID();

        // 自动选区方法
        // cn.lunadeer.dominion.misc.Others#autoPoints
        Location location = player.getLocation();
        Location location1 = new Location(location.getWorld(), location.getX() - ((double)xSize / 2.0), location.getY() - ((double)ySize / 2.0), location.getZ() - ((double)zSize / 2.0));
        Location location2 = new Location(location.getWorld(), location.getX() + ((double)xSize / 2.0), location.getY() + ((double)ySize / 2.0), location.getZ() + ((double)zSize / 2.0));
        if (Configuration.getPlayerLimitation(player).getWorldSettings(player.getWorld()).autoIncludeVertical) {
            location1.setY(Configuration.getPlayerLimitation(player).getWorldSettings(player.getWorld()).noLowerThan);
            location2.setY((Configuration.getPlayerLimitation(player).getWorldSettings(player.getWorld()).noHigherThan - 1));
        }
        CuboidDTO cuboidDTO = new CuboidDTO(location1, location2);

        // 与其它领地出现区域冲突时返回 null
        // cn.lunadeer.dominion.misc.Asserts#assertDominionIntersect
        List<DominionDTO> dominions = CacheManager.instance.getCache().getDominionCache().getChildrenOf(-1);
        for (DominionDTO dom : dominions) {
            if (!dom.getWorldUid().equals(worldUID)) {
                continue;
            }
            if (cuboidDTO.intersectWith(dom.getCuboid())) {
                return null;
            }
        }
        if (!Others.bypassLimit(player)) {
            int spawnProtection = Configuration.serverSpawnProtectionRadius;
            if (spawnProtection != -1) {
                Location spawn = world.getSpawnLocation();
                CuboidDTO spawnCuboid = new CuboidDTO(spawn.getBlockX() - spawnProtection, spawn.getBlockX() + spawnProtection,
                        spawn.getBlockY() - spawnProtection, spawn.getBlockY() + spawnProtection,
                        spawn.getBlockZ() - spawnProtection, spawn.getBlockZ() + spawnProtection);
                if (cuboidDTO.intersectWith(spawnCuboid)) {
                    return null;
                }
            }
        }
        return new Selection(cuboidDTO, cuboidDTO.x1(), cuboidDTO.y1(), cuboidDTO.z1(), cuboidDTO.x2(), cuboidDTO.y2(), cuboidDTO.z2());
    }

    @Override
    public boolean isResidenceExists(String resName) {
        try {
            return dominionAPI.getDominion(resName) != null;
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public void createResidence(Player player, String resName, Selection area) {
        CuboidDTO cuboidDTO = (CuboidDTO) area.tag;
        DominionProvider.getInstance().createDominion(
                /*operator:*/player,
                /*name:*/resName,
                /*owner:*/player.getUniqueId(),
                /*world:*/player.getWorld(), /*cuboid:*/cuboidDTO,
                /*parent:*/null,
                /*skipEconomy:*/true
        );
    }

    @Override
    public int getResidenceCount(Player player) {
        int count = 0;
        UUID worldUid = player.getWorld().getUID();
        // cn.lunadeer.dominion.misc.Asserts#assertPlayerDominionAmount
        List<DominionDTO> dominions = CacheManager.instance.getCache().getDominionCache().getPlayerOwnDominionDTOs(player.getUniqueId());
        for (DominionDTO dom : dominions) {
            if (dom.getWorldUid().equals(worldUid)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int getResidenceMaxCount(Player player) {
        Limitation limitation = Configuration.getPlayerLimitation(player);
        Limitation.WorldLimitationSetting settings = limitation.getWorldSettings(player.getWorld());
        return settings.amount;
    }

    @Override
    public void showSelection(Player player, Selection area) {
        CuboidDTO cuboidDTO = (CuboidDTO) area.tag;
        ParticleUtil.showBorder(player, player.getWorld(), cuboidDTO);
    }
}
