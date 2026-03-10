package com.tomas65107.moretraffic.registration;

import com.tomas65107.moretraffic.block.FlashingBlinkerBlock;
import com.tomas65107.moretraffic.block.LightControlCabinetBlock;
import com.tomas65107.moretraffic.block.TrafficDisplayBlock;
import com.tomas65107.moretraffic.mod.MoreTraffic;
import com.tomas65107.moretraffic.block.AdvancedTrafficLightBlock;
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


}
