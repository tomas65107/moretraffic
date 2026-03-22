package com.tomas65107.moretraffic.gui.containers;

import com.tomas65107.moretraffic.block.LEDStripBlockEntity;
import com.tomas65107.moretraffic.data.AbstractSheet;
import com.tomas65107.moretraffic.data.ColorsManager;
import com.tomas65107.moretraffic.data.SpritesManager;
import com.tomas65107.moretraffic.gui.components.BetterEditBox;
import com.tomas65107.moretraffic.gui.components.HelpElementWidget;
import com.tomas65107.moretraffic.gui.components.LabelWidget;
import com.tomas65107.moretraffic.gui.components.buttons.AdvancedButton;
import com.tomas65107.moretraffic.gui.components.buttons.ColorButton;
import com.tomas65107.moretraffic.gui.makers.GridMaker;
import com.tomas65107.moretraffic.gui.tooltip.NoticeBoxTooltip;
import com.tomas65107.moretraffic.mod.MoreTraffic;
import com.tomas65107.moretraffic.networking.ClientSyncLightPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.swing.text.StyledEditorKit;
import java.util.Arrays;
import java.util.function.Consumer;

import static com.tomas65107.moretraffic.data.ColorsManager.*;
import static com.tomas65107.moretraffic.data.SpritesManager.ICON_INFO;
import static com.tomas65107.moretraffic.data.helpers.ColorHelper.rgb;
import static net.neoforged.neoforge.network.PacketDistributor.sendToServer;

public class LEDStripScreen extends AbstractTomiContainerScreen<LEDStripMenu>{

    private final LEDStripMenu menu;
    private final BlockPos pos;
    private final LEDStripBlockEntity be;

    private int guiX;
    private int guiY;

    protected boolean queueRefresh;
    private int timer = 0;

    int guiWidth = 200;
    int guiHeight = 192;

    BlockEntityRenderDispatcher beDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();

    public LEDStripScreen(LEDStripMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.menu = menu;
        this.be = menu.be;
        this.pos = be.getBlockPos();
    }

