package top.mrxiaom.sweet.autores.api;

import org.bukkit.entity.Player;

public interface IResidenceAdapter {
    String getName();
    Selection genAutoSelection(Player player, int xSize, int ySize, int zSize);
    boolean isResidenceExists(String resName);
    void createResidence(Player player, String resName, Selection area);
    int getResidenceCount(Player player);
}
