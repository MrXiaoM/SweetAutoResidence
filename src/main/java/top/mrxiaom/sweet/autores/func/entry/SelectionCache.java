package top.mrxiaom.sweet.autores.func.entry;

import org.bukkit.World;
import org.bukkit.entity.Player;
import top.mrxiaom.sweet.autores.api.Selection;

public class SelectionCache {
    public Player player;
    public World world;
    public String itemId;
    public Selection selection;
    public long startTime = System.currentTimeMillis();

    public SelectionCache(Player player, World world, String itemId, Selection selection) {
        this.player = player;
        this.world = world;
        this.itemId = itemId;
        this.selection = selection;
    }

    @SuppressWarnings("RedundantIfStatement")
    public boolean isValid(Player player, Item item) {
        if (!itemId.equals(item.id)) return false;
        if (!world.getName().equals(player.getWorld().getName())) return false;
        if (System.currentTimeMillis() - startTime > 30000L) return false; // 30 秒内创建领地
        return true;
    }
}
