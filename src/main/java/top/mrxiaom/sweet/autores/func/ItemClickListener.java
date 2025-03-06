package top.mrxiaom.sweet.autores.func;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.sweet.autores.SweetAutoResidence;
import top.mrxiaom.sweet.autores.api.Selection;
import top.mrxiaom.sweet.autores.func.entry.Item;
import top.mrxiaom.sweet.autores.func.entry.SelectionCache;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AutoRegister
public class ItemClickListener extends AbstractModule implements Listener {
    private final Map<UUID, SelectionCache> caches = new HashMap<>();
    public ItemClickListener(SweetAutoResidence plugin) {
        super(plugin);
        registerEvents();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        removeCache(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        removeCache(e.getPlayer());
    }

    private void removeCache(Player player) {
        caches.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(PlayerInteractEvent e) {
        if (e.useItemInHand().equals(Event.Result.DENY)) return;
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && !e.getAction().equals(Action.RIGHT_CLICK_AIR)) return;
        EquipmentSlot hand = e.getHand();
        ItemStack itemStack = e.getItem();
        if (hand == null || itemStack == null) return;
        Player player = e.getPlayer();
        Item item = ItemsManager.inst().match(itemStack);
        if (item == null) return;
        UUID uuid = player.getUniqueId();
        if (player.isSneaking()) { // 确认圈地
            SelectionCache cache = caches.remove(uuid);
            if (cache == null || !cache.isValid(player, item)) {
                t(player, "&e请先手持道具，右键点击查看圈地范围");
                return;
            }
            if (plugin.getAdapter().hasReachResCountLimit(player)) {
                t(player, "&e你的领地数量已到达上限");
                return;
            }
            String resName = item.genResName(plugin.getAdapter(), player);
            if (resName == null) {
                t(player, "&e同名的领地已存在，无法创建");
                return;
            }
            player.getInventory().setItem(hand, null);
            plugin.getAdapter().createResidence(player, resName, cache.selection);
        } else { // 选择区域
            caches.remove(uuid);
            Selection selection = plugin.getAdapter().genAutoSelection(player, item.sizeX, item.sizeY, item.sizeZ);
            if (selection == null) return;
            caches.put(uuid, new SelectionCache(player, player.getWorld(), item.id, selection));
            t(player, "&a已选中区域，Shift+右键确认圈地");
        }
    }
}
