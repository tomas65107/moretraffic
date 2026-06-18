package com.tomas65107.moretraffic.data;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class SpritesManager {

    public static final ResourceLocation INFO = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_info.png");
    public static final ResourceLocation WARNING = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_warning.png");
    public static final ResourceLocation ERROR = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_error.png");

    public static final ResourceLocation CLEAR = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_clear.png");
    public static final ResourceLocation INVERT = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_invert.png");
    public static final ResourceLocation IMPORT = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_import.png");
    public static final ResourceLocation EXPORT = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_export.png");
    public static final ResourceLocation PLUS = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_plus.png");
    public static final ResourceLocation REPEAT = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_repeat.png");
    public static final ResourceLocation ONETIME = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_onetime.png");
    public static final ResourceLocation ICON_TRASHCAN = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_trashcan.png");
    public static final ResourceLocation ICON_EMPTY = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_empty.png");
    public static final ResourceLocation PLAY = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_play.png");
    public static final ResourceLocation PLAY_ENGAGED = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_play_engaged.png");
    public static final ResourceLocation GROUP = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_group.png");
    public static final ResourceLocation ADD_POSITION = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_add_position.png");
    public static final ResourceLocation EXPORT_FROM_IMAGE = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_import_img.png");
    public static final ResourceLocation EDIT_DISPLAY = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_edit_display.png");
    public static final ResourceLocation CENTER = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_center.png");
    public static final ResourceLocation PALETTE = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_palette.png");
    public static final ResourceLocation EMITS_LIGHT_ON = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_emits_light_on.png");
    public static final ResourceLocation EMITS_LIGHT_OFF = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_emits_light_off.png");
    public static final ResourceLocation MOVE = ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/icon_move.png");

    public static void renderSprite(
            GuiGraphics gfx,
            ResourceLocation sprite,
            int x, int y
    ) {
        renderSprite(gfx, sprite, x, y, 16);
    }

    public static void renderSprite(
            GuiGraphics gfx,
            ResourceLocation sprite,
            int x, int y, int size
    ) {
        gfx.blit(
                sprite,
                x, y,
                0, 0,          // u, v
                size, size, // size on screen
                size, size  // texture size
        );
    }

}
