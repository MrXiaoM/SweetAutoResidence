package top.mrxiaom.sweet.autores.func;
        
import top.mrxiaom.sweet.autores.SweetAutoResidence;

@SuppressWarnings({"unused"})
public abstract class AbstractPluginHolder extends top.mrxiaom.pluginbase.func.AbstractPluginHolder<SweetAutoResidence> {
    public AbstractPluginHolder(SweetAutoResidence plugin) {
        super(plugin);
    }

    public AbstractPluginHolder(SweetAutoResidence plugin, boolean register) {
        super(plugin, register);
    }
}
