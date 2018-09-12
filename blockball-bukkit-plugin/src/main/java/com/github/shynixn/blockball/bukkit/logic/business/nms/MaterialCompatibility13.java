package com.github.shynixn.blockball.bukkit.logic.business.nms;

import org.bukkit.Material;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Shynixn 2017.
 * <p>
 * Version 1.1
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public final class MaterialCompatibility13 {
    private static Method getMaterialFromIdMethod;
    private static Method getIdFromMaterialMethod;

    static {
        try {
            if (VersionSupport.getServerVersion().isVersionLowerThan(VersionSupport.VERSION_1_13_R1)) {
                getMaterialFromIdMethod = Material.class.getDeclaredMethod("getMaterial", int.class);
            }

            getIdFromMaterialMethod = Material.class.getDeclaredMethod("getId");
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static Material getBukkitMaterial(com.github.shynixn.blockball.api.business.enumeration.MaterialType material) {
        return getMaterialFromId(material.getMinecraftNumericId());
    }

    private MaterialCompatibility13() {
        super();
    }

    /**
     * Handles changes for minecraft 1.13.
     * Returns the material from the given id.
     *
     * @param id id
     * @return material
     */
    public static Material getMaterialFromId(int id) {
        try {
            if (getMaterialFromIdMethod != null) {
                return (Material) getMaterialFromIdMethod.invoke(null, id);
            } else {
                for (final Material material : Material.values()) {
                    if (getIdFromMaterial(material) == id) {
                        return material;
                    }
                }

                throw new RuntimeException("Material not found!");
            }
        } catch (final IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handle changes for minecraft 1.13.
     * Returns the id of the given material
     *
     * @param material material
     * @return id
     */
    public static int getIdFromMaterial(Material material) {
        try {
            return (int) getIdFromMaterialMethod.invoke(material);
        } catch (final IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
