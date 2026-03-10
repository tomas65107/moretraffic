package com.tomas65107.moretraffic.registration;

import com.tomas65107.moretraffic.block.FlashingBlinkerBlockEntity;
import com.tomas65107.moretraffic.block.LightControlCabinetBlockEntity;
import com.tomas65107.moretraffic.block.TrafficDisplayBlockEntity;
import com.tomas65107.moretraffic.mod.MoreTraffic;
import com.tomas65107.moretraffic.block.AdvancedTrafficLightBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MTBE {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MoreTraffic.MODID);

    public static final Supplier<BlockEntityType<AdvancedTrafficLightBlockEntity>> ADVANCED_TRAFFIC_LIGHT_BE =
            BLOCK_ENTITIES.register("advanced_traffic_light",
                    () -> BlockEntityType.Builder.of(
                            AdvancedTrafficLightBlockEntity::new,
                            MTBlocks.ADV_3_TRAFFIC_LIGHT.get(),
                            MTBlocks.ADV_2_TRAFFIC_LIGHT.get(),
                            MTBlocks.ADV_1_TRAFFIC_LIGHT.get()
                    ).build(null)
            );
    public static final Supplier<BlockEntityType<LightControlCabinetBlockEntity>> CONTROL_CABINET_BE =
            BLOCK_ENTITIES.register("light_control_cabinet",
                    () -> BlockEntityType.Builder.of(
                            LightControlCabinetBlockEntity::new,
                            MTBlocks.LIGHT_CONTROL_CABINET.get()
                    ).build(null)
            );
    public static final Supplier<BlockEntityType<FlashingBlinkerBlockEntity>> BLINKER_BE =
            BLOCK_ENTITIES.register("blinker",
                    () -> BlockEntityType.Builder.of(
                            FlashingBlinkerBlockEntity::new,
                            MTBlocks.BLINKER.get()
                    ).build(null)
            );
    public static final Supplier<BlockEntityType<TrafficDisplayBlockEntity>> TRAFFIC_DISPLAY_BE =
            BLOCK_ENTITIES.register("traffic_display",
                    () -> BlockEntityType.Builder.of(
                            TrafficDisplayBlockEntity::new,
                            MTBlocks.TRAFFIC_DISPLAY.get()
                    ).build(null)
            );

}
