package ru.aiefu.fahrenheit;

public class DefaultDataStorage {

    Storage VERY_COLD_BIOMES = new Storage(-0.02F, -0.02F);
    Storage COLD_BIOMES = new Storage(-0.01F, -0.01F);
    Storage MEDIUM_BIOMES = new Storage(0.005F, -0.07F);
    Storage HOT_BIOMES = new Storage(0.01F, 0.01F);
    Storage VERY_HOT_BIOMES = new Storage(0.02F, 0.02F);

    public class Storage{
        public float dayTemp;
        public float nightTemp;

        public Storage(float dayTemp, float nightTemp) {
            this.dayTemp = dayTemp;
            this.nightTemp = nightTemp;
        }
    }
}
