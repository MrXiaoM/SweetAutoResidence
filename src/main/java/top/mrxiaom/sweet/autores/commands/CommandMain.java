package top.mrxiaom.sweet.autores.commands;

import com.google.common.collect.Lists;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.pluginbase.utils.ItemStackUtil;
import top.mrxiaom.pluginbase.utils.Pair;
import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.sweet.autores.Messages;
import top.mrxiaom.sweet.autores.SweetAutoResidence;
import top.mrxiaom.sweet.autores.func.AbstractModule;
import top.mrxiaom.sweet.autores.func.ItemsManager;
import top.mrxiaom.sweet.autores.func.entry.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AutoRegister
public class CommandMain extends AbstractModule implements CommandExecutor, TabCompleter, Listener {
    public CommandMain(SweetAutoResidence plugin) {
        super(plugin);
        registerCommand("sweetautoresidence", this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length >= 2 && "give".equalsIgnoreCase(args[0]) && sender.isOp()) {
            Item item = ItemsManager.inst().get(args[1]);
            if (item == null) {
                return Messages.command__give_item_not_found.tm(sender, Pair.of("%item%", args[1]));
            }
            Integer i = args.length >= 3 ? Util.parseInt(args[2]).orElse(null) : Integer.valueOf(1);
            if (i == null) {
                return Messages.not_integer.tm(sender);
            }
            boolean bind = false;
            for (String s : args) {
                if (s.equals("-b") | s.equals("--bind")) {
                    bind = true;
                    break;
                }
            }
            Player player;
            if (args.length >= 4 && !args[3].equals("-b") && !args[3].equals("--bind")) {
                player = Util.getOnlinePlayer(args[3]).orElse(null);
                if (player == null) {
                    return Messages.player_not_online.tm(sender);
                }
            } else {
                if (sender instanceof Player) {
                    player = (Player) sender;
                } else {
                    return Messages.player_only.tm(sender);
                }
            }
            // 物品栏只能在目标玩家所属的 region 线程访问，
            // 控制台执行时(全局区域线程)或跨区域发道具会抛异常
            final boolean bindToPlayer = bind;
            plugin.getScheduler().runAtEntity(player, p -> {
                ItemStack itemStack = item.generateItem(i, bindToPlayer ? p : null);
                if (itemStack != null) {
                    ItemStackUtil.giveItemToPlayer(p, itemStack);
                }
                Messages.command__give_success.tm(sender,
                        Pair.of("%player%", p.getName()),
                        Pair.of("%item%", item.itemDisplay),
                        Pair.of("%count%", i));
            });
            return true;
        }
        if (args.length == 1 && "reload".equalsIgnoreCase(args[0]) && sender.isOp()) {
            plugin.reloadConfig();
            return Messages.command__reload.tm(sender);
        }
        return true;
    }

    private static final List<String> emptyList = Lists.newArrayList();
    private static final List<String> listArg0 = Lists.newArrayList();
    private static final List<String> listOpArg0 = Lists.newArrayList(
            "give", "reload");
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return startsWith(sender.isOp() ? listOpArg0 : listArg0, args[0]);
        }
        if (args.length == 2) {
            if ("give".equalsIgnoreCase(args[0]) && sender.isOp()) {
                return startsWith(ItemsManager.inst().keys(), args[1]);
            }
        }
        if (args.length == 4) {
            if ("give".equalsIgnoreCase(args[0]) && sender.isOp()) {
                return null;
            }
        }
        return emptyList;
    }

    public List<String> startsWith(Collection<String> list, String s) {
        return startsWith(null, list, s);
    }
    public List<String> startsWith(String[] addition, Collection<String> list, String s) {
        String s1 = s.toLowerCase();
        List<String> stringList = new ArrayList<>(list);
        if (addition != null) stringList.addAll(0, Lists.newArrayList(addition));
        stringList.removeIf(it -> !it.toLowerCase().startsWith(s1));
        return stringList;
    }
}