    @Override
    protected void init() {
        super.init();

        guiX = (this.width - guiWidth) / 2;
        guiY = (this.height - guiHeight) / 2;

        assert Minecraft.getInstance().level != null;
        assert Minecraft.getInstance().level.isClientSide;
        assert be != null;

        addBaseWidget(
                new LabelWidget(guiX+10, guiY +10, Component.translatable("gui.moretraffic.led_light.title").withStyle(ChatFormatting.BOLD), 0xFFFFFF, true)
        );

        NoticeBoxTooltip tooltip1 = new NoticeBoxTooltip(
                Component.translatable("gui.moretraffic.led_light.title"),
                Component.translatable("gui.moretraffic.led_light.message"),
                null, ColorsManager.HEADER, false);
        int finalWidthOfPrevContent = Minecraft.getInstance().font.width(Component.translatable("gui.moretraffic.led_light.title").withStyle(ChatFormatting.BOLD));
        addBaseWidget(
            new HelpElementWidget(guiX+finalWidthOfPrevContent + 10, guiY +6, ICON_INFO, tooltip1)
        );

        addBaseWidget(
                new LabelWidget(guiX+145, guiY+30, Component.translatable("gui.moretraffic.led_light.size").withColor(rgb(SECONDARY)), 0xFFFFFF, true)
        );
        BetterEditBox sizetextbox = new BetterEditBox(guiX + 148, guiY + 43, 40, 14);
        sizetextbox.setBordered(false);
        sizetextbox.setValue(be.sizeX + ", " + be.sizeY);
        sizetextbox.setTextColor(rgb(PRIMARY));
        sizetextbox.onSave(() -> {
            try {
                int[] values = Arrays.stream(sizetextbox.getValue().split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
                if (!((1 < values[0] && values[0] < 17) && (1 < values[1] && values[1] < 17))) throw new Exception();
                be.sizeX = values[0]; be.sizeY = values[1];
                updateBEAndRefreshBE();
            } catch (Exception ignored) {}
        });
        sizetextbox.onChange(text -> {
            try {
                int[] values = Arrays.stream(text.split(", ")).map(String::trim).mapToInt(Integer::parseInt).toArray();
                if (!((1 < values[0] && values[0] < 17) && (1 < values[1] && values[1] < 17))) throw new Exception();

                sizetextbox.setTextColor(rgb(PRIMARY));
                sizetextbox.hideFloatingTooltip();
            } catch (Exception e) {
                sizetextbox.setTextColor(rgb(INVALID));
                sizetextbox.showFloatingTooltip(new NoticeBoxTooltip(Component.literal("Failed to get valid coordinates that are 1..17"), INVALID));
            }
        });
        addBaseWidget(sizetextbox);


        addBaseWidget(
                new LabelWidget(guiX+145, guiY+76, Component.translatable("gui.moretraffic.led_light.startpos").withColor(rgb(SECONDARY)), 0xFFFFFF, true)
        );
        BetterEditBox offsettextbox = new BetterEditBox(guiX + 148, guiY + 89, 40, 14);
        offsettextbox.setBordered(false);
        offsettextbox.setValue(be.startPosX + ", " + be.startPosY);
        offsettextbox.setTextColor(rgb(PRIMARY));
        offsettextbox.onSave(() -> {
            try {
                int[] values = Arrays.stream(offsettextbox.getValue().split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
                if (!((values[0] < 17) && (values[1] < 17))) throw new Exception();
                be.startPosX = values[0]; be.startPosY = values[1];
                updateBEAndRefreshBE();
            } catch (Exception ignored) {}
        });
        offsettextbox.onChange(text -> {
            try {
                int[] values = Arrays.stream(text.split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
                if (!((values[0] < 17) && (values[1] < 17))) throw new Exception();

                offsettextbox.setTextColor(rgb(PRIMARY));
                offsettextbox.hideFloatingTooltip();
            } catch (Exception e) {
                offsettextbox.setTextColor(rgb(INVALID));
                offsettextbox.showFloatingTooltip(new NoticeBoxTooltip(Component.literal("Failed to get valid coordinates that are ..17"), INVALID));
            }
        });
        addBaseWidget(offsettextbox);


        addBaseWidget(new AdvancedButton(guiX+10, guiY+167, 15, 15, SpritesManager.EDIT_CENTER, new NoticeBoxTooltip(Component.translatable("gui.moretraffic.led_light.center")), true, b->{
            be.startPosX = (16 - be.sizeX) / 2;
            be.startPosY = (16 - be.sizeY) / 2;
            updateBEAndRefreshBE();
        }));

        addBaseWidget(new ColorButton(guiX+35, guiY+167, 15, 15, be.color.getTextureDiffuseColor(), b->{
            int sheetWidth = 173;
            int sheetHeight = 94;
            int sheetX = guiX + (guiWidth - sheetWidth) / 2;
            int sheetY = guiY + (guiHeight - sheetHeight) / 2;
            this.addElement(
                    new AbstractSheet(sheetX, sheetY, Component.translatable("gui.moretraffic.control_cabinet.instruction.modify_light.select_color").getString(), true, sheetWidth, sheetHeight) {
                        @Override
                        public void init(Consumer<AbstractWidget> adder) {
                            NoticeBoxTooltip tooltip1 = new NoticeBoxTooltip(
                                    Component.translatable("gui.moretraffic.advanced_traffic_light.options.change_color"),
                                    Component.translatable("gui.moretraffic.advanced_traffic_light.options.change_color.message"),
                                    null, ColorsManager.HEADER, false);
                            int finalWidthOfPrevContent = Minecraft.getInstance().font.width(Component.translatable("gui.moretraffic.control_cabinet.instruction.modify_light.select_color").withStyle(ChatFormatting.BOLD));
                            adder.accept(new HelpElementWidget(finalWidthOfPrevContent + 10, 6, ICON_INFO, tooltip1));

                            new GridMaker(10, 25, adder, c -> {
                                be.color = c;
                                updateBEAndRefreshBE(); refreshContent();
                            }, be.color, true);
                        }
                    });
        }, false, true));


    }

    private void updateBEAndRefreshBE() {
        if (be == null || minecraft == null || minecraft.player == null) return;
        assert minecraft.level != null;

        // serialize BE state into NBT
        CompoundTag tag = new CompoundTag();
        be.saveAdditional(tag, minecraft.level.registryAccess());

        // create and send packet
        ClientSyncLightPacket packet = new ClientSyncLightPacket(be.getBlockPos(), tag);
        sendToServer(packet);

        //queue update on ui
        queueRefresh = true;
    }

    @Override
    protected void containerTick() {

        if (queueRefresh) {
            MoreTraffic.LOGGER.debug("queuing ui refresh...");
            queueRefresh = false;
            timer = 2;
        }
        if (timer == 0) {
            refreshContent();
            timer = -1;
        } else if (timer != -1) {
            timer--;
        }

        super.containerTick();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(guiX +10, guiY +156.5, 10);

        float scale = 127f;
        guiGraphics.pose().scale(scale, -scale, scale);
        beDispatcher.render(be, 0f, guiGraphics.pose(), Minecraft.getInstance().renderBuffers().bufferSource());

        guiGraphics.pose().popPose();

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {

        ResourceLocation BG_TEXTURE =
                ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/led_light_gui.png");

        int x = (this.width - guiWidth) / 2;
        int y = (this.height - guiHeight) / 2;

        guiGraphics.blit(
                BG_TEXTURE,
                x, y,          // screen position
                0, 0,          // texture UV start
                guiWidth, guiHeight, // size to draw
                256, 256       // full texture size
        );

    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Do nothing
    }
}
