package top.mrxiaom.sweet.autores.func;

import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.sweet.autores.SweetAutoResidence;
import top.mrxiaom.sweet.autores.func.entry.Item;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@AutoRegister
public class ItemsManager extends AbstractModule {
    private final Map<String, Item> items = new HashMap<>();
    public ItemsManager(SweetAutoResidence plugin) {
        super(plugin);
    }

    @Override
    public void reloadConfig(MemoryConfiguration config) {
        items.clear();
        for (String path : config.getStringList("items-folder")) {
            File folder = plugin.resolve(path);
            if (!folder.exists()) {
                Util.mkdirs(folder);
                if (path.equals("./items")) {
                    plugin.saveResource("items/example.yml", new File(folder, "example.yml"));
                }
            }
            Util.reloadFolder(folder, false, (id, file) -> {
                YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
                Item loaded = Item.load(this, cfg, id);
                if (loaded != null) {
                    items.put(id, loaded);
                }
            });
        }
        info("已加载 " + items.size() + " 个圈地道具配置");
    }

    public Set<String> keys() {
        return items.keySet();
    }

    @Nullable
    public Item get(String id) {
        return items.get(id);
    }

    public static ItemsManager inst() {
        return instanceOf(ItemsManager.class);
    }
}
