package ru.aiefu.fahrenheit;

import com.google.common.collect.Maps;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import java.util.HashMap;


public class HashMapBuilder{
    HashMap<Identifier, Recipe<?>> hashMap;

    public HashMapBuilder(){
        hashMap = Maps.newHashMap();
    }

    public HashMapBuilder putIfAbsent(Identifier id, Recipe<?> recipe){
        hashMap.putIfAbsent(id, recipe);
        return this;
    }
    public HashMap<Identifier, Recipe<?>> build(){
        return hashMap;
    }
}
