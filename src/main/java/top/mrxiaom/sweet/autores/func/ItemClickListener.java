package top.mrxiaom.sweet.autores.func;

import org.bukkit.Material;
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
import top.mrxiaom.sweet.autores.Messages;
import top.mrxiaom.sweet.autores.SweetAutoResidence;
import top.mrxiaom.sweet.autores.api.Selection;
import top.mrxiaom.sweet.autores.func.entry.Item;
import top.mrxiaom.sweet.autores.func.entry.SelectionCache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@AutoRegister
public class ItemClickListener extends AbstractModule implements Listener {
    private final Map<UUID, SelectionCache> caches = new ConcurrentHashMap<>();
    private final Set<UUID> lock = new HashSet<>();
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
        UUID uuid = player.getUniqueId();
        caches.remove(uuid);
        lock.remove(uuid);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(PlayerInteractEvent e) {
        if (e.useItemInHand().equals(Event.Result.DENY)) return;
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && !e.getAction().equals(Action.RIGHT_CLICK_AIR)) return;
        EquipmentSlot hand = e.getHand();
        ItemStack itemStack = e.getItem();
        if (hand == null || itemStack == null) return;
        if (hand.equals(EquipmentSlot.OFF_HAND)) return;
        Player player = e.getPlayer();
        Item item = ItemsManager.inst().match(itemStack);
        if (item == null) return;
        UUID uuid = player.getUniqueId();
        if (lock.contains(uuid)) return;
        lock.add(uuid);
        plugin.getScheduler().runTaskAsync(() -> {
            if (handleClick(player, uuid, item, itemStack)) {
                lock.remove(uuid);
            }
        });
    }

    private boolean handleClick(Player player, UUID uuid, Item item, ItemStack itemStack) {
        if (player.isSneaking()) { // 确认圈地
            SelectionCache cache = caches.remove(uuid);
            if (cache == null || !cache.isValid(player, item)) {
                Messages.create__no_selection.tm(player);
                return true;
            }
            if (item.checkDeny(player)) return true;
            if (plugin.getAdapter().hasReachResCountLimit(player)) {
                Messages.create__no_available.tm(player);
                return true;
            }
            String resName = item.genResName(plugin.getAdapter(), player);
            if (resName == null) {
                Messages.create__already_exists.tm(player);
                return true;
            }
            plugin.getScheduler().runTask(() -> {
                int amount = itemStack.getAmount();
                if (amount <= 1) {
                    itemStack.setAmount(0);
                    itemStack.setType(Material.AIR);
                } else {
                    itemStack.setAmount(amount - 1);
                }
                try {
                    plugin.getAdapter().createResidence(player, resName, cache.selection);
                    item.executeUseCommands(player, resName);
                } catch (Throwable t) {
                    warn(t);
                    Messages.create__failed.tm(player);
                }
                lock.remove(uuid);
            });
            return false;
        } else { // 选择区域
            caches.remove(uuid);
            Selection selection = plugin.getAdapter().genAutoSelection(player, item.sizeX, item.sizeY, item.sizeZ);
            if (selection == null) return true;
            caches.put(uuid, new SelectionCache(player, player.getWorld(), item.id, selection));
            Messages.selection__success.tm(player);
            plugin.getAdapter().showSelection(player, selection);
            return true;
        }
    }
}
