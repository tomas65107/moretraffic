package com.tomas65107.moretraffic.registration;

import com.tomas65107.moretraffic.block.*;
import com.tomas65107.moretraffic.mod.MoreTraffic;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MTBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MoreTraffic.MODID);

    public static final DeferredBlock<AdvancedTrafficLightBlock> ADV_3_TRAFFIC_LIGHT = BLOCKS.registerBlock("advanced_3_traffic_light", AdvancedTrafficLightBlock::new);
    public static final DeferredBlock<AdvancedTrafficLightBlock> ADV_2_TRAFFIC_LIGHT = BLOCKS.registerBlock("advanced_2_traffic_light", AdvancedTrafficLightBlock::new);
    public static final DeferredBlock<AdvancedTrafficLightBlock> ADV_1_TRAFFIC_LIGHT = BLOCKS.registerBlock("advanced_1_traffic_light", AdvancedTrafficLightBlock::new);
    public static final DeferredBlock<TrafficDisplayBlock> TRAFFIC_DISPLAY = BLOCKS.registerBlock("traffic_display", TrafficDisplayBlock::new);

    public static final DeferredBlock<FlashingBlinkerBlock> BLINKER = BLOCKS.registerBlock("blinker", FlashingBlinkerBlock::new);

    public static final DeferredBlock<LightControlCabinetBlock> LIGHT_CONTROL_CABINET = BLOCKS.registerBlock("control_cabinet", LightControlCabinetBlock::new);

    public static final DeferredBlock<Block> TRAFFIC_TRUSS = BLOCKS.registerBlock("traffic_truss", TrafficTrussBlock::new);
    public static final DeferredBlock<Block> TRAFFIC_TRUSS_WALKWAY = BLOCKS.registerBlock("traffic_truss_walkway", TrafficTrussBlock::new);
    public static final DeferredBlock<Block> TRAFFIC_PILLAR = BLOCKS.registerBlock("traffic_pillar", TrafficPillarBlock::new);
    public static final DeferredBlock<Block> BALLAST_GRAY = BLOCKS.registerBlock("ballast_gray", BallastBlock::new);

}
