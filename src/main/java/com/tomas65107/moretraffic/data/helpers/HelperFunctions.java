package com.tomas65107.moretraffic.data.helpers;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.concurrent.atomic.AtomicReference;

public class HelperFunctions {
    public static VoxelShape rotateShape(Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};

        int times = (to.get2DDataValue() - Direction.NORTH.get2DDataValue() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
                buffer[1] = Shapes.or(buffer[1],
                        Shapes.box(
                                1 - maxZ, minY, minX,
                                1 - minZ, maxY, maxX
                        ));
            });
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }

    public static VoxelShape rotateShapeSpecial(Direction to, VoxelShape shape) {
        if (to == Direction.UP || to == Direction.DOWN) return shape; // vertical stays vertical

        AtomicReference<VoxelShape> result = new AtomicReference<>(Shapes.empty());

        // iterate all boxes in the shape
        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            double width = maxX - minX;
            double height = maxY - minY;
            double depth = maxZ - minZ;

            double newMinX = 0, newMinY = 0, newMinZ = 0;
            double newMaxX = 0, newMaxY = 0, newMaxZ = 0;

            switch (to) {
                case NORTH -> {
                    newMinX = minX;
                    newMinY = minZ;
                    newMinZ = 1 - maxY;

                    newMaxX = maxX;
                    newMaxY = maxZ;
                    newMaxZ = 1 - minY;
                }
                case SOUTH -> {
                    newMinX = 1 - maxX;
                    newMinY = minZ;
                    newMinZ = minY;

                    newMaxX = 1 - minX;
                    newMaxY = maxZ;
                    newMaxZ = maxY;
                }
                case WEST -> {
                    newMinX = minY;
                    newMinY = minZ;
                    newMinZ = minX;

                    newMaxX = maxY;
                    newMaxY = maxZ;
                    newMaxZ = maxX;
                }
                case EAST -> {
                    newMinX = 1 - maxY;
                    newMinY = minZ;
                    newMinZ = 1 - maxX;

                    newMaxX = 1 - minY;
                    newMaxY = maxZ;
                    newMaxZ = 1 - minX;
                }
            }

            result.set(Shapes.or(result.get(), Shapes.box(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ)));
        });

        return result.get();
    }
}
