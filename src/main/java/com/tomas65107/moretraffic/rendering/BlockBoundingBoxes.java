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


    static {
        TRUSS_BASE = Block.box(0, 0, 0, 16, 10, 16);
        RAIL_LEFT = Block.box(0.5, -0.75, -0.5, 1.5, 19.75, 16.5);
        RAIL_RIGHT = Block.box(14.5, -0.75, -0.5, 15.5, 19.75, 16.5);

        TRAFFIC_PILLAR = Block.box(5, 0, 5, 11, 16, 11);
    }
}
