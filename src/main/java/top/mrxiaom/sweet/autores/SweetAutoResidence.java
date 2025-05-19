package top.mrxiaom.sweet.autores;
        
import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.pluginbase.BukkitPlugin;
import top.mrxiaom.pluginbase.economy.EnumEconomy;
import top.mrxiaom.pluginbase.func.LanguageManager;
import top.mrxiaom.pluginbase.utils.PAPI;
import top.mrxiaom.pluginbase.utils.scheduler.FoliaLibScheduler;
import top.mrxiaom.sweet.autores.api.IResidenceAdapter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;

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
                .economy(EnumEconomy.NONE)
                .scanIgnore("top.mrxiaom.sweet.autores.libs")
                .libraries(true)
        );
        scheduler = new FoliaLibScheduler(this);
    }
    private IResidenceAdapter adapter;

    @NotNull
    public IResidenceAdapter getAdapter() {
        return adapter;
    }

    @Override
    protected void beforeLoad() {
        MinecraftVersion.replaceLogger(getLogger());
        MinecraftVersion.disableUpdateCheck();
        MinecraftVersion.disableBStats();
        MinecraftVersion.getVersion();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onEnable() {
        if (checkAdapter()) {
            info("使用领地插件适配器 " + adapter.getName());
            super.onEnable();
        } else {
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    protected boolean checkAdapter() {
        String adapterClass = null;
        InputStream configIn = getResource("residence-adapter.yml");
        if (configIn != null) { // 外置的 领地插件适配器
            YamlConfiguration config = new YamlConfiguration();
            try (InputStream in = configIn;
                 InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                config.load(reader);
            } catch (Exception e) {
                warn("加载扩展的 residence-adapter.yml 时发时异常", e);
            }
            adapterClass = config.getString("class", null);
            getLogger().info("外置的适配器: " + adapterClass);
        }
        if (adapterClass == null) {
            getLogger().info("未找到外置适配器，正在搜索使用内置适配器");
            PluginManager pm = Bukkit.getPluginManager();
            if (pm.isPluginEnabled("Residence")) {
                adapterClass = "top.mrxiaom.sweet.autores.impl.residence.AdapterResidence";
            }
            // TODO: 在这里添加更多领地插件支持
            getLogger().info("内置的适配器: " + adapterClass);
        }
        if (adapterClass == null) {
            warn("未找到可用的领地插件适配器，卸载插件");
            return false;
        }
        try {
            Class<?> type = Class.forName(adapterClass);
            Constructor<?> constructor = type.getConstructor(SweetAutoResidence.class);
            adapter = (IResidenceAdapter) constructor.newInstance(this);
            return true;
        } catch (Exception e) {
            warn("无法加载领地插件适配器 " + adapterClass + "，卸载插件", e);
            return false;
        }
    }

    @Override
    protected void beforeEnable() {
        LanguageManager.inst()
                .setLangFile("messages.yml")
                .register(Messages.class, Messages::holder);
    }

    @Override
    protected void afterEnable() {
        if (PAPI.isEnabled()) {
            new Placeholders(this).register();
        }
        getLogger().info("SweetAutoResidence 加载完毕");
    }
}
