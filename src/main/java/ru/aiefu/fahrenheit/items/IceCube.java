package ru.aiefu.fahrenheit.items;

import dev.emi.trinkets.api.TrinketItem;

public class IceCube extends TrinketItem {
    public IceCube(Settings settings) {
        super(settings);
    }

    /**
     * @param group
     * @param slot
     * @return Whether the provided slot is valid for this item
     */
    @Override
    public boolean canWearInSlot(String group, String slot) {
        return false;
    }
}
