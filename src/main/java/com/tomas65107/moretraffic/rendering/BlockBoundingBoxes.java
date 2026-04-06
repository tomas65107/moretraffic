package com.tomas65107.moretraffic.rendering;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockBoundingBoxes {

    /// POLE
    public static final VoxelShape POLE = Block.box(7, 0, 7, 9, 16, 9);

    /// ADVANCED TRAFFIC LIGHT
    private static final VoxelShape LIGHT_BODY_3 = Block.box(4, 0, 0.5, 12, 16, 6);
    private static final VoxelShape LIGHT_BODY_2 = Block.box(4, 2, 0.5, 12, 16, 6);
    private static final VoxelShape LIGHT_BODY_1 = Block.box(4, 9, 0.5, 12, 16, 6);

    public static final VoxelShape advancedTraffic3Light = Shapes.or(LIGHT_BODY_3, POLE);
    public static final VoxelShape advancedTraffic2Light = Shapes.or(LIGHT_BODY_2, POLE);
    public static final VoxelShape advancedTraffic1Light = Shapes.or(LIGHT_BODY_1, POLE);

    /// blinker
    public static final VoxelShape blinker = Shapes.or(
            Block.box(4, 0, 5, 12, 2, 11),
            Block.box(5, 2, 6, 11, 8, 10)
    );

    /// traffic display
    public static final VoxelShape traffic_display = Shapes.or(
            Block.box(0, 0, 9, 16, 16, 16),
            Block.box(0, 0, 8.75, 16, 16, 9)
    );

    /// truss
    public static final VoxelShape TRUSS_BASE;
    public static final VoxelShape RAIL_LEFT;
    public static final VoxelShape RAIL_RIGHT;

    /// pillar
    public static final VoxelShape TRAFFIC_PILLAR;

    /// led strip
    public static final VoxelShape LEDSTRIP = Shapes.or(
//            Block.box(0, 0, 13, 16, 16, 16)
            Block.box(0, 0, 0,                16, 2.6, 16)
    );

    public static final VoxelShape BALLAST;

    public static final VoxelShape DERAILER;


    static {
        BALLAST = Shapes.or(Block.box(0, 0, 0, 16, 16, 16), Block.box(0, 16, 0, 16, 17, 16));
        TRUSS_BASE = Block.box(7, 0, 0, 16, 8, 16);
        RAIL_LEFT =  Block.box(4, 6, 0, 5, 18, 16);
        RAIL_RIGHT = Block.box(18, 6, 0, 19, 18, 16);

        TRAFFIC_PILLAR = Block.box(5, 0, 5, 11, 16, 11);

        DERAILER = Block.box(2, 2, 0, 14, 14, 12);
    }
}
