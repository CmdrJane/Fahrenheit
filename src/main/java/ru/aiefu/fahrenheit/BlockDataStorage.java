package ru.aiefu.fahrenheit;

public class BlockDataStorage {
    public float closeRange;
    public float closeRangeTemp;
    public float longRange;
    public float longRangeTemp;

    public BlockDataStorage(float closeRange, float closeRangeTemp, float longRange, float longRangeTemp) {
        this.closeRange = closeRange;
        this.closeRangeTemp = closeRangeTemp;
        this.longRange = longRange;
        this.longRangeTemp = longRangeTemp;
    }
}
