package top.mrxiaom.sweet.autores.func.entry;

import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.func.gui.actions.IAction;
import top.mrxiaom.pluginbase.utils.AdventureItemStack;
import top.mrxiaom.pluginbase.utils.ItemStackUtil;
import top.mrxiaom.sweet.autores.conditions.ICondition;
import top.mrxiaom.sweet.autores.func.ItemsManager;

import java.util.ArrayList;
import java.util.List;

import static top.mrxiaom.pluginbase.func.AbstractGuiModule.loadActions;

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

    public Item(String id, String nameFormat, boolean nameUseAliasIfExists, int sizeX, int sizeY, int sizeZ, List<ICondition> conditionsList, List<IAction> conditionsDenyCommands, String itemMaterial, String itemDisplay, List<String> itemLore) {
        this.id = id;
        this.nameFormat = nameFormat;
        this.nameUseAliasIfExists = nameUseAliasIfExists;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.conditionsList = conditionsList;
        this.conditionsDenyCommands = conditionsDenyCommands;
        this.itemMaterial = itemMaterial;
        this.itemDisplay = itemDisplay;
        this.itemLore = itemLore;
    }

    @Nullable
    public ItemStack generateItem() {
        return generateItem(1);
    }

    @Nullable
    public ItemStack generateItem(int amount) {
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
        AdventureItemStack.setItemLoreMiniMessage(item, itemLore);
        NBT.modify(item, nbt -> {
            nbt.setString("SWEET_AUTO_RESIDENCE_ID", id);
        });
        return item;
    }

    @Nullable
    public static Item load(ItemsManager parent, YamlConfiguration config, String id) {
        // TODO: 加载物品配置
        ConfigurationSection section;
        String nameFormat = config.getString("res-name.format");
        boolean nameUseAliasIfExists = config.getBoolean("res-name.use-alias-if-exists");

        int sizeX = config.getInt("size.x"),
            sizeY = config.getInt("size.y"),
            sizeZ = config.getInt("size.z");
        if (sizeX <= 0 || sizeZ <= 0 || sizeY < 0) {
            parent.warn("[items/" + id + "] 领地大小不能小于等于0");
            return null;
        }
        List<ICondition> conditionsList = new ArrayList<>();
        List<IAction> conditionsDenyCommands = new ArrayList<>();
        section = config.getConfigurationSection("conditions");
        if (section != null) for (String key : section.getKeys(false)) {
            if (key.equals("deny-commands")) {
                conditionsDenyCommands.addAll(loadActions(section.getStringList(key)));
                continue;
            }
            // TODO: 加载领地创建条件
        }

        String material = config.getString("item.material");
        String display = config.getString("item.display");
        List<String> lore = config.getStringList("item.lore");

        return new Item(id, nameFormat, nameUseAliasIfExists,
                sizeX, sizeY, sizeZ, conditionsList, conditionsDenyCommands,
                material, display, lore);
    }
}
