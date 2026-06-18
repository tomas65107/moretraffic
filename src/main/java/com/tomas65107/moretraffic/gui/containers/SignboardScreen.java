package com.tomas65107.moretraffic.gui.containers;

import com.tomas65107.moretraffic.block.SignboardBlock;
import com.tomas65107.moretraffic.block.SignboardBlockEntity;
import com.tomas65107.moretraffic.data.AbstractSheet;
import com.tomas65107.moretraffic.data.BoardElement;
import com.tomas65107.moretraffic.data.ColorsManager;
import com.tomas65107.moretraffic.data.SpritesManager;
import com.tomas65107.moretraffic.gui.AbstractTomiContainerScreen;
import com.tomas65107.moretraffic.gui.components.*;
import com.tomas65107.moretraffic.gui.components.buttons.AdvancedButton;
import com.tomas65107.moretraffic.gui.components.buttons.ColorButton;
import com.tomas65107.moretraffic.gui.makers.GridMaker;
import com.tomas65107.moretraffic.gui.tooltip.NoticeBoxTooltip;
import com.tomas65107.moretraffic.mod.MoreTraffic;
import com.tomas65107.moretraffic.networking.ClientSyncSignboard;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.tomas65107.moretraffic.data.ColorsManager.*;
import static com.tomas65107.moretraffic.data.SpritesManager.INFO;
import static com.tomas65107.moretraffic.gui.components.buttons.AdvancedButton.NORMAL_HEIGHT;
import static com.tomas65107.moretraffic.helpers.ColorHelper.rgb;
import static com.tomas65107.moretraffic.helpers.TextCutter.cutTextComponent;
import static net.neoforged.neoforge.network.PacketDistributor.sendToServer;

public class SignboardScreen extends AbstractTomiContainerScreen<SignboardMenu> {

    private final SignboardMenu menu;
    private final BlockPos pos;
    private final SignboardBlockEntity be;

    private int guiX;
    private int guiY;

    protected boolean queueRefresh;
    private int timer = 0;

    int guiWidth = 191;
    int guiHeight = 164;

    AtomicInteger focusedIndex = new AtomicInteger(-1);

    BlockEntityRenderDispatcher beDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();

    public SignboardScreen(SignboardMenu menu, Inventory inv, Component title) {
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
                new LabelWidget(guiX+10, guiY +10, Component.translatable("gui.moretraffic.signboard.title").withStyle(ChatFormatting.BOLD), 0xFFFFFF, true)
        );

        NoticeBoxTooltip tooltip1 = new NoticeBoxTooltip(
                Component.translatable("gui.moretraffic.signboard.title"),
                Component.translatable("gui.moretraffic.signboard.message"),
                null, NoticeBoxTooltip.TooltipType.INFORMATIVE);
        int finalWidthOfPrevContent = Minecraft.getInstance().font.width(Component.translatable("gui.moretraffic.signboard.title").withStyle(ChatFormatting.BOLD));
        addBaseWidget(
                new HelpElementWidget(guiX+finalWidthOfPrevContent + 10, guiY +6, INFO, tooltip1)
        );



