package ru.aiefu.fahrenheit;

import java.util.HashMap;


public class HashMapOf<K, V> extends HashMap<K, V>{
    public HashMapOf(K k1, V v1, K k2, V v2){
        putIfAbsent(k1, v1);
        putIfAbsent(k2, v2);
    }
}
