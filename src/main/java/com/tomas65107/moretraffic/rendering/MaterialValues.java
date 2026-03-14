package com.tomas65107.moretraffic.rendering;

import net.minecraft.client.renderer.LightTexture;

public class MaterialValues {

    /// used when material is never giving out any light
    public static final int NOT_EMISSIVE = LightTexture.pack(5, 8);

    /// used when the material is masking something tagged as EMISSIVE
    public static final int MASKED_EMISSIVE = LightTexture.pack(6, 10);

    /// used when the material is giving out light (even if turned off)
    public static final int EMISSIVE = LightTexture.FULL_BRIGHT;

}
