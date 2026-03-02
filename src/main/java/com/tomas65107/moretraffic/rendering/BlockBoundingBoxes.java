package com.tomas65107.moretraffic.rendering;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockBoundingBoxes {

    //POLE
    public final VoxelShape POLE = Block.box(7, 0, 7, 9, 16, 9);

    //ADVANCED TRAFFIC LIGHT
    private final VoxelShape LIGHT_BODY_3 = Block.box(4, 0, 0.5, 12, 16, 6);
    private final VoxelShape LIGHT_BODY_2 = Block.box(4, 2, 0.5, 12, 16, 6);
    private final VoxelShape LIGHT_BODY_1 = Block.box(4, 9, 0.5, 12, 16, 6);

    public final VoxelShape advancedTraffic3Light = net.minecraft.world.phys.shapes.Shapes.or(LIGHT_BODY_3, POLE);
    public final VoxelShape advancedTraffic2Light = net.minecraft.world.phys.shapes.Shapes.or(LIGHT_BODY_2, POLE);
    public final VoxelShape advancedTraffic1Light = net.minecraft.world.phys.shapes.Shapes.or(LIGHT_BODY_1, POLE);

}
