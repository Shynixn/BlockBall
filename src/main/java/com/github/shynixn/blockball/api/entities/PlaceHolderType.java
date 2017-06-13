package com.github.shynixn.blockball.api.entities;

/**
 * Created by Shynixn
 */
public enum PlaceHolderType {
    REDSCORE,
    REDAMOUNT,
    REDCOLOR,
    REDNAME,

    BLUESCORE,
    BLUEAMOUNT,
    BLUECOLOR,
    BLUENAME,

    LASTHITPLAYERNAME;

    public static PlaceHolderType getTypeFromName(String name) {
        for (final PlaceHolderType type : PlaceHolderType.values()) {
            if (name.toUpperCase().contains(type.name().toUpperCase()))
                return type;
        }
        return null;
    }
}
