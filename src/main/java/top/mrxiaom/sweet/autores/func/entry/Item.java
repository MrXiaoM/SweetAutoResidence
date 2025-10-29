package top.mrxiaom.sweet.autores.func.entry;

import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.actions.ActionProviders;
import top.mrxiaom.pluginbase.api.IAction;
import top.mrxiaom.pluginbase.utils.AdventureItemStack;
import top.mrxiaom.pluginbase.utils.ItemStackUtil;
import top.mrxiaom.pluginbase.utils.depend.PAPI;
import top.mrxiaom.pluginbase.utils.Pair;
import top.mrxiaom.sweet.autores.Messages;
import top.mrxiaom.sweet.autores.SweetAutoResidence;
import top.mrxiaom.sweet.autores.api.IResidenceAdapter;
import top.mrxiaom.sweet.autores.conditions.ICondition;
import top.mrxiaom.sweet.autores.conditions.NumberCondition;
import top.mrxiaom.sweet.autores.func.ItemsManager;

import java.util.ArrayList;
import java.util.List;

import static top.mrxiaom.pluginbase.actions.ActionProviders.loadActions;

public class Item {
    public final String id;
    public final String nameFormat;
    public final boolean nameUseAliasIfExists;
    public final int sizeX, sizeY, sizeZ;
    public final List<ICondition> conditionsList;
    public final List<IAction> conditionsDenyCommands;
    public final String itemMaterial;
    public final String itemDisplay;
    public final List<String> itemLore;
    public final List<IAction> useCommands;

    public Item(String id, YamlConfiguration config, int sizeX, int sizeY, int sizeZ) {
        this.id = id;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;

        ConfigurationSection section;
        this.nameFormat = config.getString("res-name.format");
        this.nameUseAliasIfExists = config.getBoolean("res-name.use-alias-if-exists");

        this.conditionsList = new ArrayList<>();
        this.conditionsDenyCommands = new ArrayList<>();
        section = config.getConfigurationSection("conditions");
        if (section != null) for (String key : section.getKeys(false)) {
            if (key.equals("deny-commands")) {
                conditionsDenyCommands.addAll(loadActions(section.getStringList(key)));
                continue;
            }
            String typeStr = section.getString(key + ".type");
            if (typeStr == null) continue;
            boolean reversed = typeStr.startsWith("!");
            String type = reversed ? (typeStr.substring(1)) : typeStr;
            NumberCondition.Operator numberOperator = NumberCondition.Operator.parse(type);
            if (numberOperator != null) {
                String input = section.getString(key + ".input", "");
                String output = section.getString(key + ".output", "");
                List<IAction> denyCommands = loadActions(section, key + ".deny-commands");
                conditionsList.add(new NumberCondition(reversed, input, numberOperator, output, denyCommands));
            }
        }

        this.itemMaterial = config.getString("item.material");
        this.itemDisplay = config.getString("item.display");
        this.itemLore = config.getStringList("item.lore");

        this.useCommands = ActionProviders.loadActions(config, "use-commands");
    }

    @Nullable
    public String genResName(IResidenceAdapter adapter, Player owner) {
        if (nameUseAliasIfExists) {
            for (int i = 0; i < 50; i++) {
                String res;
                if (i > 0) {
                    res = PAPI.setPlaceholders(owner, nameFormat) + "_" + i;
                } else {
                    res = PAPI.setPlaceholders(owner, nameFormat);
                }
                if (!adapter.isResidenceExists(res)) {
                    return res;
                }
            }
            return null;
        }
        String res = PAPI.setPlaceholders(owner, nameFormat);
        return adapter.isResidenceExists(res) ? null : res;
    }

    public boolean checkDeny(Player player) {
        boolean match = true;
        for (ICondition condition : conditionsList) {
            if (condition.match(player)) continue;
            match = false;
            for (IAction action : condition.getDenyCommands()) {
                action.run(player);
            }
        }
        if (!match) {
            for (IAction action : conditionsDenyCommands) {
                action.run(player);
            }
            return true;
        }
        return false;
    }

    @Nullable
    public ItemStack generateItem() {
        return generateItem(1);
    }

    @Nullable
    public ItemStack generateItem(int amount) {
        return generateItem(amount, null);
    }

    @Nullable
    public ItemStack generateItem(int amount, @Nullable Player bindPlayer) {
        if (amount <= 0) {
            return null;
        }
        ItemStack item = ItemStackUtil.getItem(itemMaterial, true);
        if (item == null || item.getType().equals(Material.AIR)) {
            return null;
        }
        int stackSize = item.getType().getMaxStackSize();
        item.setAmount(Math.min(amount, stackSize));
        AdventureItemStack.setItemDisplayName(item, itemDisplay);
        if (bindPlayer == null) {
            AdventureItemStack.setItemLoreMiniMessage(item, itemLore);
        } else {
            List<String> lore = new ArrayList<>(itemLore);
            lore.addAll(Messages.item__lore_bind.list(Pair.of("%player%", bindPlayer.getName())));
            AdventureItemStack.setItemLoreMiniMessage(item, lore);
        }
        NBT.modify(item, nbt -> {
            nbt.setString("SWEET_AUTO_RESIDENCE_ID", id);
            if (bindPlayer != null) {
                nbt.setUUID("SWEET_AUTO_RESIDENCE_BIND_UUID", bindPlayer.getUniqueId());
                nbt.setString("SWEET_AUTO_RESIDENCE_BIND_NAME", bindPlayer.getName());
            }
        });
        return item;
    }

    public void executeUseCommands(Player player, String resName) {
        List<Pair<String, Object>> pairs = new ArrayList<>();
        pairs.add(Pair.of("%name%", resName));
        ActionProviders.run(SweetAutoResidence.getInstance(), player, useCommands, pairs);
    }

    @Nullable
    public static Item load(ItemsManager parent, YamlConfiguration config, String id) {
        int sizeX = config.getInt("size.x"),
            sizeY = config.getInt("size.y"),
            sizeZ = config.getInt("size.z");
        if (sizeX <= 0 || sizeZ <= 0 || sizeY < 0) {
            parent.warn("[items/" + id + "] 领地大小不能小于等于0");
            return null;
        }
        return new Item(id, config, sizeX, sizeY, sizeZ);
    }
}
