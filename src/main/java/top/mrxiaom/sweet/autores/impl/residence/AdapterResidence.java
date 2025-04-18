package top.mrxiaom.sweet.autores.impl.residence;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.ResidencePlayer;
import com.bekvon.bukkit.residence.containers.Visualizer;
import com.bekvon.bukkit.residence.protection.CuboidArea;
import com.bekvon.bukkit.residence.selection.SelectionManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.sweet.autores.SweetAutoResidence;
import top.mrxiaom.sweet.autores.api.IResidenceAdapter;
import top.mrxiaom.sweet.autores.api.Selection;
import top.mrxiaom.sweet.autores.func.AbstractPluginHolder;

@SuppressWarnings("unused")
public class AdapterResidence extends AbstractPluginHolder implements IResidenceAdapter {
    Residence residence = Residence.getInstance();
    public AdapterResidence(SweetAutoResidence plugin) {
        super(plugin);
    }

    @Override
    public @NotNull String getName() {
        return "Residence " + residence.getDescription().getVersion();
    }

    @Override
    public @Nullable Selection genAutoSelection(Player player, int xSize, int ySize, int zSize) {
        return auto.genSelection(residence, player, xSize, ySize, zSize);
    }

    @Override
    public boolean isResidenceExists(String resName) {
        return residence.getResidenceManager().getByName(resName) != null;
    }

    @Override
    public void createResidence(Player player, String resName, Selection area) {
        World world = player.getWorld();
        Location loc1 = new Location(world, area.x1, area.y1, area.z1);
        Location loc2 = new Location(world, area.x2, area.y2, area.z2);
        residence.getResidenceManager().addResidence(player, resName, loc1, loc2, true);
    }

    @Override
    public int getResidenceCount(Player player) {
        return residence.getPlayerManager().getResidenceCount(player.getUniqueId());
    }

    @Override
    public int getResidenceMaxCount(Player player) {
        ResidencePlayer p = residence.getPlayerManager().getResidencePlayer(player);
        return p.getMaxRes();
    }

    @Override
    public void showSelection(Player player, Selection area) {
        SelectionManager manager = Residence.getInstance().getSelectionManager();
        Visualizer v = new Visualizer(player);

        v.setStart(System.currentTimeMillis());
        v.cancelAll();

        v.setAreas(new CuboidArea(area.getPos1(player), area.getPos2(player)));
        v.setOnce(false);
        manager.showBounds(player, v);
    }
}
