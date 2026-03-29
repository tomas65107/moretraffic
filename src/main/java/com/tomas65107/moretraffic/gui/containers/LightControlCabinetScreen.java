package com.tomas65107.moretraffic.gui.containers;

import com.tomas65107.moretraffic.block.LightControlCabinetBlockEntity;
import com.tomas65107.moretraffic.data.*;
import com.tomas65107.moretraffic.data.helpers.MaskConverter;
import com.tomas65107.moretraffic.data.helpers.TextHelper;
import com.tomas65107.moretraffic.data.lightinstructions.*;
import com.tomas65107.moretraffic.gui.components.BetterEditBox;
import com.tomas65107.moretraffic.gui.components.CustomRenderAsWidget;
import com.tomas65107.moretraffic.gui.components.HelpElementWidget;
import com.tomas65107.moretraffic.gui.components.LabelWidget;
import com.tomas65107.moretraffic.gui.components.buttons.AdvancedButton;
import com.tomas65107.moretraffic.gui.components.buttons.ColorButton;
import com.tomas65107.moretraffic.gui.makers.GridMaker;
import com.tomas65107.moretraffic.gui.makers.PixelGridMaker;
import com.tomas65107.moretraffic.gui.tooltip.BodyTooltip;
import com.tomas65107.moretraffic.gui.tooltip.NoticeBoxTooltip;
import com.tomas65107.moretraffic.mod.MoreTraffic;
import com.tomas65107.moretraffic.networking.ClientSyncCabinetPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.tomas65107.moretraffic.data.ColorsManager.*;
import static com.tomas65107.moretraffic.data.SpritesManager.ICON_INFO;
import static com.tomas65107.moretraffic.data.helpers.ColorHelper.rgb;
import static com.tomas65107.moretraffic.data.helpers.TextCutter.cutTextComponent;
import static com.tomas65107.moretraffic.data.helpers.TextHelper.Alignment.CENTER;
import static com.tomas65107.moretraffic.data.helpers.TextHelper.align;
import static com.tomas65107.moretraffic.gui.components.buttons.AdvancedButton.NORMAL_HEIGHT;
import static com.tomas65107.moretraffic.networking.ClientSenderPacketTrafficLight.shortsToBytes;
import static net.neoforged.neoforge.network.PacketDistributor.sendToServer;

public class LightControlCabinetScreen extends AbstractTomiContainerScreen<LightControlCabinetMenu> {

    private final LightControlCabinetMenu menu;
    private final BlockPos pos;
    private final LightControlCabinetBlockEntity be;

    private int guiX;
    private int guiY;

    protected boolean queueRefresh;
    private int timer = 0;

    private DyeColor colorPicker = (DyeColor.BLACK);

    int guiWidth = 208;
    int guiHeight = 249;

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

        int currentY = guiY + 41; // initial Y for first instruction (after header)

        addBaseWidget(
                new LabelWidget(guiX+10, guiY + 30, Component.translatable("gui.moretraffic.control_cabinet.instructions").withColor(rgb(SECONDARY)), 0xFFFFFF, true)
        );
        // currentY for instructions is handled below

        int index = 0;

        if (be.instructions.isEmpty()) {
            int offset = guiY +50;
            for (Component component : cutTextComponent(Component.translatable("gui.moretraffic.control_cabinet.instructions.empty"), true)) {
                addBaseWidget(new LabelWidget(align(CENTER, component, this.width/2), offset, component, rgb(PRIMARY), true));
                offset += 10;
            }
        }

        int viewportTop = guiY + 41;
        int viewportBottom = guiY + 216;
        int instructionHeight = 22;

