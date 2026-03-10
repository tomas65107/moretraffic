package com.tomas65107.moretraffic.gui.containers;

import com.tomas65107.moretraffic.block.AdvancedTrafficLightBlock;
import com.tomas65107.moretraffic.block.AdvancedTrafficLightBlockEntity;
import com.tomas65107.moretraffic.data.AbstractSheet;
import com.tomas65107.moretraffic.data.ColorsManager;
import com.tomas65107.moretraffic.data.SpritesManager;
import com.tomas65107.moretraffic.data.TrafficLightLight;
import com.tomas65107.moretraffic.data.helpers.MaskConverter;
import com.tomas65107.moretraffic.data.trafficlightproperties.TrafficLightProperty;
import com.tomas65107.moretraffic.gui.components.HelpElementWidget;
import com.tomas65107.moretraffic.gui.components.buttons.AdvancedButton;
import com.tomas65107.moretraffic.gui.makers.GridMaker;
import com.tomas65107.moretraffic.gui.components.LabelWidget;
import com.tomas65107.moretraffic.gui.makers.MaskGridMaker;
import com.tomas65107.moretraffic.gui.makers.ModelSliderChangerMaker;
import com.tomas65107.moretraffic.gui.tooltip.NoticeBoxTooltip;
import com.tomas65107.moretraffic.mod.MoreTraffic;
import com.tomas65107.moretraffic.networking.ClientSenderPacketTrafficLight;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.tomas65107.moretraffic.data.ColorsManager.SECONDARY;
import static com.tomas65107.moretraffic.data.SpritesManager.ICON_INFO;
import static com.tomas65107.moretraffic.data.helpers.ColorHelper.rgb;
import static com.tomas65107.moretraffic.gui.components.buttons.AdvancedButton.NORMAL_HEIGHT;
import static com.tomas65107.moretraffic.networking.ClientSenderPacketTrafficLight.shortsToBytes;
import static net.neoforged.neoforge.network.PacketDistributor.sendToServer;

public class AdvancedTrafficLightScreen extends AbstractTomiContainerScreen<AdvancedTrafficLightMenu> {

    private final AdvancedTrafficLightMenu menu;
    private final BlockPos pos;
    private final AdvancedTrafficLightBlockEntity be;

    private int guiX;
    private int guiY;

    protected boolean queueRefresh;
    private int timer = 0;

    int guiWidth = 244;
    int guiHeight = 199;

    BlockEntityRenderDispatcher beDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();

    public AdvancedTrafficLightScreen(AdvancedTrafficLightMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.menu = menu;
        this.be = menu.be;
        this.pos = be.getBlockPos();
    }

