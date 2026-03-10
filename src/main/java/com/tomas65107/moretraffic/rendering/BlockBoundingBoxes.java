package com.tomas65107.moretraffic.rendering;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockBoundingBoxes {

    //POLE
    public static final VoxelShape POLE = Block.box(7, 0, 7, 9, 16, 9);

    //ADVANCED TRAFFIC LIGHT
    private static final VoxelShape LIGHT_BODY_3 = Block.box(4, 0, 0.5, 12, 16, 6);
    private static final VoxelShape LIGHT_BODY_2 = Block.box(4, 2, 0.5, 12, 16, 6);
    private static final VoxelShape LIGHT_BODY_1 = Block.box(4, 9, 0.5, 12, 16, 6);

    public static final VoxelShape advancedTraffic3Light = net.minecraft.world.phys.shapes.Shapes.or(LIGHT_BODY_3, POLE);
    public static final VoxelShape advancedTraffic2Light = net.minecraft.world.phys.shapes.Shapes.or(LIGHT_BODY_2, POLE);
    public static final VoxelShape advancedTraffic1Light = net.minecraft.world.phys.shapes.Shapes.or(LIGHT_BODY_1, POLE);

    //BLINKER
    public static final VoxelShape blinker = Shapes.or(
            Block.box(4, 0, 5, 12, 2, 11),
            Block.box(5, 2, 6, 11, 8, 10)
    );

}
