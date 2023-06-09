package com.github.shynixn.blockball.api.business.enumeration

/**
 * Supported versions.
 */
enum class Version(
    /**
     * Id of the bukkit versions.
     */
    val bukkitId: String,
    /**
     * General id.
     */
    val id: String,
    /**
     * Numeric Id for calculations.
     */
    val numericId: Double
) {
    /**
     * Unknown version.
     */
    VERSION_UNKNOWN("", "", 0.0),

    /**
     * Version 1.8.0 - 1.8.2.
     */
    VERSION_1_8_R1("v1_8_R1", "1.8.2", 1.081),

    /**
     * Version 1.8.3 - 1.8.4.
     */
    VERSION_1_8_R2("v1_8_R2", "1.8.3", 1.082),

    /**
     * Version 1.8.5 - 1.8.9.
     */
    VERSION_1_8_R3("v1_8_R3", "1.8.9", 1.083),

    /**
     * Version 1.9.0 - 1.9.1.
     */
    VERSION_1_9_R1("v1_9_R1", "1.9.1", 1.091),

    /**
     * Version 1.9.2 - 1.9.4
     */
    VERSION_1_9_R2("v1_9_R2", "1.9.4", 1.092),

    /**
     * Version 1.10.0 - 1.10.2.
     */
    VERSION_1_10_R1("v1_10_R1", "1.10.2", 1.10),

    /**
     * Version 1.11.0 - 1.11.2.
     */
    VERSION_1_11_R1("v1_11_R1", "1.11.2", 1.11),

    /**
     * Version 1.12.0 - 1.12.2.
     */
    VERSION_1_12_R1("v1_12_R1", "1.12.2", 1.12),

    /**
     * Version 1.13.0 - 1.13.0.
     */
    VERSION_1_13_R1("v1_13_R1", "1.13.0", 1.13),

    /**
     * Version 1.13.1 - 1.13.2.
     */
    VERSION_1_13_R2("v1_13_R2", "1.13.2", 1.131),

    /**
     * Version 1.14.0 - 1.14.4.
     */
    VERSION_1_14_R1("v1_14_R1", "1.14.4", 1.144),

    /**
     * Version 1.15.0 - 1.15.2.
     */
    VERSION_1_15_R1("v1_15_R1", "1.15.2", 1.150),

    /**
     * Version 1.16.0 - 1.16.1.
     */
    VERSION_1_16_R1("v1_16_R1", "1.16.1", 1.160),

    /**
     * Version 1.16.2 - 1.16.3.
     */
    VERSION_1_16_R2("v1_16_R2", "1.16.2", 1.161),

    /**
     * Version 1.16.4 - 1.16.5.
     */
    VERSION_1_16_R3("v1_16_R3", "1.16.5", 1.162),

    /**
     * Version 1.17.0 - 1.17.1.
     */
    VERSION_1_17_R1("v1_17_R1", "1.17.0", 1.170),

    /**
     * Version 1.18.0 - 1.18.1.
     */
    VERSION_1_18_R1("v1_18_R1", "1.18.0", 1.180),

    /**
     * Version 1.18.2 - 1.18.2.
     */
    VERSION_1_18_R2("v1_18_R2", "1.18.2", 1.182),

    /**
     * Version 1.19.0 - 1.19.2.
     */
    VERSION_1_19_R1("v1_19_R1", "1.19.0", 1.190),

    /**
     * Version 1.19.3 - 1.19.3.
     */
    VERSION_1_19_R2("v1_19_R2", "1.19.3", 1.193),

    /**
     * Version 1.19.4 - 1.19.4.
     */
    VERSION_1_19_R3("v1_19_R3", "1.19.4", 1.194),

    /**
     * Version 1.20 - 1.20.
     */
    VERSION_1_20_R1("v1_20_R1", "1.20.0", 1.20);

    /**
     * Gets if this version is same or greater than the given version by parameter.
     */
    fun isVersionSameOrGreaterThan(version: Version): Boolean {
        val result = this.numericId.compareTo(version.numericId)
        return result == 0 || result == 1
    }

    /**
     * Gets if the version is the same or lower than the given version by parameter.
     */
    fun isVersionSameOrLowerThan(version: Version): Boolean {
        val result = this.numericId.compareTo(version.numericId)
        return result == 0 || result == -1
    }

    /**
     * Gets if this version is compatible to the versions given as parameter.
     */
    fun isCompatible(vararg versions: Version): Boolean {
        for (version in versions) {
            if (this.bukkitId == version.bukkitId) {
                return true
            }
        }

        return false
    }
}
