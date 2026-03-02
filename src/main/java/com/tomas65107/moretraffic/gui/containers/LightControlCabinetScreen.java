package com.tomas65107.moretraffic.gui.containers;

import com.tomas65107.moretraffic.block.LightControlCabinetBlockEntity;
import com.tomas65107.moretraffic.data.AbstractSheet;
import com.tomas65107.moretraffic.data.ColorsManager;
import com.tomas65107.moretraffic.data.SpritesManager;
import com.tomas65107.moretraffic.data.TrafficLightGroup;
import com.tomas65107.moretraffic.data.helpers.TextHelper;
import com.tomas65107.moretraffic.data.lightinstructions.AwaitRedstone;
import com.tomas65107.moretraffic.data.lightinstructions.Delay;
import com.tomas65107.moretraffic.data.lightinstructions.LightInstructionProperty;
import com.tomas65107.moretraffic.data.lightinstructions.ModifyLight;
import com.tomas65107.moretraffic.gui.components.BetterEditBox;
import com.tomas65107.moretraffic.gui.components.CustomRenderAsWidget;
import com.tomas65107.moretraffic.gui.components.HelpElementWidget;
import com.tomas65107.moretraffic.gui.components.LabelWidget;
import com.tomas65107.moretraffic.gui.components.buttons.AdvancedButton;
import com.tomas65107.moretraffic.gui.components.buttons.ColorButton;
import com.tomas65107.moretraffic.gui.makers.GridMaker;
import com.tomas65107.moretraffic.gui.tooltip.BodyTooltip;
import com.tomas65107.moretraffic.gui.tooltip.NoticeBoxTooltip;
import com.tomas65107.moretraffic.mod.MoreTraffic;
import com.tomas65107.moretraffic.networking.ClientSyncCabinetPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.tomas65107.moretraffic.data.ColorsManager.*;
import static com.tomas65107.moretraffic.data.SpritesManager.ICON_INFO;
import static com.tomas65107.moretraffic.data.helpers.ColorHelper.rgb;
import static com.tomas65107.moretraffic.data.helpers.TextCutter.cutTextComponent;
import static com.tomas65107.moretraffic.data.helpers.TextHelper.Alignment.CENTER;
import static com.tomas65107.moretraffic.data.helpers.TextHelper.align;
import static com.tomas65107.moretraffic.gui.components.buttons.AdvancedButton.NORMAL_HEIGHT;
import static net.neoforged.neoforge.network.PacketDistributor.sendToServer;

public class LightControlCabinetScreen extends AbstractTomiContainerScreen<LightControlCabinetMenu> {

    private final LightControlCabinetMenu menu;
    private final BlockPos pos;
    private final LightControlCabinetBlockEntity be;

    private int guiX;
    private int guiY;

    protected boolean queueRefresh;
    private int timer = 0;

    int guiWidth = 208;
    int guiHeight = 219;

    private int scrollOffset = 0;

    public LightControlCabinetScreen(LightControlCabinetMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
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
                new LabelWidget(guiX + 10, guiY +10, Component.translatable("gui.moretraffic.control_cabinet.title").withStyle(ChatFormatting.BOLD), 0xFFFFFF, true)
        );

        int currentY = guiY + 30 - scrollOffset;

        addBaseWidget(
                new LabelWidget(guiX+10, guiY + 30, Component.translatable("gui.moretraffic.control_cabinet.instructions").withColor(rgb(SECONDARY)), 0xFFFFFF, true)
        );
        currentY += 11;

        int index = 0;

        if (be.instructions.isEmpty()) {
            int offset = guiY +50;
            for (Component component : cutTextComponent(Component.translatable("gui.moretraffic.control_cabinet.instructions.empty"), true)) {
                addBaseWidget(new LabelWidget(align(CENTER, component, this.width/2), offset, component, rgb(PRIMARY), true));
                offset += 10;
            }

        }

        int viewportTop = guiY + 41;
        int viewportBottom = guiY + 186;