        if (focusedIndex.get() != -1) {
            int YOffset = 30;
            var element = be.elements.get(focusedIndex.get());

            addBaseWidget(new LabelWidget(guiX+10, guiY+YOffset, Component.translatable("gui.moretraffic.led_light.startpos").withColor(rgb(SECONDARY)), 0xFFFFFF, true));
            YOffset += 11;

            BetterEditBox startposbox = new NumberEditBox(guiX+10, guiY + YOffset);
            startposbox.setValue(element.startX + ", " + element.startY);
            startposbox.onSave(() -> {
                try {
                    int[] values = Arrays.stream(startposbox.getValue().split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
                    if (!((values[0] < 17) && (values[1] < 17) && (values[0] >= 0) && (values[1] >= 0))) throw new Exception();
                    element.startX = values[0]; element.startY = values[1];
                    updateBEAndRefreshBE();
                } catch (Exception ignored) {}
            });
            startposbox.onChange(text -> {
                try {
                    int[] values = Arrays.stream(text.split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
                    if (!((values[0] < 17) && (values[1] < 17) && (values[0] >= 0) && (values[1] >= 0))) throw new Exception();

                    startposbox.setTextColor(rgb(PRIMARY));
                    startposbox.hideFloatingTooltip();
                } catch (Exception e) {
                    startposbox.setTextColor(rgb(ERROR));
                    startposbox.showFloatingTooltip(new NoticeBoxTooltip(Component.translatable("gui.moretraffic.error_saving"), Component.literal("Start position incorrectly formated or is outside bounds!"), NoticeBoxTooltip.TooltipType.ERROR));
                }
            });
            addBaseWidget(startposbox);
            YOffset += 20;


            if (element instanceof BoardElement.Background) {

                addBaseWidget(new LabelWidget(guiX+10, guiY+YOffset, Component.literal("Color").withColor(rgb(SECONDARY)), 0xFFFFFF, true));
                YOffset += 11;
                addBaseWidget(new ColorButton(guiX+10, guiY+YOffset, 16, 16, ((BoardElement.Background) element).color.getTextureDiffuseColor(), b->{
                    int sheetWidth = 173;
                    int sheetHeight = 94;
                    int sheetX = guiX + (guiWidth - sheetWidth) / 2;
                    int sheetY = guiY + (guiHeight - sheetHeight) / 2;
                    this.addScreenSheet(
                            new AbstractSheet(sheetX, sheetY, Component.literal("Select color").getString(), true, sheetWidth, sheetHeight) {
                                @Override
                                public void init(Consumer<AbstractWidget> adder) {
                                    new GridMaker(10, 25, adder, c -> {
                                        ((BoardElement.Background) be.elements.get(focusedIndex.get())).color = c; updateBEAndRefreshBE(); refreshContent();
                                    }, ((BoardElement.Background) be.elements.get(focusedIndex.get())).color, true);
                                }
                            });
                }, false, true));
                YOffset += 20;

            } else if (element instanceof BoardElement.Text) {

                addBaseWidget(new LabelWidget(guiX+10, guiY+YOffset, Component.literal("Text").withColor(rgb(SECONDARY)), 0xFFFFFF, true));
                YOffset += 11;

                addBaseWidget(new AdvancedButton(guiX+10, guiY+YOffset, 80, NORMAL_HEIGHT, Component.translatable("gui.moretraffic.configure"), SpritesManager.EDIT_DISPLAY, p->{
                    int sheetWidth = 270;
                    int sheetHeight = 150;
                    int sheetX = guiX + (guiWidth - sheetWidth) / 2;
                    int sheetY = guiY + (guiHeight - sheetHeight) / 2;

                    this.addScreenSheet(
                            new AbstractSheet(sheetX, sheetY, Component.literal("Configure text").getString(), true, sheetWidth, sheetHeight) {
                                @Override
                                public void init(Consumer<AbstractWidget> adder) {

                                    final int[] offset = {25};
                                    for (Component component : cutTextComponent(Component.literal("Type in here simple text by surrounding it with quotes or if you want more precise control over color and format, use JSON"), 0, 250, true)) {
                                        adder.accept(new LabelWidget(10, offset[0], component.copy().withColor(rgb(SECONDARY)), rgb(PRIMARY), true));
                                        offset[0] += 10;
                                    }
                                    offset[0] += 3;

                                    var texteditbox = new BetterEditBox(10, offset[0], 250, NORMAL_HEIGHT);
                                    texteditbox.setMaxLength(800);
                                    texteditbox.setValue(((BoardElement.Text) be.elements.get(focusedIndex.get())).json);
                                    texteditbox.onSave(() -> {
                                        String json = new String(texteditbox.getValue()); // force snapshot copy

                                        ((BoardElement.Text) be.elements.get(focusedIndex.get())).json = json;
                                        updateBEAndRefreshBE();
                                    });
                                    texteditbox.onChange((s -> {
                                        if (getJsonParseErrorMessage(s) == null) {
                                            texteditbox.hideFloatingTooltip();
                                            texteditbox.setTextColor(rgb(PRIMARY));
                                        } else {
                                            texteditbox.showFloatingTooltip(new NoticeBoxTooltip(Component.translatable("gui.moretraffic.error_saving"), Component.nullToEmpty(getJsonParseErrorMessage(s)), NoticeBoxTooltip.TooltipType.ERROR));
                                            texteditbox.setTextColor(rgb(ERROR));
                                        }
                                    }));
                                    adder.accept(texteditbox);

                                    offset[0] += NORMAL_HEIGHT+6;
//
//                                    for (Component component : cutTextComponent(Component.nullToEmpty(currentError.get()), 0, 180, true)) {
//                                        adder.accept(new LabelWidget(10, offset[0], component.copy().withColor(rgb(ERROR))));
//                                        offset[0] += 10;
//                                    }

                                }

                                static String getJsonParseErrorMessage(String json) {
                                    try {
                                        Component.Serializer.fromJson(json, Minecraft.getInstance().level.registryAccess());
                                        return null; // valid
                                    } catch (Exception e) {
                                        return e.getCause() != null ? e.getCause().getMessage() : e.getMessage(); // or custom friendly message
                                    }
                                }
                            }
                    );
                }));
                YOffset += 26;

                addBaseWidget(new LabelWidget(guiX+10, guiY+YOffset, Component.literal("Text Size").withColor(rgb(SECONDARY)), 0xFFFFFF, true));
                YOffset += 11;
                var sizebox = new NumberEditBox(guiX+10, guiY+YOffset);
                sizebox.setValue(String.valueOf(((BoardElement.Text) element).size));
                sizebox.onSave(() -> {
                    try {
                        ((BoardElement.Text) element).size = Float.parseFloat(sizebox.getValue());
                        updateBEAndRefreshBE();
                    } catch (NumberFormatException ignored) {}
                });
                sizebox.onChange(text -> {
                    try {
                        Float.parseFloat(text);

                        sizebox.setTextColor(rgb(PRIMARY));
                        sizebox.hideFloatingTooltip();
                    } catch (NumberFormatException ignored) {
                        sizebox.setTextColor(rgb(ERROR));
                        sizebox.showFloatingTooltip(new NoticeBoxTooltip(Component.translatable("gui.moretraffic.error_saving"), Component.literal("Incomplete number or not an float number!"), NoticeBoxTooltip.TooltipType.ERROR));
                    }
                });
                addBaseWidget(sizebox);


            }
        }

        int index = 0;
        for (var element : be.elements) {
            int finalIndex = index;
            addBaseWidget(
                    new InLineActionLabel(guiX+97, guiY+40+(finalIndex*13),
                    BoardElement.BoardElementType.byClass(element.getClass()).getComponentOfProperty(),
                    rgb(ColorsManager.PRIMARY),
                    true,
                    be.elements.size() > 1 ? List.of(
                            new AdvancedButton(0, 0, 0, 0, SpritesManager.ICON_TRASHCAN, safeToRenderTooltips() ? new NoticeBoxTooltip(Component.translatable("gui.moretraffic.signboard.element.delete")): null, true, a->{
                                be.elements.remove(element);
                                updateBEAndRefreshBE();
                            }),
                            new AdvancedButton(0, 0, 0, 0, SpritesManager.MOVE, safeToRenderTooltips() ? new NoticeBoxTooltip(Component.translatable("gui.moretraffic.signboard.element.move")) : null, true, a->{
                                if (finalIndex > 0) {
                                    Collections.swap(be.elements, finalIndex, finalIndex - 1);
                                    updateBEAndRefreshBE();
                                }
                            })
                    ) : List.of(),
                    82, () -> {
                        if (focusedIndex.get() == finalIndex) {
                            focusedIndex.set(-1);
                        } else {
                            focusedIndex.set(finalIndex);
                        }
                        queueRefresh = true;
                    }, (finalIndex == focusedIndex.get())
                    )
            );
            index++;
        }

        addBaseWidget(new AdvancedButton(guiX+10, guiY+139, 15, 15, SpritesManager.PLUS, new NoticeBoxTooltip(Component.translatable("gui.moretraffic.signboard.plus")), true, b->{
            int sheetWidth = 150;
            int sheetHeight = 130;

            int sheetX = guiX + (guiWidth - sheetWidth) / 2;
            int sheetY = guiY + (guiHeight - sheetHeight) / 2;

            this.addScreenSheet(
                    new AbstractSheet(sheetX, sheetY, Component.translatable("gui.moretraffic.signboard.plus").getString(), true, sheetWidth, sheetHeight) {
                        @Override
                        public void init(Consumer<AbstractWidget> adder) {
                            int offsetY = 25;

                            for (BoardElement.BoardElementType property : BoardElement.BoardElementType.values()) {
                                if (property.equals(BoardElement.BoardElementType.SPRITE)) continue;
                                adder.accept(new AdvancedButton(10, offsetY, 130, NORMAL_HEIGHT, property.getComponentOfProperty(), new NoticeBoxTooltip(property.getComponentOfProperty()), b -> {
                                    if (be.elements.size() > 6) return;
                                    be.elements.add((BoardElement) property.defaultValue);
                                    updateBEAndRefreshBE();
                                    onClose();
                                }));
                                offsetY += 23;
                            }

                        }
                    }
            );
        }));


    }

    private void updateBEAndRefreshBE() {
        if (be == null || minecraft == null || minecraft.player == null) return;
        assert minecraft.level != null;

        // serialize BE state into NBT
        CompoundTag tag = new CompoundTag();
        be.saveAdditional(tag, minecraft.level.registryAccess());

        // create and send packet
        ClientSyncSignboard packet = new ClientSyncSignboard(be.getBlockPos(), tag);
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
        guiGraphics.pose().translate(guiX -150, guiY +137, 10);

        BlockState model = be.getBlockState().setValue(SignboardBlock.FACING, Direction.SOUTH);

        float scale = 127f;
        guiGraphics.pose().scale(scale, -scale, scale);

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
                ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/signboard_gui.png");

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
