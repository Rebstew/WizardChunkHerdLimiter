package net.rebstew.wizardchunkherdlimiter;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WizardChunkHerdLimiter extends JavaPlugin {

    private Map<String, Integer> worldConfigsMap;

    @Override
    public void onEnable() {
        loadWorldConfigMap();
        this.getCommand("wchunkherdlimiter").setExecutor(new WizardChunkHerdLimiterCommandExecutor(this));

        Integer taskDelay = (getConfig().getInt("task-frequency") != 0 ?
            getConfig().getInt("task-frequency") : 18000);

        new WizardChunkHerdLimitTask(this).runTaskTimerAsynchronously(this,
                taskDelay,
                taskDelay);
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }

    void limitAnimalsInWorld(String worldName, Integer limit){
        worldConfigsMap.put(worldName, limit);

        writeWorldsMap();
    }

    private void loadWorldConfigMap(){
        worldConfigsMap = new HashMap<>();

        ConfigurationSection worldSection = getConfig().getConfigurationSection("worlds");
        if(worldSection != null) {
            Set<String> keys = worldSection.getKeys(false);

            for (String key : keys) {
                Integer worldLimit = worldSection.getInt(key);
                worldConfigsMap.put(key, worldLimit);
            }
        }
    }

    private void writeWorldsMap(){
        for(Map.Entry<String, Integer> entry : worldConfigsMap.entrySet()){
            String worldName = entry.getKey();
            Integer limit = entry.getValue();

            getConfig().set("worlds." + worldName, limit);
        }

        saveConfig();
    }

    public Map<String, Integer> getWorldConfigsMap() {
        return worldConfigsMap;
    }
}
