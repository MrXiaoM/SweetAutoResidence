package top.mrxiaom.sweet.autores.conditions;

import org.bukkit.entity.Player;
import top.mrxiaom.pluginbase.func.gui.actions.IAction;

import java.util.List;

public interface ICondition {
    boolean match(Player player);
    List<IAction> getDenyCommands();
}
