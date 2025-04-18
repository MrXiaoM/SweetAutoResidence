package top.mrxiaom.sweet.autores;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Placeholders extends PlaceholderExpansion {
    SweetAutoResidence plugin;
    public Placeholders(SweetAutoResidence plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean register() {
        try {
            super.unregister();
        } catch (Throwable ignored) {
        }
        return super.register();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "sares";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.equalsIgnoreCase("residence_count")) {
            return String.valueOf(plugin.getAdapter().getResidenceCount(player));
        }
        if (params.equalsIgnoreCase("residence_max_count")) {
            return String.valueOf(plugin.getAdapter().getResidenceMaxCount(player));
        }
        return super.onPlaceholderRequest(player, params);
    }
}