        // Scroll-compatible instruction rendering
        for (LightInstructionProperty instruction : be.instructions) {
            int finalIndex = index++;
            int renderY = currentY - scrollOffset;

            int spriteY = (be.isRunning && be.programStep == finalIndex ? 124 : 1) + 23 *
                    switch (instruction) {
                        case Delay d -> 0;
                        case ModifyLight m -> 1;
                        case AwaitRedstone a -> 2;
                        case ModifyDisplay m -> 3;
                        case SendPulse s -> 4;
                    };

            // Only render if in viewport
            if (renderY + instructionHeight < viewportTop || renderY > viewportBottom) {
                currentY += 24;
                continue;
            }

            // Scissor must not underflow: clamp right/bottom to guiX+guiWidth and guiY+guiHeight
            int scissorLeft = guiX + 10;
            int scissorRight = Math.min(guiX + guiWidth - 10, guiX + 2000);

            addBaseWidget(new CustomRenderAsWidget(gfx -> {
                gfx.enableScissor(scissorLeft, viewportTop, scissorRight+300, viewportBottom);
                gfx.blit(
                        ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/gui/control_cabinet_sprites.png"),
                        guiX + 10, renderY, 2, spriteY, 188, 22, 256, 256
                );
                TextHelper.renderText(gfx, guiX+25, renderY+7, instruction.getClassType().getComponentOfProperty(false));
            }));

            if (instruction instanceof Delay delay) {
                BetterEditBox textField = new BetterEditBox(guiX + 139, renderY + 7, 30, 14);

                textField.onSave(() -> {
                    String text = textField.getValue();

                    String filtered = text.replaceAll("[^0-9.]", "");

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
                addBaseWidget(new ColorButton(guiX + 134, renderY + 4, 14, 14, modifyLight.light0().getTextureDiffuseColor(), b -> {
                    displaySheet(finalIndex, modifyLight, 0);
                }, false, true));
                addBaseWidget(new ColorButton(guiX + 149, renderY + 4, 14, 14, modifyLight.light1().getTextureDiffuseColor(), b -> {
                    displaySheet(finalIndex, modifyLight, 1);
                }, false, true));
                addBaseWidget(new ColorButton(guiX + 164, renderY + 4, 14, 14, modifyLight.light2().getTextureDiffuseColor(), b -> {
                    displaySheet(finalIndex, modifyLight, 2);
                }, false, true));
                BetterEditBox textField = new BetterEditBox(guiX + 89, renderY + 7, 40, 14);
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
            } else if (instruction instanceof ModifyDisplay modifyDisplay) {
                addBaseWidget(new AdvancedButton(guiX + 164, renderY + 4, 14, 14, SpritesManager.EDIT_DISPLAY, new NoticeBoxTooltip(Component.translatable("gui.moretraffic.control_cabinet.instruction.modify_display.modify")), true, b->{
                    int sheetWidth = 250;
                    int sheetHeight = 200;
                    int sheetX = guiX + (guiWidth - sheetWidth) / 2;
                    int sheetY = guiY + (guiHeight - sheetHeight) / 2;
                    this.addElement(
                            new AbstractSheet(sheetX, sheetY, Component.translatable("gui.moretraffic.control_cabinet.instruction.modify_display.modify").getString(), true, sheetWidth, sheetHeight) {
                                @Override
                                public void init(Consumer<AbstractWidget> adder) {

                                    new GridMaker(10, 25, adder, c -> {
                                        colorPicker = c; refreshContent();
                                    }, colorPicker, true);

                                    new PixelGridMaker(
                                            10, 60,
                                            ((ModifyDisplay) be.instructions.get(finalIndex)).trafficDisplayPixels(),
                                            adder,
                                            newPixels -> {
                                                be.instructions.set(finalIndex, new ModifyDisplay(modifyDisplay.group(), newPixels));
                                                updateBEAndRefreshBE(); timer = 1;
                                            },
                                            colorPicker
                                    );


                                    NoticeBoxTooltip tooltip3 = new NoticeBoxTooltip(
                                            Component.translatable("gui.moretraffic.advanced_traffic_light.options.change_mask.controls"),
                                            Component.translatable("gui.moretraffic.advanced_traffic_light.options.change_mask.controls.message"),
                                            null, ColorsManager.HEADER, false);
                                    adder.accept(new HelpElementWidget(148, 70, ICON_INFO, tooltip3));


                                    adder.accept(
                                            new AdvancedButton(150, 90, 70, NORMAL_HEIGHT, Component.translatable("gui.moretraffic.advanced_traffic_light.options.change_mask.clear"), SpritesManager.ICON_CLEAR, a-> {
                                                be.instructions.set(finalIndex, new ModifyDisplay(modifyDisplay.group(), new TrafficDisplayPixels()));
                                                updateBEAndRefreshBE(); timer = 1;
                                            })
                                    );

                                    adder.accept(
                                            new AdvancedButton(150, 90+28, NORMAL_HEIGHT, NORMAL_HEIGHT, null, SpritesManager.ICON_EXPORT, new NoticeBoxTooltip(Component.translatable("gui.moretraffic.control_cabinet.instruction.modify_display.export"), Component.translatable("gui.moretraffic.control_cabinet.instruction.modify_display.export.message"), null), a->{
                                                updateBEAndRefreshBE(); timer = 1;
                                                Minecraft.getInstance().keyboardHandler.setClipboard(((ModifyDisplay) be.instructions.get(finalIndex)).trafficDisplayPixels().serialize());
                                                refreshContent();
                                            })
                                    );
                                    adder.accept(new AdvancedButton(150+28, 90+28, NORMAL_HEIGHT, NORMAL_HEIGHT, null, SpritesManager.ICON_IMPORT, new NoticeBoxTooltip(Component.translatable("gui.moretraffic.control_cabinet.instruction.modify_display.import"), Component.translatable("gui.moretraffic.control_cabinet.instruction.modify_display.import.message"), null), a->{
                                                try {
                                                    be.instructions.set(finalIndex, new ModifyDisplay(modifyDisplay.group(), TrafficDisplayPixels.deserialize(Minecraft.getInstance().keyboardHandler.getClipboard())));
                                                    updateBEAndRefreshBE(); timer = 1;
                                                } catch (Exception ex) {
                                                    a.setTooltip(Tooltip.create(Component.translatable("gui.moretraffic.import_invalid_data")));
                                                }
                                            })
                                    );

                                }
                            }
                    );
                }));
                BetterEditBox textField = new BetterEditBox(guiX + 120, renderY + 7, 40, 14);
                textField.setBordered(false);
                textField.setValue(modifyDisplay.group());
                textField.setTextColor(rgb(PRIMARY));
                textField.onSave(() -> {
                    if (be.groups.stream().noneMatch(g -> g.name.equals(textField.getValue()))) return;
                    be.instructions.set(finalIndex, new ModifyDisplay(textField.getValue(), modifyDisplay.trafficDisplayPixels()));
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
            } else if (instruction instanceof SendPulse(String group, boolean enable)) {
                addBaseWidget(Checkbox.builder(Component.literal(""), Minecraft.getInstance().font).pos(guiX + 164, renderY + 4) .selected(enable) .onValueChange((b, a)-> {
                    be.instructions.set(finalIndex, new SendPulse(group, a)); updateBEAndRefreshBE();
                }) .build());

                BetterEditBox textField = new BetterEditBox(guiX + 120, renderY + 7, 40, 14);
                textField.setBordered(false);
                textField.setValue(group);
                textField.setTextColor(rgb(PRIMARY));
                textField.onSave(() -> {
                    if (be.groups.stream().noneMatch(g -> g.name.equals(textField.getValue()))) return;
                    be.instructions.set(finalIndex, new SendPulse(textField.getValue(), enable));
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

            addBaseWidget(new AdvancedButton(guiX + 183, renderY + 6, 9, 9, SpritesManager.ICON_TRASHCAN,
                    new NoticeBoxTooltip(Component.translatable("gui.moretraffic.control_cabinet.instruction.delete")), true, b->{
                if (be.isRunning) return;
                be.instructions.remove(finalIndex);
                updateBEAndRefreshBE();
            }));

            addBaseWidget(
                    new AdvancedButton(guiX+201, renderY, NORMAL_HEIGHT, NORMAL_HEIGHT, Component.literal("↑"), null, null, b -> {
                        if (finalIndex > 0) {
                            Collections.swap(be.instructions, finalIndex, finalIndex - 1);
                            updateBEAndRefreshBE();
                        }})
            );

            addBaseWidget(new CustomRenderAsWidget(gfx -> {
                gfx.disableScissor();
                double mouseX = Minecraft.getInstance().mouseHandler.xpos() * this.width / Minecraft.getInstance().getWindow().getScreenWidth();
                double mouseY = Minecraft.getInstance().mouseHandler.ypos() * this.height / Minecraft.getInstance().getWindow().getScreenHeight();
                int textWidth = Minecraft.getInstance().font.width(instruction.getClassType().getComponentOfProperty(false).getString());
                if (mouseX >= guiX + 13 &&
                            mouseX <= guiX + 25 + textWidth + 2 && mouseY >= renderY + 7 && mouseY <= renderY + 7 + Minecraft.getInstance().font.lineHeight && shouldRenderTooltips()) {
                    gfx.renderTooltip(
                            Minecraft.getInstance().font, List.of(Component.empty()),
                            Optional.of(new NoticeBoxTooltip(instruction.getClassType().getComponentOfProperty(false), instruction.getClassType().getComponentOfProperty(true), Component.literal("id: " + instruction.getClassType().getNameOfProperty() + "  instructionIndex: " + finalIndex))),
                            ItemStack.EMPTY, (int) mouseX, (int) mouseY
                    );
                }
            }));

            currentY += 24;
        }

        addBaseWidget(new AdvancedButton(guiX+10, guiY+224, 15, 15, SpritesManager.ICON_IMPORT, new NoticeBoxTooltip(Component.translatable("gui.moretraffic.control_cabinet.import")), true, b->{

            int sheetWidth = 180;
            int sheetHeight = 90;

            int sheetX = guiX + (guiWidth - sheetWidth) / 2;
            int sheetY = guiY + (guiHeight - sheetHeight) / 2;

            this.addElement(
                    new AbstractSheet(sheetX, sheetY, Component.translatable("gui.moretraffic.control_cabinet.import").getString(), true, sheetWidth, sheetHeight) {

                        @Override
                        public void init(Consumer<AbstractWidget> adder) {

                            String clipboard = Minecraft.getInstance().keyboardHandler.getClipboard();

                            boolean validClipboardContents = false;
                            CompoundTag tagToSave = new CompoundTag();

                            if (!clipboard.isBlank()) {
                                try {

                                    CompoundTag parsed = TagParser.parseTag(clipboard);

                                    be.saveAdditional(tagToSave, Minecraft.getInstance().level.registryAccess());

                                    if (parsed.contains("Instructions")) {
                                        tagToSave.put("Instructions", parsed.get("Instructions").copy());
                                    }

                                    if (parsed.contains("Groups")) {
                                        tagToSave.put("Groups", parsed.get("Groups").copy());
                                    }

                                    //everything finished without throwing
                                    validClipboardContents = true;

                                } catch (Exception e) {
                                    validClipboardContents = false;
                                }
                            }

                            var button = new AdvancedButton(10, 25, 160, NORMAL_HEIGHT, Component.translatable("gui.moretraffic.control_cabinet.import.from_clipboard"), null, null, b -> {
                                if (!tagToSave.isEmpty()) {
                                    be.loadAdditional(tagToSave, Minecraft.getInstance().level.registryAccess());
                                    updateBEAndRefreshBE();
                                    onClose();
                                }
                            });

                            if (validClipboardContents && (!tagToSave.isEmpty())) {
                                adder.accept(button);
                            } else {
                                int offset = 23;
                                for (Component component : cutTextComponent(Component.translatable("gui.moretraffic.import_invalid_data"), 0, 170, true)) {
                                    adder.accept(new LabelWidget(align(CENTER, component, sheetWidth/2), offset, component, rgb(INVALID), true));
                                    offset += 10;
                                }
                            }
                        }
                    });

        }));
        addBaseWidget(new AdvancedButton(guiX+26, guiY+224, 15, 15, SpritesManager.ICON_EXPORT, new NoticeBoxTooltip(Component.translatable("gui.moretraffic.control_cabinet.export")), true, b->{

            int sheetWidth = 180;
            int sheetHeight = 130;

            int sheetX = guiX + (guiWidth - sheetWidth) / 2;
            int sheetY = guiY + (guiHeight - sheetHeight) / 2;

            this.addElement(
                    new AbstractSheet(sheetX, sheetY, Component.translatable("gui.moretraffic.control_cabinet.export").getString(), true, sheetWidth, sheetHeight) {

                        Checkbox includeInstructions;
                        Checkbox includeGroups;
                        AdvancedButton button;

                        @Override
                        public void init(Consumer<AbstractWidget> adder) {

                            adder.accept(new LabelWidget(10, 25, Component.translatable("gui.moretraffic.control_cabinet.export.include"), rgb(SECONDARY), true));

                            includeInstructions = Checkbox.builder(Component.translatable("gui.moretraffic.control_cabinet.export.include_instruction"), Minecraft.getInstance().font).pos(10, 36) .selected(false) .onValueChange((b, a)-> updateButton()) .build();
                            includeGroups = Checkbox.builder(Component.translatable("gui.moretraffic.control_cabinet.export.include_groups"), Minecraft.getInstance().font).pos(10, 54) .selected(false) .onValueChange((b, a)-> updateButton()) .build();
                            adder.accept(includeInstructions);
                            adder.accept(includeGroups);

                            button = new AdvancedButton(10, sheetHeight - 30, 100, NORMAL_HEIGHT, Component.translatable("gui.moretraffic.control_cabinet.export.to_clipboard"), null, null, b -> {
                                CompoundTag exportTag = new CompoundTag();
                                be.saveAdditional(exportTag, Minecraft.getInstance().level.registryAccess());

                                CompoundTag toCopy = new CompoundTag();

                                if (includeInstructions.selected() && exportTag.contains("Instructions"))
                                    toCopy.put("Instructions", exportTag.get("Instructions").copy());

                                if (includeGroups.selected() && exportTag.contains("Groups"))
                                    toCopy.put("Groups", exportTag.get("Groups").copy());

                                Minecraft.getInstance().keyboardHandler.setClipboard(toCopy.toString());
                                onClose();
                            });
                            adder.accept(button);
                            updateButton();
                        }

                        private void updateButton() {
                            button.active = (includeInstructions.selected() || includeGroups.selected());
                        }

                    });


        }));
        addBaseWidget(new AdvancedButton(guiX+51, guiY+224, 15, 15, be.shouldLoop ? SpritesManager.ICON_REPEAT : SpritesManager.ICON_ONETIME, new NoticeBoxTooltip(Component.translatable("gui.moretraffic.control_cabinet.repeat"), Component.translatable("gui.moretraffic.control_cabinet.repeat.message."+(be.shouldLoop ? "yes":"no")), Component.translatable("gui.moretraffic.change")), true, b->{
            be.shouldLoop = !be.shouldLoop;
            updateBEAndRefreshBE();
        }));
        addBaseWidget(new AdvancedButton(guiX+76, guiY+224, 15, 15, SpritesManager.ICON_GROUP, new NoticeBoxTooltip(Component.translatable("gui.moretraffic.control_cabinet.groups")), true, b->{
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
                                    Component.translatable("gui.moretraffic.control_cabinet.groups.cta"), HEADER, false);
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

        addBaseWidget(new AdvancedButton(guiX+76+25, guiY+224, 15, 15, SpritesManager.ICON_PLUS, new NoticeBoxTooltip(Component.translatable("gui.moretraffic.control_cabinet.plus")), true, b->{
            int sheetWidth = 150;
            int sheetHeight = 170;

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


        addBaseWidget(new AdvancedButton(guiX+167, guiY+224, 15, 15, be.isRunning ? SpritesManager.ICON_PLAY_ENGAGED : SpritesManager.ICON_PLAY, new NoticeBoxTooltip(Component.translatable("gui.moretraffic.control_cabinet.play"), null, Component.translatable("gui.moretraffic.change")), true, b->{
            if (be.isRunning) {
                be.isRunning = false;
            } else {
                be.ticksSinceStart = 0; be.lastInstructionFinishedTick = 0; be.programStep = 0;
                be.isRunning = true;
            }
            updateBEAndRefreshBE();
        }));

        addBaseWidget(new LabelWidget(guiX+183, guiY+224 + 5, Component.literal(be.ticksSinceStart/20+"s"), be.isRunning ? rgb(SELECTED) : rgb(PRIMARY), true, 0.5f));
        addBaseWidget(new CustomRenderAsWidget(gfx -> {
            double mouseX = Minecraft.getInstance().mouseHandler.xpos() * this.width / Minecraft.getInstance().getWindow().getScreenWidth();
            double mouseY = Minecraft.getInstance().mouseHandler.ypos() * this.height / Minecraft.getInstance().getWindow().getScreenHeight();

            if (mouseX >= guiX + 182 && mouseX <=  guiX + 195 &&
                mouseY >= guiY + 221 + 5 && mouseY <= guiY + 221 + 17 &&
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

        addElement(new AbstractSheet(sheetX, sheetY, Component.translatable("gui.moretraffic.control_cabinet.instruction.modify_light.select_color").getString(), true, sheetWidth, sheetHeight) {
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
        scrollOffset = Math.max(0, Math.min(scrollOffset, ((be.instructions.size() - 7)*26)+10));

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
