package com.headfishindustries.civilisedideas;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.RegionFileCache;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.WorldEvent;
import org.apache.commons.lang3.time.StopWatch;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Slind on 7/4/2016.
 */

@SuppressWarnings("WeakerAccess")
public class ReplaceHandler {

    // needs to be static as its shared between both event handlers of this class
    private static boolean loaded = false;
    // needs to be static as its shared between both event handlers of this class
    private static Map<Integer, HashSet<Coordinates>> convertedChunks = new HashMap<Integer, HashSet<Coordinates>>();
    private Map<Integer, LinkedList<Coordinates>> toProcess = new HashMap<Integer, LinkedList<Coordinates>>();
    private Map<Block, Block> replaceMapping = new HashMap<Block, Block>();

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (!loaded) {
            replaceMapping.put(Block.getBlockFromName("ci:Bonerock"), Block.getBlockFromName("Quadrum:bones"));
            replaceMapping.put(Block.getBlockFromName("ci:Redrock"), Block.getBlockFromName("ExtrabiomesXL:terrain_blocks1"));
            load();
            loaded = true;
            CivilisedIdeas.logger.info("Finished loading replace mapping and CivilizedIdeasConvertedChunks.json");
            /*for (Object aBlockRegistry : Block.blockRegistry) {
                CivilisedIdeas.logger.info(Block.blockRegistry.getNameForObject(aBlockRegistry));
            }*/
        }
    }

    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save event) {
        if (loaded && event.world.provider.dimensionId == 0) {
            CivilisedIdeas.logger.info("Saving data to be on the safe side.");
            save();
        }
    }

    //get bounding size, only works with cuboid worlds centering around 0 0
    @SubscribeEvent
    public void onWorldInit(TickEvent.WorldTickEvent event) {
        int dimID = event.world.provider.dimensionId;
        if (loaded && !toProcess.containsKey(dimID)) {
            StopWatch sw = new StopWatch();
            sw.start();
            int bx = 0, bz = 0;
            if (!convertedChunks.containsKey(dimID)) {
                convertedChunks.put(dimID, new HashSet<Coordinates>());
            }

            toProcess.put(dimID, new LinkedList<Coordinates>());

            WorldServer world = DimensionManager.getWorld(dimID);
            while (RegionFileCache.createOrLoadRegionFile(world.getChunkSaveLocation(), bx, 0).chunkExists(bx & 0x1F, 0 & 0x1F)) {
                bx++;
            }
            bx--;
            while (RegionFileCache.createOrLoadRegionFile(world.getChunkSaveLocation(), 0, bz).chunkExists(0 & 0x1F, bz & 0x1F)) {
                bz++;
            }
            bz--;
            CivilisedIdeas.logger.info("Bounding box size of world " + dimID + " is " + bx + " " + bz);

            HashSet<Coordinates> covered = convertedChunks.get(dimID);
            LinkedList<Coordinates> toProcessList = new LinkedList<Coordinates>();

            for (int x = bx; x > -bx; x--) {
                for (int z = bz; z > -bz; z--) {
                    Coordinates c = new Coordinates(x,z);
                    if (covered == null || !covered.contains(c)) {
                        toProcessList.add(c);
                    }
                }
            }
            toProcess.put(dimID, toProcessList);
            sw.stop();
            CivilisedIdeas.logger.info("World " + dimID + " has " + toProcessList.size() + " chunks to process. Creating the process queue took " + sw.getTime() + " ms");
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        int dimID = event.world.provider.dimensionId;
        if (loaded && toProcess.containsKey(dimID) && !toProcess.get(dimID).isEmpty()) {
            int count = 0;
            Coordinates toProcess = this.toProcess.get(dimID).getLast();
            Chunk chunk = event.world.getChunkFromChunkCoords(toProcess.getX(), toProcess.getZ());
            int yMax = event.world.getActualHeight();
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < yMax; y++) {
                        Block block = chunk.getBlock(x, y, z);
                        if (replaceMapping.containsKey(block)) {
                            chunk.func_150807_a(x, y, z, replaceMapping.get(block), 0);
                            count++;
                        }
                    }
                }
            }
            convertedChunks.get(dimID).add(toProcess);
            this.toProcess.get(dimID).removeLast();
            CivilisedIdeas.logger.info("Processed chunk " + toProcess.getX() + " " + toProcess.getZ() + " and replaced " + count + " blocks in world " + dimID);
        }
    }


    private void save() {
        Gson gson = new Gson();

        CivilisedIdeas.logger.info("[before save] convertedChunks DIM 0 size: " + (convertedChunks.get(0) != null ? convertedChunks.get(0).size() : "NaN"));

        // convert java object to JSON format,
        // and returned as JSON formatted string
        Type type = new TypeToken<Map<Integer, HashSet<Coordinates>>>(){}.getType();
        String json = gson.toJson(convertedChunks, type);

        try {
            //write converted json data to a file named "file.json"
            FileWriter writer = new FileWriter(getFile());
            writer.write(json);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void load() {
        Gson gson = new Gson();

        try {
            File file = getFile();
            if (file.exists()) {
                BufferedReader br = new BufferedReader(
                        new FileReader(getFile()));

                //convert the json string back to object
                Type type = new TypeToken<Map<Integer, HashSet<Coordinates>>>(){}.getType();
                convertedChunks = gson.fromJson(br, type);
                CivilisedIdeas.logger.info("[after load] convertedChunks DIM 0 size: " + (convertedChunks.get(0) != null ? convertedChunks.get(0).size() : "NaN"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getFile() {
        return new File(DimensionManager.getCurrentSaveRootDirectory() + File.separator + "CivilizedIdeasConvertedChunks.json");
    }


    private class Coordinates {

        private int x;
        private int z;

        Coordinates(int x, int z) {
            this.x = x;
            this.z = z;
        }

        int getX() {
            return x;
        }

        void setX(int x) {
            this.x = x;
        }

        int getZ() {
            return z;
        }

        void setZ(int z) {
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Coordinates that = (Coordinates) o;

            return x == that.x && z == that.z;

        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + z;
            return result;
        }
    }

}
