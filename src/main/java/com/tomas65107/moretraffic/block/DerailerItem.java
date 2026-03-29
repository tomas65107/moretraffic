package com.tomas65107.moretraffic.block;

import com.simibubi.create.content.trains.track.TrackTargetingBlockItem;
import com.tomas65107.moretraffic.mod.MoreTrafficCompat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class DerailerItem extends TrackTargetingBlockItem {
    public DerailerItem(Block block, Item.Properties props) {
        super(block, props, MoreTrafficCompat.DERAILER);
    }
}