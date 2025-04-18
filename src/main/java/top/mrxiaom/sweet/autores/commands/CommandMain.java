package top.mrxiaom.sweet.autores.commands;
        
import com.google.common.collect.Lists;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.pluginbase.utils.ItemStackUtil;
import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.sweet.autores.SweetAutoResidence;
import top.mrxiaom.sweet.autores.func.AbstractModule;
import top.mrxiaom.sweet.autores.func.ItemsManager;
import top.mrxiaom.sweet.autores.func.entry.Item;

import java.util.*;

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
                return t(sender, "&e找不到物品 " + args[1]);
            }
            Integer i = args.length >= 3 ? Util.parseInt(args[2]).orElse(null) : Integer.valueOf(1);
            if (i == null) {
                return t(sender, "&e请输入一个整数");
            }
            Player player;
            if (args.length >= 4) {
                player = Util.getOnlinePlayer(args[3]).orElse(null);
                if (player == null) {
                    return t(sender, "&e玩家不在线");
                }
            } else {
                if (sender instanceof Player) {
                    player = (Player) sender;
                } else {
                    return t(sender, "&e该命令只能由玩家执行");
                }
            }
            ItemStackUtil.giveItemToPlayer(player, item.generateItem(i));
            return t(sender, "&a已给予玩家&e " + player.getName() + "&a 物品 " + item.itemDisplay + "&r &a共 &e" + i + "&a 个");
        }
        if (args.length == 1 && "reload".equalsIgnoreCase(args[0]) && sender.isOp()) {
            plugin.reloadConfig();
            return t(sender, "&a配置文件已重载");
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
