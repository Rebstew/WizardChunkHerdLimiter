package net.rebstew.wizardchunkherdlimiter;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class WizardChunkHerdLimitTask extends BukkitRunnable {

    private List<EntityType> limitedTypes = Arrays.asList(EntityType.COW,
            EntityType.MUSHROOM_COW, EntityType.CHICKEN, EntityType.SHEEP, EntityType.PIG,
            EntityType.CAT, EntityType.WOLF, EntityType.HORSE, EntityType.MULE, EntityType.DONKEY, EntityType.BEE,
            EntityType.TURTLE, EntityType.FOX, EntityType.LLAMA, EntityType.RABBIT
    );

    private WizardChunkHerdLimiter plugin;

    WizardChunkHerdLimitTask(WizardChunkHerdLimiter plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {

        double startClock = System.currentTimeMillis();
        List<World> worlds = Bukkit.getWorlds();
        Map<String, Integer> worldsConfig = plugin.getWorldConfigsMap();
        Map<EntityType, Map<Chunk, List<LivingEntity>>> limitMap = new HashMap<>();
        List<LivingEntity> entitiesToDelete = new ArrayList<>();

        for(World world : worlds){
            if(!worldsConfig.containsKey(world.getName())){
                continue;
            }

            Integer worldLimit = worldsConfig.get(world.getName());
            limitMap.clear();

            List<LivingEntity> worldEntities = world.getLivingEntities();

            // add all loaded living entities in the limitMap
            for(LivingEntity entity : worldEntities){
                EntityType currentType = entity.getType();
                if(!limitedTypes.contains(currentType)){
                    continue;
                }

                Map<Chunk, List<LivingEntity>> currentEntityMap = limitMap.get(currentType);
                if(currentEntityMap == null){
                    currentEntityMap = new HashMap<>();
                }

                Chunk currentChunk = entity.getLocation().getChunk();
                List<LivingEntity> currentEntitiesList = currentEntityMap.get(currentChunk);
                if(currentEntitiesList == null){
                    currentEntitiesList = new ArrayList<>();
                }

                currentEntitiesList.add(entity);
                currentEntityMap.put(currentChunk, currentEntitiesList);
                limitMap.put(currentType, currentEntityMap);
            }

            for(EntityType type : limitMap.keySet()){

                Map<Chunk, List<LivingEntity>> currentLimitMap = limitMap.get(type);
                Collection<List<LivingEntity>> lists = currentLimitMap.values();

                for(List<LivingEntity> list : lists){
                    if(list.size() > worldLimit){

                        List<LivingEntity> extraEntitiesList = list.subList(worldLimit, list.size());
                        plugin.getLogger().info(type + " animals at chunk around "
                                + list.get(0).getLocation() + " are too many! ("
                                + list.size() + " instead of " + worldLimit + "), removing "
                                + extraEntitiesList.size() + " of them");
                        entitiesToDelete.addAll(extraEntitiesList);
                    }
                }
            }
        }

        for(LivingEntity entityToDelete : entitiesToDelete){
//                plugin.getLogger().info("Will remove entity " + entityToDelete.getType()
//                        + " at "
//                        + entityToDelete.getLocation());
            entityToDelete.remove();
        }

        double endClock = System.currentTimeMillis();
        plugin.getLogger().info("Removal of " + entitiesToDelete.size() + " extra animals took " + (endClock-startClock) + " ms");
    }
}