        for (LightInstructionProperty instruction : be.instructions) {
            int finalIndex = index++;
            int finalCurrentY = currentY;

            int spriteY = (be.isRunning && be.programStep == finalIndex ? 101 : 1) + 23 *
                    switch (instruction) {
                        case Delay d -> 0;
                        case ModifyLight m -> 1;
                        case AwaitRedstone a -> 2;
                    };

            addBaseWidget(new CustomRenderAsWidget(gfx -> {
                // clip only inside instruction viewport
                gfx.enableScissor(guiX + 10, viewportTop, guiX + 2000, viewportBottom);

                // skip if outside viewport
                if (finalCurrentY + 22 < viewportTop || finalCurrentY > viewportBottom) return;

                gfx.blit(
                        ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/control_cabinet_sprites.png"),
                        guiX + 10, finalCurrentY, 2, spriteY, 188, 22, 256, 256
                );
                TextHelper.renderText(gfx, guiX+25, finalCurrentY+7, instruction.getClassType().getComponentOfProperty(false));
            }));
            addBaseWidget(new AdvancedButton(guiX + 183, currentY + 6, 9, 9, SpritesManager.ICON_TRASHCAN,
                    new NoticeBoxTooltip(Component.translatable("gui.moretraffic.control_cabinet.instruction.delete")), true, b->{
                if (be.isRunning) return;
                be.instructions.remove(finalIndex);
                updateBEAndRefreshBE();
            }));

            if (instruction instanceof Delay delay) {
                BetterEditBox textField = new BetterEditBox( guiX + 139, currentY + 7, 30, 14);

                textField.onSave(() -> {
                    String text = textField.getValue();
                    // remove any character except digits and dot
                    String filtered = text.replaceAll("[^0-9.]", "");

                    // allow only one dot
                    int dotIndex = filtered.indexOf('.');
                    if (dotIndex != -1) {
                        filtered = filtered.substring(0, dotIndex + 1) + filtered.substring(dotIndex + 1).replace(".", "");
                    }

                    if (!filtered.isEmpty() && !filtered.equals(".")) {
                        try {
                            float seconds = Float.parseFloat(filtered);
                            be.instructions.set(finalIndex, new Delay((int)(seconds * 20)));
                            updateBEAndRefreshBE();
                        } catch (NumberFormatException ignore) {}
                    }
                });

                textField.setBordered(false);
                textField.setValue(String.valueOf(delay.delayInTicks() / 20f));
                textField.setTextColor(rgb(PRIMARY));

                textField.onChange(text -> {
                    String filtered = text.replaceAll("[^0-9.]", "");

                    // allow only one dot
                    int dotIndex = filtered.indexOf('.');
                    if (dotIndex != -1) {
                        filtered = filtered.substring(0, dotIndex + 1) + filtered.substring(dotIndex + 1).replace(".", "");
                    }
                    if (!filtered.equals(text)) {
                        textField.setValue(filtered);
                    }
                });

                textField.active = !be.isRunning;
                addBaseWidget(textField);

            } else if (instruction instanceof ModifyLight modifyLight) {
                addBaseWidget(new ColorButton(guiX + 134, currentY + 4, 14, 14, modifyLight.light0().getTextureDiffuseColor(), b -> {
                    displaySheet(finalIndex, modifyLight, 0);
                }, false, true));

                addBaseWidget(new ColorButton(guiX + 149, currentY + 4, 14, 14, modifyLight.light1().getTextureDiffuseColor(), b -> {
                    displaySheet(finalIndex, modifyLight, 1);
                }, false, true));

                addBaseWidget(new ColorButton(guiX + 164, currentY + 4, 14, 14, modifyLight.light2().getTextureDiffuseColor(), b -> {
                    displaySheet(finalIndex, modifyLight, 2);
                }, false, true));

                BetterEditBox textField = new BetterEditBox(guiX + 89, currentY + 7, 40, 14);
                textField.setBordered(false);
                textField.setValue(modifyLight.group());
                textField.setTextColor(rgb(PRIMARY));

                textField.onSave(() -> {
                    if (be.groups.stream().noneMatch(g -> g.name.equals(textField.getValue()))) return;
                    be.instructions.set(finalIndex, new ModifyLight(textField.getValue(), modifyLight.light0(), modifyLight.light1(), modifyLight.light2()));
                    updateBEAndRefreshBE();
                });

                textField.onChange(text -> {
                    if (be.groups.stream().noneMatch(g -> g.name.equals(text))) {
                        textField.setTextColor(rgb(INVALID));
                        textField.showFloatingTooltip(new NoticeBoxTooltip(Component.literal("Group named '" + text + "' does not exist"), INVALID));
                    } else {
                        textField.setTextColor(rgb(PRIMARY));
                        textField.hideFloatingTooltip();
                    }
                });

                textField.active = !be.isRunning;
                addBaseWidget(textField);

            }

            currentY += 24;
            addBaseWidget(new CustomRenderAsWidget(gfx -> {
                gfx.disableScissor();

                double mouseX = Minecraft.getInstance().mouseHandler.xpos() * this.width / Minecraft.getInstance().getWindow().getScreenWidth();
                double mouseY = Minecraft.getInstance().mouseHandler.ypos() * this.height / Minecraft.getInstance().getWindow().getScreenHeight();
                int textWidth = Minecraft.getInstance().font.width(instruction.getClassType().getComponentOfProperty(false).getString());

                if (mouseX >= guiX + 13 &&
                            mouseX <= guiX + 25 + textWidth + 2 && mouseY >= finalCurrentY + 7 && mouseY <= finalCurrentY + 7 + Minecraft.getInstance().font.lineHeight && shouldRenderTooltips()) {
                    gfx.renderTooltip(
                            Minecraft.getInstance().font, List.of(Component.empty()),
                            Optional.of(new NoticeBoxTooltip(instruction.getClassType().getComponentOfProperty(false), instruction.getClassType().getComponentOfProperty(true), Component.literal("instructionIndex: " + finalIndex))),
                            ItemStack.EMPTY, (int) mouseX, (int) mouseY
                    );
                }
            }));
        }

