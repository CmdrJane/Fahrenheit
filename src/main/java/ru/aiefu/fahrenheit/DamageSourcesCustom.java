package ru.aiefu.fahrenheit;

import net.minecraft.entity.damage.DamageSource;

public class DamageSourcesCustom extends DamageSource {
    public DamageSourcesCustom(String name) {
        super(name);
    }

    @Override
    protected DamageSource setBypassesArmor() {
        return super.setBypassesArmor();
    }
}
