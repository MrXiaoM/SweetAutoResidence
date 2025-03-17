package top.mrxiaom.sweet.autores.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IResidenceAdapter {
    /**
     * 领地适配器名称，用于后台显示
     */
    @NotNull
    String getName();

    /**
     * 自动圈地选区
     * @param player 玩家
     * @param xSize x方向最大大小
     * @param ySize y方向最大大小
     * @param zSize z方向最大大小
     * @return 圈地选区，返回 null 则为选区失败
     */
    @Nullable
    Selection genAutoSelection(Player player, int xSize, int ySize, int zSize);

    /**
     * 是否存在该名称的领地
     */
    boolean isResidenceExists(String resName);

    /**
     * 创建领地，忽略数量限制，忽略金币消耗等等。并且应该在创建领地后向玩家发送提示
     * @param player 玩家
     * @param resName 领地名
     * @param area 圈地选区
     */
    void createResidence(Player player, String resName, Selection area);

    /**
     * 获取玩家的领地数量
     */
    int getResidenceCount(Player player);

    /**
     * 获取玩家的领地数量限制，-1 则无限制
     */
    int getResidenceMaxCount(Player player);

    /**
     * 玩家的领地数量是否已越过限制
     */
    default boolean hasReachResCountLimit(Player player) {
        int max = getResidenceMaxCount(player);
        if (max < 0) return false;
        if (max == 0) return true;
        int count = getResidenceCount(player);
        return count >= max;
    }
}