        addBaseWidget(new AdvancedButton(guiX+10, guiY+194, 15, 15, SpritesManager.ICON_IMPORT, new NoticeBoxTooltip(Component.translatable("gui.moretraffic.control_cabinet.import")), true, b->{

        }));
        addBaseWidget(new AdvancedButton(guiX+26, guiY+194, 15, 15, SpritesManager.ICON_EXPORT, new NoticeBoxTooltip(Component.translatable("gui.moretraffic.control_cabinet.export")), true, b->{

        }));
        addBaseWidget(new AdvancedButton(guiX+51, guiY+194, 15, 15, be.shouldLoop ? SpritesManager.ICON_REPEAT : SpritesManager.ICON_ONETIME, new NoticeBoxTooltip(Component.translatable("gui.moretraffic.control_cabinet.repeat"), Component.translatable("gui.moretraffic.control_cabinet.repeat.message."+(be.shouldLoop ? "yes":"no")), Component.translatable("gui.moretraffic.change")), true, b->{
            be.shouldLoop = !be.shouldLoop;
            updateBEAndRefreshBE();
        }));
        addBaseWidget(new AdvancedButton(guiX+76, guiY+194, 15, 15, SpritesManager.ICON_GROUP, new NoticeBoxTooltip(Component.translatable("gui.moretraffic.control_cabinet.groups")), true, b->{
            int sheetWidth = 410;
            int sheetHeight = 200;

            int sheetX = guiX + (guiWidth - sheetWidth) / 2;
            int sheetY = guiY + (guiHeight - sheetHeight) / 2;

            this.addElement(
                    new AbstractSheet(sheetX, sheetY, Component.translatable("gui.moretraffic.control_cabinet.groups").getString(), true, sheetWidth, sheetHeight) {

                        @Override
                        public void init(Consumer<AbstractWidget> adder) {
                            BlockPos clipboardBlockPos;

                            if (verifyAndParseCoordinates(Minecraft.getInstance().keyboardHandler.getClipboard()) instanceof BlockPos blockPosClip) {
                                clipboardBlockPos = blockPosClip;
                            } else {
                                clipboardBlockPos = null;
                            }

                            NoticeBoxTooltip tooltip1 = new NoticeBoxTooltip(
                                    Component.translatable("gui.moretraffic.control_cabinet.groups.title"),
                                    Component.translatable("gui.moretraffic.control_cabinet.groups.message"),
                                    Component.translatable("gui.moretraffic.control_cabinet.groups.cta"), ColorsManager.HEADER, false);
                            int finalWidthOfPrevContent = Minecraft.getInstance().font.width(Component.translatable("gui.moretraffic.control_cabinet.groups").withStyle(ChatFormatting.BOLD));
                            adder.accept(new HelpElementWidget(finalWidthOfPrevContent + 10, 5, ICON_INFO, tooltip1));

                            adder.accept(new LabelWidget(10, 25, Component.translatable("gui.moretraffic.control_cabinet.groups.group_name"), rgb(SECONDARY), true));
                            adder.accept(new LabelWidget(77, 25, Component.translatable("gui.moretraffic.control_cabinet.groups.group_pos"), rgb(SECONDARY), true));

                            int offsetY = 25 + 11;

                            int index = 0;
                            for (TrafficLightGroup group : be.groups) {
                                int finalIndex = index;
                                BetterEditBox textField = new BetterEditBox(10, offsetY, 60, NORMAL_HEIGHT, Component.translatable("gui.moretraffic.control_cabinet.groups.group_name"));

                                textField.setValue(group.name);
                                textField.setTextColor(rgb(PRIMARY));

                                textField.onSave(() -> {
                                            if (be.groups.stream()
                                                    .filter(g -> g != be.groups.get(finalIndex))
                                                    .anyMatch(g -> g.name.equals(textField.getValue()))) return;
                                    be.groups.get(finalIndex).name = textField.getValue();
                                    updateBEAndRefreshBE();
                                });

                                textField.onChange(text -> {
                                    if (text.length() > 17) textField.setValue(text.substring(0, text.length() - 1));

                                    if (be.groups.stream().filter(g -> g != be.groups.get(finalIndex)).noneMatch(g -> g.name.equals(text))) {
                                        be.groups.get(finalIndex).name = text;
                                    }

                                    if (be.groups.stream()
                                            .filter(g -> g != be.groups.get(finalIndex))
                                            .anyMatch(g -> g.name.equals(text))) {
                                        textField.setTextColor(rgb(INVALID));
                                        textField.showFloatingTooltip(new NoticeBoxTooltip(Component.literal("Group named '"+text+"' already exist"), INVALID));
                                    } else {
                                        textField.setTextColor(rgb(PRIMARY)); textField.hideFloatingTooltip();
                                    }
                                });
                                adder.accept(textField);

                                int offsetX = 77;
                                int lightPosIndex = 0;
                                for (BlockPos pos : be.groups.get(finalIndex).lightsPositions) {
                                    BetterEditBox posEditBox = new BetterEditBox(offsetX, offsetY, 40, NORMAL_HEIGHT);

                                    posEditBox.onChange((text) -> {
                                        if (verifyAndParseCoordinates(text) instanceof String s) {
                                            posEditBox.setTextColor(rgb(INVALID));
                                            posEditBox.showFloatingTooltip(new NoticeBoxTooltip(Component.literal(s), INVALID));
                                        } else {
                                            posEditBox.hideFloatingTooltip();
                                            posEditBox.setTextColor(rgb(PRIMARY));
                                        }
                                    });

                                    int finalLightPosIndex = lightPosIndex;
                                    posEditBox.onSave(() -> {
                                        MoreTraffic.LOGGER.debug("triggered onsave, value of parse: "+ verifyAndParseCoordinates(posEditBox.getValue()));
                                        if (verifyAndParseCoordinates(posEditBox.getValue()) instanceof BlockPos blockPos) {
                                            be.groups.get(finalIndex).lightsPositions.set(finalLightPosIndex, blockPos);
                                            updateBEAndRefreshBE();
                                        }
                                    });

                                    posEditBox.setValue(pos.toShortString());

                                    adder.accept(posEditBox);

                                    offsetX += 40;

                                    adder.accept(new AdvancedButton(offsetX, offsetY, NORMAL_HEIGHT, NORMAL_HEIGHT, SpritesManager.ICON_TRASHCAN, null, false, b->{be.groups.get(finalIndex).lightsPositions.remove(finalLightPosIndex); updateBEAndRefreshBE();}));

                                    offsetX += 23;
                                    lightPosIndex++;
                                }
                                adder.accept(new AdvancedButton(offsetX, offsetY, NORMAL_HEIGHT, NORMAL_HEIGHT, SpritesManager.ICON_ADD_POSITION, new NoticeBoxTooltip(Component.translatable("gui.moretraffic.control_cabinet.groups.add_light_pos"), null, clipboardBlockPos != null ? Component.translatable("gui.moretraffic.control_cabinet.groups.add_light_pos.clipboard_prompt") : null, PRIMARY, true), false, b->{be.groups.get(finalIndex).lightsPositions.add(new BlockPos(0, 0, 0)); updateBEAndRefreshBE();}) {
                                    @Override
                                    public boolean mouseClicked(double mouseX, double mouseY, int button) {
                                        if (!this.active || !this.visible || !this.isMouseOver(mouseX, mouseY) || button != 1 || clipboardBlockPos == null) return super.mouseClicked(mouseX, mouseY, button);
                                        be.groups.get(finalIndex).lightsPositions.add(clipboardBlockPos); updateBEAndRefreshBE();
                                        return true;
                                    }
                                } );

                                adder.accept(new AdvancedButton(sheetWidth - 30, offsetY, NORMAL_HEIGHT, NORMAL_HEIGHT, SpritesManager.ICON_TRASHCAN , null, false, b->{be.groups.remove(finalIndex); updateBEAndRefreshBE();}));

                                offsetY += 23;
                                index++;
                            }
                            adder.accept(new AdvancedButton(10, sheetHeight - 30, 100, NORMAL_HEIGHT, Component.translatable("gui.moretraffic.control_cabinet.groups.create_group"), SpritesManager.ICON_PLUS,b->{be.groups.add(new TrafficLightGroup("", new ArrayList<>())); updateBEAndRefreshBE();}));

                        }
                    }
            );
        }));

