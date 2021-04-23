package ru.aiefu.fahrenheit;

public class ConfigInstance {
    public float precision;
    public int operationMode;
    public boolean enableTemperature;
    public boolean enableTemperatureHud;
    public boolean enableThirst;
    public boolean enableThirstHud;
    public boolean disableDeadlyCold;
    public boolean disableDeadlyHeat;
    public boolean disableThinAir;
    public boolean waterFlatChill;
    public boolean cauldronInfiniteSource;

    public ConfigInstance(float precision, int operationMode, boolean enableTemperature, boolean enableTemperatureHud,
                          boolean enableThirst, boolean enableThirstHud, boolean disableDeadlyCold, boolean disableDeadlyHeat,
                          boolean disableThinAir, boolean waterFlatChill, boolean cauldronInfiniteSource) {
        this.precision = precision;
        this.operationMode = operationMode;
        this.enableTemperature = enableTemperature;
        this.enableTemperatureHud = enableTemperatureHud;
        this.enableThirst = enableThirst;
        this.enableThirstHud = enableThirstHud;
        this.disableDeadlyCold = disableDeadlyCold;
        this.disableDeadlyHeat = disableDeadlyHeat;
        this.disableThinAir = disableThinAir;
        this.waterFlatChill = waterFlatChill;
        this.cauldronInfiniteSource = cauldronInfiniteSource;
    }
}
