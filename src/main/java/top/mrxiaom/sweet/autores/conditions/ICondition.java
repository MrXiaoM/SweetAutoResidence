package top.mrxiaom.sweet.autores.conditions;

import org.bukkit.entity.Player;
import top.mrxiaom.pluginbase.api.IAction;

import java.util.List;

public interface ICondition {
    boolean match(Player player);
    List<IAction> getDenyCommands();
}
