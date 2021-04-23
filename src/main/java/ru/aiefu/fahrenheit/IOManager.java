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
    private final HashMap<String, BiomeDataStorage> defaultBiomeCfg = new HashMapOf<>("swamp", new BiomeDataStorage(0.15F,0.5F), "desert", new BiomeDataStorage(0.3F, -0.3F));
    IOManager(){
        Map<String, BlockDataStorage> test1 = new HashMap<>();
        Map<String, BlockDataStorage> test2 = new HashMap<>();
        test1.put("default", new BlockDataStorage(2.1F,2.0F, 3.5F, 1.0F));
        test2.put("lit", new BlockDataStorage(2.5F,1.0F, 3.5F, 0.3F));
        this.defaultBlocksData.put("minecraft:lava", test1);
        this.defaultBlocksData.put("minecraft:furnace", test2);
    }
    public void genDefaultCfg(){
        String json = new GsonBuilder().setPrettyPrinting().create().toJson(new ConfigInstance(10,0, true, true, true, true, false, false, false, false, true));
        File file = new File("./config/fahrenheit/config.json");
        fileWriter(file,json);
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
            biomeData = new Gson().fromJson(new FileReader("./config/fahrenheit/blocks-data.json"), new TypeToken<Map<String, BiomeDataStorage>>(){}.getType());
        } catch (Exception e){
            e.printStackTrace();
            biomeData = this.defaultBiomeCfg;
        }
        for(Map.Entry<String, BiomeDataStorage> e :biomeData.entrySet()){
            EnvironmentManager.biomeDataMap.putIfAbsent(new Identifier(e.getKey()), e.getValue());
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
