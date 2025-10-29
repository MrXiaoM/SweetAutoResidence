package top.mrxiaom.sweet.autores;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.utils.depend.PlaceholdersExpansion;
import top.mrxiaom.sweet.autores.func.DictionaryManager;
import top.mrxiaom.sweet.autores.func.entry.Dictionary;

public class Placeholders extends PlaceholdersExpansion<SweetAutoResidence> {
    public Placeholders(SweetAutoResidence plugin) {
        super(plugin);
    }

    @Override
    public @NotNull String getIdentifier() {
        return "sares";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.equalsIgnoreCase("residence_count")) {
            return String.valueOf(plugin.getAdapter().getResidenceCount(player));
        }
        if (params.equalsIgnoreCase("residence_max_count")) {
            return String.valueOf(plugin.getAdapter().getResidenceMaxCount(player));
        }
        if (params.startsWith("random_")) {
            String id = params.substring(7);
            Dictionary dictionary = DictionaryManager.inst().get(id);
            return dictionary == null ? "" : dictionary.generate();
        }
        return super.onPlaceholderRequest(player, params);
    }
}
