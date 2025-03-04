package top.mrxiaom.sweet.autores.impl.residence;

import com.bekvon.bukkit.residence.Residence;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
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
    public String getName() {
        return "Residence " + residence.getDescription().getVersion();
    }

    @Override
    public Selection genAutoSelection(Player player, int xSize, int ySize, int zSize) {
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
        // TODO: resadmin 有风险，应该判断限制领地数量
        residence.getResidenceManager().addResidence(player, resName, loc1, loc2, true);
    }

    @Override
    public int getResidenceCount(Player player) {
        return residence.getPlayerManager().getResidenceCount(player.getUniqueId());
    }
}
