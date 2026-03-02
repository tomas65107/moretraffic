package com.tomas65107.moretraffic.data;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class SpritesManager {

    public static final ResourceLocation ICON_INFO = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_info.png");
    public static final ResourceLocation ICON_WARNING = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_warning.png");

    public static final ResourceLocation ICON_CLEAR = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_clear.png");
    public static final ResourceLocation ICON_INVERT = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_invert.png");
    public static final ResourceLocation ICON_IMPORT = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_import.png");
    public static final ResourceLocation ICON_EXPORT = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_export.png");
    public static final ResourceLocation ICON_PLUS = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_plus.png");
    public static final ResourceLocation ICON_REPEAT = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_repeat.png");
    public static final ResourceLocation ICON_ONETIME = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_onetime.png");
    public static final ResourceLocation ICON_TRASHCAN = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_trashcan.png");
    public static final ResourceLocation ICON_EMPTY = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_empty.png");
    public static final ResourceLocation ICON_PLAY = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_play.png");
    public static final ResourceLocation ICON_PLAY_ENGAGED = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_play_engaged.png");
    public static final ResourceLocation ICON_GROUP = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_group.png");
    public static final ResourceLocation ICON_ADD_POSITION = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_add_position.png");

    public static void renderSprite(
            GuiGraphics gfx,
            ResourceLocation sprite,
            int x, int y
    ) {
        gfx.blit(
                sprite,
                x, y,
                0, 0,          // u, v
                16, 16, // size on screen
                16, 16  // texture size
        );
    }

}
