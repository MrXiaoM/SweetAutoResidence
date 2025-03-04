package top.mrxiaom.sweet.autores.conditions;

import top.mrxiaom.pluginbase.func.gui.actions.IAction;

import java.util.List;

public interface ICondition {
    List<IAction> getDenyCommands();
}
