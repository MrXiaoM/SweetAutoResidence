package top.mrxiaom.sweet.autores.impl.residence;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.DominionInterface;
import cn.lunadeer.dominion.api.DominionAPI;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.configuration.Limitation;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.sweet.autores.SweetAutoResidence;
import top.mrxiaom.sweet.autores.api.IResidenceAdapter;
import top.mrxiaom.sweet.autores.api.Selection;
import top.mrxiaom.sweet.autores.func.AbstractPluginHolder;

@SuppressWarnings({"unused", "UnstableApiUsage"})
public class AdapterDominion extends AbstractPluginHolder implements IResidenceAdapter {
    DominionAPI dominionAPI;
    Dominion dominion;
    public AdapterDominion(SweetAutoResidence plugin) {
        super(plugin);
        dominion = Dominion.instance;
        dominionAPI = DominionInterface.instance;
    }

    @Override
    public @NotNull String getName() {
        return "Dominion " + dominion.getDescription().getVersion();
    }

    @Override
    public @Nullable Selection genAutoSelection(Player player, int xSize, int ySize, int zSize) {
        // TODO: 生成自动选区
        return null;
    }

    @Override
    public boolean isResidenceExists(String resName) {
        try {
            dominionAPI.getDominion(resName);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public void createResidence(Player player, String resName, Selection area) {
        // TODO: 创建领地
    }

    @Override
    public int getResidenceCount(Player player) {
        return dominionAPI.getPlayerOwnDominionDTOs(player.getUniqueId()).size();
    }

    @Override
    public int getResidenceMaxCount(Player player) {
        Limitation limitation = Configuration.getPlayerLimitation(player);
        Limitation.WorldLimitationSetting settings = limitation.getWorldSettings(player.getWorld());
        return settings.amount;
    }
}
