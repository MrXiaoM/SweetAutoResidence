package top.mrxiaom.sweet.autores;
        
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.pluginbase.BukkitPlugin;
import top.mrxiaom.pluginbase.EconomyHolder;

public class SweetAutoResidence extends BukkitPlugin {
    public static SweetAutoResidence getInstance() {
        return (SweetAutoResidence) BukkitPlugin.getInstance();
    }

    public SweetAutoResidence() {
        super(options()
                .bungee(false)
                .adventure(true)
                .database(false)
                .reconnectDatabaseWhenReloadConfig(false)
                .vaultEconomy(true)
                .scanIgnore("top.mrxiaom.sweet.autores.libs")
        );
    }
    @NotNull
    public EconomyHolder getEconomy() {
        return options.economy();
    }


    @Override
    protected void afterEnable() {
        getLogger().info("SweetAutoResidence 加载完毕");
    }
}