    @Override
    protected void init() {
        super.init();

        guiX = (this.width - guiWidth) / 2 ;
        guiY = (this.height - guiHeight) / 2 ;

        assert Minecraft.getInstance().level != null;
        assert Minecraft.getInstance().level.isClientSide;
        assert be != null;

        addBaseWidget(
                new LabelWidget(guiX + 10, guiY +10, Component.translatable("gui.moretraffic.advanced_traffic_light.title").withStyle(ChatFormatting.BOLD), 0xFFFFFF, true)
        );

        int index = 0;
        int currentY = guiY + 30;
        for (TrafficLightLight light : be.lights) {
            int finalIndex = index;

            addBaseWidget(
                    new LabelWidget(guiX+10, currentY, Component.translatable("core.moretraffic.advanced_traffic_light.light"+(finalIndex+1)).withColor(rgb(SECONDARY)), 0xFFFFFF, true)
            );
            currentY += 11;

            addBaseWidget(
                    Button.builder(Component.translatable("gui.moretraffic.configure").withColor(rgb(ColorsManager.HEADER)),
                            b -> {
                                int sheetWidth = 220;
                                int sheetHeight = 230;

                                int sheetX = guiX + (guiWidth - sheetWidth) / 2;
                                int sheetY = guiY + (guiHeight - sheetHeight) / 2;
                        this.addElement(
                                new AbstractSheet(sheetX, sheetY, Component.translatable("gui.moretraffic.advanced_traffic_light.options.title.light"+(finalIndex+1)).getString(), true, sheetWidth, sheetHeight) {

                            @Override
                            public void init(Consumer<AbstractWidget> adder) {

                                //LIGHT COLOR
                                adder.accept(new LabelWidget(10, 25, Component.translatable("gui.moretraffic.advanced_traffic_light.options.change_color"), rgb(SECONDARY), true));

                                NoticeBoxTooltip tooltip1 = new NoticeBoxTooltip(
                                        Component.translatable("gui.moretraffic.advanced_traffic_light.options.change_color"),
                                        Component.translatable("gui.moretraffic.advanced_traffic_light.options.change_color.message"),
                                        null, ColorsManager.HEADER, false);
                                int finalWidthOfPrevContent = Minecraft.getInstance().font.width(Component.translatable("gui.moretraffic.advanced_traffic_light.options.change_color"));
                                adder.accept(new HelpElementWidget(finalWidthOfPrevContent + 10, 21, ICON_INFO, tooltip1));

                                new GridMaker(
                                    10, 25 + 11, adder,
                                    c -> {
                                        sendToServer(
                                                new ClientSenderPacketTrafficLight(
                                                        pos,
                                                        finalIndex,
                                                        c.getId(),
                                                        shortsToBytes(be.lights.get(finalIndex).mask.getRows())
                                                )
                                        );
                                        queueRefresh = true;
                                        }, be.lights.get(finalIndex).color
                                );

                                //MASK
                                adder.accept(new LabelWidget(10, 80, Component.translatable("gui.moretraffic.advanced_traffic_light.options.change_mask"), rgb(SECONDARY), true));

                                NoticeBoxTooltip tooltip2 = new NoticeBoxTooltip(
                                        Component.translatable("gui.moretraffic.advanced_traffic_light.options.change_mask"),
                                        Component.translatable("gui.moretraffic.advanced_traffic_light.options.change_mask.message"),
                                        Component.translatable("gui.moretraffic.advanced_traffic_light.options.change_mask.cta"), ColorsManager.HEADER, false);
                                int finalWidthOfPrevContent1 = Minecraft.getInstance().font.width(Component.translatable("gui.moretraffic.advanced_traffic_light.options.change_mask"));;
                                adder.accept(new HelpElementWidget(finalWidthOfPrevContent1 + 10, 76, ICON_INFO, tooltip2));

                                NoticeBoxTooltip tooltip3 = new NoticeBoxTooltip(
                                        Component.translatable("gui.moretraffic.advanced_traffic_light.options.change_mask.controls"),
                                        Component.translatable("gui.moretraffic.advanced_traffic_light.options.change_mask.controls.message"),
                                        null, ColorsManager.HEADER, false);
                                adder.accept(new HelpElementWidget((finalWidthOfPrevContent1 + 10)+16, 76, ICON_INFO, tooltip3));

                                new MaskGridMaker(10, 90, be.lights.get(finalIndex).mask, adder, newMask-> {
                                    sendToServer(new ClientSenderPacketTrafficLight(
                                                    pos,
                                                    finalIndex,
                                                    be.lights.get(finalIndex).color.getId(),
                                                    shortsToBytes(newMask.getRows())
                                            )
                                    );
                                    timer = 1;
                                });

                                adder.accept(
                                        new AdvancedButton(144, 90, 70, NORMAL_HEIGHT, Component.translatable("gui.moretraffic.advanced_traffic_light.options.change_mask.clear"), SpritesManager.ICON_CLEAR, a-> {
                                            sendToServer(new ClientSenderPacketTrafficLight(
                                                            pos,
                                                            finalIndex,
                                                            be.lights.get(finalIndex).color.getId(),
                                                            shortsToBytes(new TrafficLightLight.TrafficLightMask().getRows())
                                                    )
                                            ); queueRefresh = true;
                                        })
                                );
                                adder.accept(
                                        new AdvancedButton(144, 90+20+5, 70, NORMAL_HEIGHT, Component.translatable("gui.moretraffic.advanced_traffic_light.options.change_mask.invert"), SpritesManager.ICON_INVERT, a->{
                                            short[] rows = be.lights.get(finalIndex).mask.getRows().clone();
                                            for (int i = 0; i < rows.length; i++) {
                                                rows[i] = (short) ~rows[i];
                                            }
                                            sendToServer(new ClientSenderPacketTrafficLight(
                                                            pos,
                                                            finalIndex,
                                                            be.lights.get(finalIndex).color.getId(),
                                                            shortsToBytes(rows)
                                                    )
                                            ); queueRefresh = true;
                                        })
                                );

                                adder.accept(new AdvancedButton(144+28, 100+40, NORMAL_HEIGHT, NORMAL_HEIGHT, null, SpritesManager.ICON_IMPORT, new NoticeBoxTooltip(Component.translatable("gui.moretraffic.advanced_traffic_light.options.change_mask.import"), Component.translatable("gui.moretraffic.advanced_traffic_light.options.change_mask.import.message"), null), a->{
                                    try {
                                        Pattern pattern = Pattern.compile("(?<=^|,)\\d+(?=,|$)");
                                        Matcher matcher = pattern.matcher(Minecraft.getInstance().keyboardHandler.getClipboard());
                                        short[] newRows = new short[16];
                                        int countOfRows = 0;
                                        while (matcher.find()) {
                                            if (countOfRows > 16) throw new Exception();
                                            short number = Short.parseShort(matcher.group());
                                            newRows[countOfRows] = number;
                                            countOfRows++;
                                        }
                                        if (countOfRows < 16) throw new Exception();
                                        sendToServer(new ClientSenderPacketTrafficLight(
                                                        pos,
                                                        finalIndex,
                                                        be.lights.get(finalIndex).color.getId(),
                                                        shortsToBytes(newRows)
                                                )
                                        ); queueRefresh = true;
                                    } catch (Exception ex) {
                                        a.setTooltip(Tooltip.create(Component.translatable("gui.moretraffic.import_invalid_data")));
                                    }
                                })
                                );
                                adder.accept(
                                        new AdvancedButton(144+30+20, 100+40, NORMAL_HEIGHT, NORMAL_HEIGHT, null, SpritesManager.ICON_EXPORT, new NoticeBoxTooltip(Component.translatable("gui.moretraffic.advanced_traffic_light.options.change_mask.export"), Component.translatable("gui.moretraffic.advanced_traffic_light.options.change_mask.export.message"), null), a->{
                                            short[] rows = be.lights.get(finalIndex).mask.getRows();
                                            StringBuilder sb = new StringBuilder();
                                            for (short s : rows) sb.append(s).append(",");
                                            Minecraft.getInstance().keyboardHandler.setClipboard(sb.toString());
                                        })
                                );
                                adder.accept(
                                        new AdvancedButton(144, 100+40, NORMAL_HEIGHT, NORMAL_HEIGHT, null, SpritesManager.ICON_EXPORT_FROM_IMAGE, new NoticeBoxTooltip(Component.translatable("gui.moretraffic.advanced_traffic_light.options.change_mask.import_img"), Component.translatable("gui.moretraffic.advanced_traffic_light.options.change_mask.import_img.message"), Component.translatable("gui.moretraffic.advanced_traffic_light.options.change_mask.import_img.cta")), a->{
                                            try {
                                                String filepath = MaskConverter.openResourcePopUp();
                                                if (filepath == null || filepath.isEmpty()) return;
                                                short[] convertedRows = MaskConverter.convertImage(new File(filepath));

                                                sendToServer(new ClientSenderPacketTrafficLight(
                                                                pos,
                                                                finalIndex,
                                                                be.lights.get(finalIndex).color.getId(),
                                                                shortsToBytes(convertedRows)
                                                        )
                                                ); timer = 15;

                                            } catch (Exception e) {
                                                throw new RuntimeException("092 ¨ Converting a file to mask failed, no error handling implemented yet. Please report this. Nothing should be corrupted.\n -> "+e);
                                            }
                                        })
                                );
                            }

                        });
                    }
                    ).bounds(guiX+10, currentY, 70, 20).build()
            );
            currentY += 30;
            index++;
        }

        //2. COLUMN
        currentY = guiY + 30;

        for (TrafficLightProperty.PropertyTypes property : TrafficLightProperty.PropertyTypes.values()) {
            addBaseWidget(
                    new LabelWidget(guiX + 100, currentY, Component.translatable("core.moretraffic.advanced_traffic_light.property."+property.getNameOfProperty()).withColor(rgb(SECONDARY)), 0xFFFFFF, true)
            );
            currentY += 11+1;
            new ModelSliderChangerMaker<>(guiX + 100, currentY, be, property.getClassOfProperty(), this::addBaseWidget, b -> queueRefresh = b, this::shouldRenderTooltips);
            currentY += 30-1;
        }

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
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.pose().pushPose();

        guiGraphics.pose().translate(guiX - 80, guiY + ((float) guiHeight)/1.3, 10);

        float scale = 90f;
        guiGraphics.pose().scale(scale, -scale, scale);

        //model
        BlockState model = be.getBlockState().setValue(AdvancedTrafficLightBlock.FACING, Direction.SOUTH);

        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                model,
                guiGraphics.pose(),
                Minecraft.getInstance().renderBuffers().bufferSource(),
                0xF000F0,
                OverlayTexture.NO_OVERLAY
        );

        // traffic lights
        BlockState originalBeBs = be.getBlockState();
        be.setBlockState(model);
        beDispatcher.render(be, 0f, guiGraphics.pose(), Minecraft.getInstance().renderBuffers().bufferSource());
        be.setBlockState(originalBeBs);

        guiGraphics.pose().popPose();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {

        ResourceLocation BG_TEXTURE =
                ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/advanced_traffic_light_gui.png");

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