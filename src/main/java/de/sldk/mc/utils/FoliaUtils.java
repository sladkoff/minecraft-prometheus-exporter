package de.sldk.mc.utils;

public final class FoliaUtils {

    private static final boolean FOLIA;

    static {
        boolean folia = false;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (ClassNotFoundException e) {
            // not Folia
        }
        FOLIA = folia;
    }

    public static boolean isFolia() {
        return FOLIA;
    }

    private FoliaUtils() {
    }
}
