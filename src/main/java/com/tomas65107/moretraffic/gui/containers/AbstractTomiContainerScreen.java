package com.tomas65107.moretraffic.gui.containers;

import com.tomas65107.moretraffic.data.AbstractSheet;
import com.tomas65107.moretraffic.gui.components.BetterEditBox;
import com.tomas65107.moretraffic.gui.components.CustomRenderAsWidget;
import com.tomas65107.moretraffic.gui.components.LabelWidget;
import com.tomas65107.moretraffic.gui.components.overriderenders.NonInteractableWidget;
import com.tomas65107.moretraffic.gui.components.overriderenders.PriorityWidget;
import com.tomas65107.moretraffic.data.helpers.ColorHelper;
import com.tomas65107.moretraffic.data.ColorsManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import org.apache.logging.log4j.core.net.Priority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.Consumer;

import static com.tomas65107.moretraffic.data.ColorsManager.PRIMARY;
import static com.tomas65107.moretraffic.data.helpers.ColorHelper.rgb;


abstract public class AbstractTomiContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    private AbstractSheet sheetContainer;
    private ArrayList<AbstractWidget> baseWidgets = new ArrayList<>();

    public final boolean shouldRenderTooltips() {
        return (sheetContainer == null);
    }

    final protected void addElement(AbstractSheet sheet) {
        sheetContainer = sheet;
        refreshContent();
        refreshContent();
    }

    public AbstractTomiContainerScreen(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    private void addSheetWidget(AbstractWidget widget) {
        super.addRenderableWidget(new PriorityWidget<>(widget));
    }

    final protected void addBaseWidget(AbstractWidget widget) {
        if (sheetContainer == null) {
            super.addRenderableWidget(widget);
        }
        baseWidgets.add(widget);
    }

    @Override
    protected void init() {
        super.init();
        if (sheetContainer != null) {

            this.clearWidgets();

            for (AbstractWidget widget : baseWidgets) {
                super.addRenderableWidget(new NonInteractableWidget<>(widget));
            }

            baseWidgets.clear();

            //sheet
            addSheetWidget(new CustomRenderAsWidget( g ->
                        g.fill(0, 0, this.width, this.height, ColorHelper.rgb(ColorsManager.BACKGROUND)
            )));
            addSheetWidget(
                    new CustomRenderAsWidget(gfx -> gfx.fill(
                            sheetContainer.x,
                            sheetContainer.y,
                            sheetContainer.x + sheetContainer.width,
                            sheetContainer.y + sheetContainer.height,
                            ColorHelper.rgb(ColorsManager.TERTIARY))
                    ));

            addSheetWidget(
            new LabelWidget(10 + sheetContainer.x, 10 + sheetContainer.y, Component.literal(sheetContainer.title).withStyle(ChatFormatting.BOLD), rgb(PRIMARY), true)
            );

            sheetContainer.init(offsetAdder());

            if (sheetContainer.showDoneButton) {
                addSheetWidget(
                        Button.builder(Component.translatable("gui.moretraffic.close").withColor(DyeColor.LIME.getTextureDiffuseColor()),
                                b -> onClose()
                        ).bounds(sheetContainer.x + sheetContainer.width - 60, sheetContainer.y + sheetContainer.height - 30, 50, 20).build()
                );
            }
        }
    }

    private Consumer<AbstractWidget> offsetAdder() {
        return widget -> {

            if (widget instanceof CustomRenderAsWidget) {
                ((CustomRenderAsWidget) widget).xAdder = (widget.getX() + sheetContainer.x);
                ((CustomRenderAsWidget) widget).yAdder = (widget.getY() + sheetContainer.y);
            }

            widget.setX(widget.getX() + sheetContainer.x);
            widget.setY(widget.getY() + sheetContainer.y);
            addSheetWidget(widget);
        };
    }

    public void refreshContent() {
        this.clearWidgets(); this.init();
    }

    @Override
    public void onClose() {
        GuiEventListener focused = getFocused();
        if (focused instanceof PriorityWidget<?> pw) focused = pw.getOriginal();
        if (focused instanceof BetterEditBox betterEditBox) betterEditBox.triggerSaveCode();

        if (sheetContainer == null) {
            super.onClose();
        } else {
            sheetContainer = null;
            refreshContent();
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (sheetContainer != null) {
            sheetContainer.renderer(guiGraphics, mouseX, mouseY);
        }

    }

    @Override
    protected <T extends GuiEventListener & Renderable & NarratableEntry> @NotNull T addRenderableWidget(T widget) {
        throw new IllegalArgumentException("Incorrect usage; Please use addBaseWidget() instead of addRenderableWidget() to render widgets!");
    }

}
