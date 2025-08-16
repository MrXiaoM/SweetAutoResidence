package top.mrxiaom.sweet.autores.func;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.sweet.autores.SweetAutoResidence;
import top.mrxiaom.sweet.autores.func.entry.Dictionary;

import java.util.HashMap;
import java.util.Map;

@AutoRegister
public class DictionaryManager extends AbstractModule {
    private final Map<String, Dictionary> dictionaryMap = new HashMap<>();
    public DictionaryManager(SweetAutoResidence plugin) {
        super(plugin);
    }

    @Override
    public void reloadConfig(MemoryConfiguration config) {
        dictionaryMap.clear();
        ConfigurationSection section = config.getConfigurationSection("random-dictionaries");
        if (section != null) for (String id : section.getKeys(false)) {
            char[] chars = section.getString(id + ".chars", "").toCharArray();
            if (chars.length == 0) {
                continue;
            }
            int lengthMin, lengthMax;
            String lengthStr = section.getString(id + ".length", "");
            if (lengthStr.contains("-")) {
                String[] split = lengthStr.split("-", 2);
                int a = Util.parseInt(split[0]).orElse(0);
                int b = Util.parseInt(split[1]).orElse(0);
                if (a <= 0 || b <= 0) continue;
                lengthMin = Math.min(a, b);
                lengthMax = Math.max(a, b);
            } else {
                int a = Util.parseInt(lengthStr).orElse(0);
                if (a <= 0) continue;
                lengthMin = lengthMax = a;
            }
            dictionaryMap.put(id, new Dictionary(id, chars, lengthMin, lengthMax));
        }
    }

    public Dictionary get(String id) {
        return dictionaryMap.get(id);
    }

    public static DictionaryManager inst() {
        return instanceOf(DictionaryManager.class);
    }
}
