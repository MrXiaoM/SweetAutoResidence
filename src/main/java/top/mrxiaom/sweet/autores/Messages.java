package top.mrxiaom.sweet.autores;

import top.mrxiaom.pluginbase.func.language.IHolderAccessor;
import top.mrxiaom.pluginbase.func.language.Language;
import top.mrxiaom.pluginbase.func.language.LanguageEnumAutoHolder;

import java.util.List;

import static top.mrxiaom.pluginbase.func.language.LanguageEnumAutoHolder.wrap;

@Language(prefix = "messages.")
public enum Messages implements IHolderAccessor {
    not_integer("&e请输入一个整数"),
    player_not_online("&e玩家不在线"),
    player_only("&e该命令只能由玩家执行"),
    command__give_item_not_found("&e找不到物品 %item%"),
    command__give_success("&a已给予玩家&e %player% &a物品 %item%&r &a共 &e%count% &a个"),
    command__reload("&a配置文件已重载"),
    create__no_selection("&e请先手持道具，右键点击查看圈地范围"),
    create__no_available("&e你的领地数量已到达上限"),
    create__already_exists("&e同名的领地已存在，无法创建"),
    create__empty_name("&e生成的领地名为空，无法创建，请联系服务器管理员"),
    create__failed("&e创建领地出现错误，已输出到后台日志，请联系服务器管理员"),
    selection__success("&a已选中区域，Shift+右键确认圈地"),

    ;

    Messages(String defaultValue) {
        holder = wrap(this, defaultValue);
    }
    Messages(String... defaultValue) {
        holder = wrap(this, defaultValue);
    }
    Messages(List<String> defaultValue) {
        holder = wrap(this, defaultValue);
    }
    // 4. 添加字段 holder 以及它的 getter
    private final LanguageEnumAutoHolder<Messages> holder;
    public LanguageEnumAutoHolder<Messages> holder() {
        return holder;
    }
}
