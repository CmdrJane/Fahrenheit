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
    private final Map<String, Map<String, float[]>> defaultBlocksData = new HashMap<>();
    IOManager(){
        Map<String, float[]> test1 = new HashMap<>();
        Map<String, float[]> test2 = new HashMap<>();
        test1.put("default", new float[]{2.0F, 2.7f});
        test2.put("lit", new float[]{1.0F, 2.7f});
        this.defaultBlocksData.put("minecraft:lava", test1);
        this.defaultBlocksData.put("minecraft:furnace", test2);
    }
    public void genDefaultCfg() throws IOException {
        String json = new GsonBuilder().setPrettyPrinting().create().toJson(this.defaultBlocksData);
        File file = new File("./config/fahrenheit/blocks-data.json");
        fileWriter(file,json);
    }
    public void readBlocksCfg() {
        Map<String, Map<String, float[]>> cfgData;
        try {
            cfgData = new Gson().fromJson(new FileReader("./config/fahrenheit/blocks-data.json"), new TypeToken<Map<String, Map<String, float[]>>>(){}.getType());
        } catch (Exception e){
            e.printStackTrace();
            cfgData = this.defaultBlocksData;
        }

        for(String s : cfgData.keySet()){
            Fahrenheit.blocks_cfg.put(new Identifier(s), cfgData.get(s));
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