        addBaseWidget(new AdvancedButton(guiX+76+25, guiY+194, 15, 15, SpritesManager.ICON_PLUS, new NoticeBoxTooltip(Component.translatable("gui.moretraffic.control_cabinet.plus")), true, b->{
            int sheetWidth = 150;
            int sheetHeight = 130;

            int sheetX = guiX + (guiWidth - sheetWidth) / 2;
            int sheetY = guiY + (guiHeight - sheetHeight) / 2;

            this.addElement(
                    new AbstractSheet(sheetX, sheetY, Component.translatable("gui.moretraffic.control_cabinet.plus").getString(), true, sheetWidth, sheetHeight) {
                        @Override
                        public void init(Consumer<AbstractWidget> adder) {
                            int offsetY = 25;

                            for (LightInstructionProperty.PropertyTypes property : LightInstructionProperty.PropertyTypes.values()) {
                                adder.accept(new AdvancedButton(10, offsetY, 130, NORMAL_HEIGHT, property.getComponentOfProperty(false), new NoticeBoxTooltip(property.getComponentOfProperty(false), property.getComponentOfProperty(true)), b -> {
                                    be.instructions.add(property.create());
                                    updateBEAndRefreshBE();
                                    onClose();
                                }));
                                offsetY += 23;
                            }

                        }
                    }
            );
        }));


