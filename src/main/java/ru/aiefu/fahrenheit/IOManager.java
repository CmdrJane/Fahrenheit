package ru.aiefu.fahrenheit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IOManager {
    private final HashMap<String, Map<String, BlockDataStorage>> defaultBlocksData = new HashMap<>();
    private final HashMap<String, BiomeDataStorage> defaultBiomeCfg = new HashMapOf<>("swamp", new BiomeDataStorage(0.015F,0.05F), "desert", new BiomeDataStorage(0.03F, -0.03F));
    public IOManager(){
        Map<String, BlockDataStorage> test1 = new HashMap<>();
        Map<String, BlockDataStorage> test2 = new HashMap<>();
        test1.put("default", new BlockDataStorage(2.1F,0.2F, 3.5F, 0.1F));
        test2.put("lit", new BlockDataStorage(2.5F,0.1F, 3.5F, 0.03F));
        this.defaultBlocksData.put("minecraft:lava", test1);
        this.defaultBlocksData.put("minecraft:furnace", test2);
    }
    public void reload(){
        readConfig();
        readDefaultDataStorage();
        readBlocksCfg();
        readBiomeCfg();
    }
    public void genDefaultCfg(){
        String json = new GsonBuilder().setPrettyPrinting().create().toJson(new ConfigInstance());
        File file = new File("./config/fahrenheit/config.json");
        fileWriter(file,json);
    }
    public void readConfig(){
        ConfigInstance cfg;
        try {
            cfg = new Gson().fromJson(new FileReader("./config/fahrenheit/config.json"), ConfigInstance.class);
        }
        catch (IOException e){
            e.printStackTrace();
            cfg = new ConfigInstance();
        }
        Fahrenheit.config_instance = cfg;
    }
    public void genDefaultDataStorage(){
        String json = new GsonBuilder().setPrettyPrinting().create().toJson(new DefaultDataStorage());
        File file = new File("./config/fahrenheit/biomes-by-temp-category-config.json");
        fileWriter(file,json);
    }
    public void readDefaultDataStorage(){
        DefaultDataStorage cfgData;
        try {
            cfgData = new Gson().fromJson(new FileReader("./config/fahrenheit/biomes-by-temp-category-config.json"), DefaultDataStorage.class);
        } catch (Exception e){
            e.printStackTrace();
            cfgData = new DefaultDataStorage();
        }
        Fahrenheit.defaultDataStorage = cfgData;
    }
    public void genBlocksCfg(){
        String json = new GsonBuilder().setPrettyPrinting().create().toJson(this.defaultBlocksData);
        File file = new File("./config/fahrenheit/blocks-data.json");
        fileWriter(file,json);
    }
    public void readBlocksCfg() {
        Map<String, Map<String, BlockDataStorage>> cfgData;
        try {
            cfgData = new Gson().fromJson(new FileReader("./config/fahrenheit/blocks-data.json"), new TypeToken<Map<String, Map<String, BlockDataStorage>>>(){}.getType());
        } catch (Exception e){
            e.printStackTrace();
            cfgData = this.defaultBlocksData;
        }

        for(String s : cfgData.keySet()){
            Fahrenheit.blocks_cfg.put(new Identifier(s), cfgData.get(s));
        }

    }
    public void genBiomeCfg(){
        String json = new GsonBuilder().setPrettyPrinting().create().toJson(defaultBiomeCfg);
        File file = new File("./config/fahrenheit/biome-config.json");
        fileWriter(file,json);
    }
    public void readBiomeCfg(){
        Map<String, BiomeDataStorage> biomeData;
        try {
            biomeData = new Gson().fromJson(new FileReader("./config/fahrenheit/biome-config.json"), new TypeToken<Map<String, BiomeDataStorage>>(){}.getType());
        } catch (Exception e){
            e.printStackTrace();
            biomeData = this.defaultBiomeCfg;
        }
        for(Map.Entry<String, BiomeDataStorage> e :biomeData.entrySet()){
            Fahrenheit.biomeDataMap.putIfAbsent(new Identifier(e.getKey()), e.getValue());
        }
    }
    public void fileWriter(File file, String gson){
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try(FileWriter writer = new FileWriter(file)) {
            writer.write(gson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
