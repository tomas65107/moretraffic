package com.tomas65107.moretraffic.gui.components.buttons;

import com.tomas65107.moretraffic.block.AdvancedTrafficLightBlock;
import com.tomas65107.moretraffic.block.AdvancedTrafficLightBlockEntity;
import com.tomas65107.moretraffic.data.ColorsManager;
import com.tomas65107.moretraffic.data.trafficlightproperties.TrafficLightOrientation;
import com.tomas65107.moretraffic.data.trafficlightproperties.TrafficLightPosition;
import com.tomas65107.moretraffic.data.trafficlightproperties.TrafficLightProperty;
import com.tomas65107.moretraffic.data.trafficlightproperties.TrafficLightScale;
import com.tomas65107.moretraffic.gui.tooltip.NoticeBoxTooltip;
import com.tomas65107.moretraffic.networking.UpdateTrafficLightStatePacket;
import com.tomas65107.moretraffic.registration.MTBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.tomas65107.moretraffic.data.helpers.BlockStateHelper.setValueFromString;
import static com.tomas65107.moretraffic.data.helpers.ColorHelper.rgb;

public class ModelButton extends Button {

    private final boolean selected;
    private final AdvancedTrafficLightBlockEntity be;
    private BlockState newState;
    private final TrafficLightProperty value;
    private final Consumer<Boolean> shouldQueueRefresh;
    private boolean isCompatible;
    private final boolean shouldRenderTooltips;

    public ModelButton(int x, int y, int width, int height, AdvancedTrafficLightBlockEntity be, TrafficLightProperty value, boolean selected, Consumer<Boolean> shouldQueueRefresh, boolean shouldRenderTooltips) {
        super(x, y, width, height, Component.empty(), u->{}, DEFAULT_NARRATION);
        this.selected = selected;
        this.be = be;
        this.value = value;
        this.shouldQueueRefresh = shouldQueueRefresh;
        this.shouldRenderTooltips = shouldRenderTooltips;

        newState = setValueFromString(
                be.getBlockState(),
                value.getClassType().getNameOfProperty(),
                value.getSerializedName()
        );
        newState = newState.setValue(AdvancedTrafficLightBlock.FACING, Direction.SOUTH);

        isCompatible = true;
        if (be.getBlockState().getBlock() == MTBlocks.ADV_3_TRAFFIC_LIGHT.get()) {
            if (value.getClassType().getClassOfProperty().equals(TrafficLightPosition.class) && value.equals(TrafficLightPosition.BOTTOM)) {isCompatible = false;}
            if (value.getClassType().getClassOfProperty().equals(TrafficLightScale.class) && !value.equals(TrafficLightScale.S1_0X)) {isCompatible = false;}
        }

        if (be.getBlockState().getBlock() == MTBlocks.ADV_2_TRAFFIC_LIGHT.get()) {
            if (value.getClassType().getClassOfProperty().equals(TrafficLightScale.class) && (value.equals(TrafficLightScale.S2_0X) || value.equals(TrafficLightScale.FULLBLOCK))) {isCompatible = false;}
        }

        if (be.getBlockState().getBlock() == MTBlocks.ADV_1_TRAFFIC_LIGHT.get()) {
            if (value.getClassType().getClassOfProperty().equals(TrafficLightScale.class) && value.equals(TrafficLightScale.FULLBLOCK) && be.getBlockState().getValue(AdvancedTrafficLightBlock.ORIENTATION).equals(TrafficLightOrientation.VERTICAL)) {isCompatible = false;}
            if (value.getClassType().getClassOfProperty().equals(TrafficLightScale.class) && value.equals(TrafficLightScale.FULLBLOCK) && be.getBlockState().getValue(AdvancedTrafficLightBlock.POSITION).equals(TrafficLightPosition.BOTTOM)) {isCompatible = false;}
        }
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int bg = this.isHovered ?
                //hovered
                selected ? rgb(ColorsManager.BUTTON_SELECTED_HOVER) : rgb(ColorsManager.TERTIARY)
                ://not hovered
                selected ? rgb(ColorsManager.BUTTON_SELECTED) : rgb(ColorsManager.SECONDARY);
        if (!isCompatible) {
            bg = rgb(ColorsManager.BACKGROUND);
        }

        int bgWidth = width + (selected ? 2 : 0);
        int bgHeight = height + (selected ? 2 : 0);
        guiGraphics.fill(getX() - (selected ? 1 : 0), getY() - (selected ? 1 : 0), getX() + bgWidth, getY() + bgHeight, bg);

        guiGraphics.pose().pushPose();

        float centerX = getX() + width / 2f;
        float centerY = getY() + bgHeight / 2f;
        guiGraphics.pose().translate(centerX, centerY, 100);
        float scale = 14f;
        guiGraphics.pose().scale(scale, -scale, scale);

        guiGraphics.pose().translate(-0.5f, -0.5f, -0.5f); //

        if (isCompatible) {
            //model
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                    newState,
                    guiGraphics.pose(),
                    Minecraft.getInstance().renderBuffers().bufferSource(),
                    0xF000F0,
                    OverlayTexture.NO_OVERLAY
            );
            // traffic lights (be)
            BlockState originalBeBs = be.getBlockState();
            be.setBlockState(newState);
            Minecraft.getInstance().getBlockEntityRenderDispatcher().render(
                    be,
                    0f,
                    guiGraphics.pose(),
                    Minecraft.getInstance().renderBuffers().bufferSource()
            );
            be.setBlockState(originalBeBs);
        }
        guiGraphics.pose().popPose();

        if (isHovered && !isCompatible && shouldRenderTooltips) {
            var tooltip = new NoticeBoxTooltip(
                    null,
                    Component.translatable("gui.moretraffic.advanced_traffic_light.change_properties.incompatible"),
                    null);
            guiGraphics.renderTooltip(Minecraft.getInstance().font, List.of(Component.empty()), Optional.of(tooltip), ItemStack.EMPTY, mouseX, mouseY);
        }

    }



    @Override
    public void onPress() {
        if (isHovered && isCompatible) {
            PacketDistributor.sendToServer(
                    new UpdateTrafficLightStatePacket(
                            be.getBlockPos(),
                            value.getClassType().getNameOfProperty(),
                            value.getSerializedName()
                    )
            );
            shouldQueueRefresh.accept(true);
        }
    }

}