        addBaseWidget(new AdvancedButton(guiX+167, guiY+194, 15, 15, be.isRunning ? SpritesManager.ICON_PLAY_ENGAGED : SpritesManager.ICON_PLAY, new NoticeBoxTooltip(Component.translatable("gui.moretraffic.control_cabinet.play"), null, Component.translatable("gui.moretraffic.change")), true, b->{
            if (be.isRunning) {
                be.isRunning = false;
            } else {
                be.ticksSinceStart = 0; be.lastInstructionFinishedTick = 0; be.programStep = 0;
                be.isRunning = true;
            }
            updateBEAndRefreshBE();
        }));

        addBaseWidget(new LabelWidget(guiX+183, guiY+194 + 5, Component.literal(be.ticksSinceStart/20+"s"), be.isRunning ? rgb(SELECTED) : rgb(PRIMARY), true, 0.5f));
        addBaseWidget(new CustomRenderAsWidget(gfx -> {
            double mouseX = Minecraft.getInstance().mouseHandler.xpos() * this.width / Minecraft.getInstance().getWindow().getScreenWidth();
            double mouseY = Minecraft.getInstance().mouseHandler.ypos() * this.height / Minecraft.getInstance().getWindow().getScreenHeight();

            if (mouseX >= guiX + 182 && mouseX <=  guiX + 195 &&
                mouseY >= guiY + 191 + 5 && mouseY <= guiY + 191 + 17 &&
                shouldRenderTooltips()) {
                gfx.renderTooltip(
                        Minecraft.getInstance().font, List.of(Component.empty()),
                        Optional.of(new BodyTooltip((c)-> {
                            c.xTotal = 130;
                            c.yTotal = 20;

                            if (c.guiGraphics instanceof GuiGraphics g) {
                                TextHelper.renderText(g, c.x+3, c.y+-10, Component.literal("isRunning: " + be.isRunning), 0.77f, be.isRunning ? rgb(SELECTED): rgb(SECONDARY), true);
                                TextHelper.renderText(g, c.x+3, c.y, Component.literal("ticksSinceStart: " + be.ticksSinceStart), 0.77f, rgb(SECONDARY), true);
                                TextHelper.renderText(g, c.x+3, c.y+10, Component.literal("lastInstructionFinishedTick: " + be.lastInstructionFinishedTick), 0.77f, rgb(SECONDARY), true);
                            }
                        })),
                        ItemStack.EMPTY, (int) mouseX, (int) mouseY
                );
            }
        }));
    }


    public Object verifyAndParseCoordinates(String str) {
        String[] parts = str.trim().split("\\s*,\\s*");
        if (parts.length != 3) {
            MoreTraffic.LOGGER.debug("FAIL," + str);
            return "Must contain 3 coordinates";
        }

        try {
            if (BlockPos.getX(new BlockPos(Integer.parseInt(parts[0].trim()), 0, 0).asLong()) != Integer.parseInt(parts[0].trim())) return "Invalid X coordinate";
            if (BlockPos.getY(new BlockPos(0, Integer.parseInt(parts[1].trim()), 0).asLong()) != Integer.parseInt(parts[1].trim())) return "Invalid Y coordinate";
            if (BlockPos.getZ(new BlockPos(0, 0, Integer.parseInt(parts[2].trim())).asLong()) != Integer.parseInt(parts[2].trim())) return "Invalid Z coordinate";

            return new BlockPos(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()), Integer.parseInt(parts[2].trim()));

        } catch (NumberFormatException e) {
            return "Not valid integers";
        }
    }

    private void displaySheet(int index, ModifyLight modifyLight, int indexOfLight) {

        int sheetWidth = 173;
        int sheetHeight = 98;

        int sheetX = guiX + (guiWidth - sheetWidth) / 2;
        int sheetY = guiY + (guiHeight - sheetHeight) / 2;

        addElement(new AbstractSheet(sheetX, sheetY, Component.translatable("gui.moretraffic.control_cabinet.instruction.delay.select_color").getString(), true, sheetWidth, sheetHeight) {
            @Override
            public void init(Consumer<AbstractWidget> adder) {
                adder.accept(new LabelWidget(10, 20, Component.translatable("core.moretraffic.advanced_traffic_light.light"+(indexOfLight+1)), rgb(PRIMARY), true));
                switch (indexOfLight) {
                    case 0 -> new GridMaker(10, 33, adder, c -> {be.instructions.set(index, new ModifyLight(modifyLight.group(), c, modifyLight.light1(), modifyLight.light2()));updateBEAndRefreshBE();onClose();}, modifyLight.light0());
                    case 1 -> new GridMaker(10, 33, adder, c -> {be.instructions.set(index, new ModifyLight(modifyLight.group(), modifyLight.light0(), c, modifyLight.light2()));updateBEAndRefreshBE();onClose();}, modifyLight.light1());
                    case 2 -> new GridMaker(10, 33, adder, c -> {be.instructions.set(index, new ModifyLight(modifyLight.group(), modifyLight.light0(), modifyLight.light1(), c));updateBEAndRefreshBE();onClose();}, modifyLight.light2());
                }
            }
        });

    }

    private void updateBEAndRefreshBE() {
        if (be == null || minecraft == null || minecraft.player == null) return;
        assert minecraft.level != null;

        // serialize BE state into NBT
        CompoundTag tag = new CompoundTag();
        be.saveAdditional(tag, minecraft.level.registryAccess());

        // create and send packet
        ClientSyncCabinetPacket packet = new ClientSyncCabinetPacket(be.getBlockPos(), tag);
        sendToServer(packet);

        //queue update on ui
        queueRefresh = true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (be.instructions.size() < 6) return false;
        int scrollSpeed = 10;

        scrollOffset -= scrollY * scrollSpeed;
        scrollOffset = Math.max(0, Math.min(scrollOffset, ((be.instructions.size() - 6)*26)+10));

        refreshContent();

        return true;
    }

    @Override
    protected void containerTick() {

        if (be.isRunning) {
            if (!shouldRenderTooltips()) {onClose();}
            refreshContent();
            timer = 2;
        } else

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
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {

        ResourceLocation BG_TEXTURE =
                ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/control_cabinet_gui.png");

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
        // do not render inventory text
    }
}
