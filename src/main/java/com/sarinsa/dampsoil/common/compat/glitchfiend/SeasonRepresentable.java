package com.sarinsa.dampsoil.common.compat.glitchfiend;

import javax.annotation.Nullable;

public enum SeasonRepresentable {
    EARLY_SPRING("EARLY_SPRING"),
    MID_SPRING("MID_SPRING"),
    LATE_SPRING("LATE_SPRING"),
    EARLY_SUMMER("EARLY_SUMMER"),
    MID_SUMMER("MID_SUMMER"),
    LATE_SUMMER("LATE_SUMMER"),
    EARLY_AUTUMN("EARLY_AUTUMN"),
    MID_AUTUMN("MID_AUTUMN"),
    LATE_AUTUMN("LATE_AUTUMN"),
    EARLY_WINTER("EARLY_WINTER"),
    MID_WINTER("MID_WINTER"),
    LATE_WINTER("LATE_WINTER");

    SeasonRepresentable(String name) {
        this.name = name;
    }
    private final String name;


    public String getName() {
            return name;
        }

    @Nullable
    public static SeasonRepresentable getFromName(String name) {
        for (SeasonRepresentable representable : values()) {
            if (representable.getName().equals(name))
                return representable;
        }
        return null;
    }
}
