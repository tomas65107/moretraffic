package com.tomas65107.moretraffic.data;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ISimpleBlockProperties {

    enum Material {
        FULLBLOCK_TOUGH(12.0f, 40.0f),
        FULLBLOCK_NORMAL(9f, 27f),
        MODEL_TOUGH(7f, 10f),
        MODEL_NORMAL(4f, 7f);

        Material(float destroyTime, float explosionResistance) {
            this.destroyTime = destroyTime;
            this.explosionResistance = explosionResistance;
        }

        public final float destroyTime;
        public final float explosionResistance;
    }

    static BlockBehaviour.Properties set(BlockBehaviour.Properties base, SoundType sound, MapColor color, Material material) {
        base.sound(sound);
        base.mapColor(color);
        base.strength(material.destroyTime, material.explosionResistance);
        return base;
    }

    public default @NotNull List<ItemStack> getDrops(BlockState state, LootParams.@NotNull Builder params) {
        return List.of(state.getBlock().asItem().getDefaultInstance());
    }

}
