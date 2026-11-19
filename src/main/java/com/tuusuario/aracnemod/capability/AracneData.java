package com.tuusuario.aracnemod.capability;

import net.minecraft.nbt.CompoundTag;

public class AracneData {
    private int phase = 0; // 0 = base, 1 = escalar, 2-3 = patas en espalda, 4 = transformacion
    private int spiderLegs = 0; // 0 a 4 patas
    private boolean transformed = false;

    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = Math.min(4, Math.max(0, phase));
    }

    public int getSpiderLegs() {
        return spiderLegs;
    }

    public void setSpiderLegs(int spiderLegs) {
        this.spiderLegs = Math.min(4, Math.max(0, spiderLegs));
    }

    public boolean isTransformed() {
        return transformed;
    }

    public void setTransformed(boolean transformed) {
        this.transformed = transformed;
    }

    public void copyFrom(AracneData source) {
        this.phase = source.phase;
        this.spiderLegs = source.spiderLegs;
        this.transformed = source.transformed;
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("aracne_phase", phase);
        nbt.putInt("aracne_legs", spiderLegs);
        nbt.putBoolean("aracne_transformed", transformed);
    }

    public void loadNBTData(CompoundTag nbt) {
        phase = nbt.getInt("aracne_phase");
        spiderLegs = nbt.getInt("aracne_legs");
        transformed = nbt.getBoolean("aracne_transformed");
    }
}
