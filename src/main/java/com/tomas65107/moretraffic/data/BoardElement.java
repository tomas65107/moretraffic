package com.tomas65107.moretraffic.data;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tomas65107.moretraffic.block.SignboardBlockEntity;
import com.tomas65107.moretraffic.rendering.helpers.LegacyCube;
import de.mrjulsen.mcdragonlib.client.ber.BERGraphics;
import de.mrjulsen.mcdragonlib.client.util.DLGraphics;
import de.mrjulsen.mcdragonlib.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec2;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.tomas65107.moretraffic.rendering.MaterialValues.*;
import static com.tomas65107.moretraffic.rendering.TrafficLightBlockEntityRenderer.getBoostedTint;

public abstract class BoardElement {
    public int startX;
    public int startY;
    public abstract void render(BERGraphics<? extends BlockEntity> graphics);
    public abstract CompoundTag serialize();

    public static BoardElement deserialize(CompoundTag tag) {
        return switch (BoardElementType.byId(tag.getString("Type"))) {
            case TEXT -> Text.deserialize(tag);
            case BACKGROUND -> Background.deserialize(tag);
            case SPRITE -> Sprite.deserialize(tag);
        };
    }

    public enum BoardSizes {
        TINY(4, 4),
        SMALL(8, 6),
        MEDIUM(12, 10),
        FULL(16, 16);

        public final int sizeX;
        public final int sizeY;

        BoardSizes(int sizeX, int sizeY) {
            this.sizeX = sizeX;
            this.sizeY = sizeY;
        }
    }

    public enum BoardElementType {
        TEXT("Text", Text.class, new Text(0, 0, "", 3)),
        BACKGROUND("Background", Background.class, new Background(0,0, DyeColor.WHITE)),
        SPRITE("Sprite",  Sprite.class, new Sprite(0, 0));

        public final String id;
        public final Class<? extends BoardElement> clazz;
        public final Object defaultValue;

        BoardElementType(String id, Class<? extends BoardElement> clazz, Object newValue) {
            this.id = id;
            this.clazz = clazz;
            this.defaultValue = newValue;
        }

        public static BoardElementType byClass(Class<? extends BoardElement> classToFind) {
            for (BoardElementType type : values()) {
                if (type.clazz.equals(classToFind)) return type;
            }
            throw new IllegalArgumentException("No element found for class " + classToFind);
        }

        public static BoardElementType byId(String id) {
            for (BoardElementType type : values()) {
                if (type.id.equals(id)) return type;
            }
            throw new IllegalArgumentException("Unknown BoardElementType: " + id);
        }

        public Component getComponentOfProperty() {
            return switch(this) {
                case TEXT -> Component.translatable("gui.moretraffic.signboard.element.text");
                case BACKGROUND -> Component.translatable("gui.moretraffic.signboard.element.bg");
                case SPRITE -> Component.translatable("gui.moretraffic.signboard.element.sprite");
                default -> throw new IllegalArgumentException("Unknown BoardElementType: " + id);
            };
        }
    }

    private BoardElement(int startX, int startY) {
        this.startX = startX;
        this.startY = startY;
    }

    public static class Text extends BoardElement {
        public String json;
        public float size;

        public Text(int startX, int startY, String json, float size) {
            super(startX, startY);
            this.json = json;
            this.size = size;
        }

        @Override
        public void render(BERGraphics graphics) {
            Font font = Minecraft.getInstance().font;

            graphics.poseStack().pushPose();

            graphics.poseStack().translate(startX, -15.8 + startY, 0);
            graphics.poseStack().scale(size / 8, size / 8, 1);

            try {
                Component component = Objects.requireNonNull(
                        Component.Serializer.fromJson(json)
                );

                float scale = size / 8f;

                // Visible area of the 16x16 board in local text coordinates.
                float minX = -startX / scale;
                float maxX = (16f - startX) / scale;
                float minY = -(startY) / scale;
                float maxY = (16f - startY) / scale;

                String text = component.getString();
                StringBuilder visible = new StringBuilder();

                int currentWidth = 0;
                for (int i = 0; i < text.length(); i++) {
                    char c = text.charAt(i);
                    int charWidth = font.width(String.valueOf(c));

                    if (currentWidth + charWidth > maxX) {
                        break;
                    }

                    if (currentWidth + charWidth >= Math.max(0, minX)) {
                        visible.append(c);
                    }

                    currentWidth += charWidth;
                }

                if (!visible.isEmpty() && maxY > 0 && minY < font.lineHeight) {
                    font.drawInBatch(
                            Component.literal(visible.toString()).withStyle(component.getStyle()),
                            Math.max(0, minX),
                            0,
                            0xFFFFFF,
                            false,
                            graphics.poseStack().last().pose(),
                            graphics.multiBufferSource(),
                            Font.DisplayMode.NORMAL,
                            0,
                            graphics.packedLight()
                    );
                }
            } catch (Exception ignored) {
            }

            graphics.poseStack().popPose();
        }


        @Override
        public CompoundTag serialize() {
            CompoundTag tag = new CompoundTag();
            tag.putString("Type", "Text");
            tag.putInt("startX", startX);
            tag.putInt("startY", startY);
            tag.putFloat("size", size);
            tag.putString("json", json);
            return tag;
        }

        public static Text deserialize(CompoundTag tag) {
            int x = tag.getInt("startX");
            int y = tag.getInt("startY");
            float size = tag.getFloat("size");
            String json = tag.getString("json");
            return new Text(x, y, json, size);
        }
    }

    public static class Background extends BoardElement {
        public DyeColor color;

        public Background(int startX, int startY, DyeColor color) {
            super(startX, startY);
            this.color = color;
        }

        @Override
        public void render(BERGraphics<? extends BlockEntity> graphics) {
            float width = 16f - startX;
            float height = 16f - startY;

            graphics.poseStack().translate(startX / 2f, -startY / 2f, 0);

            LegacyCube light = LegacyCube.cube(
                    ResourceLocation.fromNamespaceAndPath("moretraffic", "textures/block/light_on_solid.png"),
                    width, height, 0f,
                    dir -> dir == Direction.SOUTH || dir == Direction.NORTH,
                    dir -> Pair.of(new Vec2(0f, 0f), new Vec2(1f, 1f))
            );

            light.setLight(NOT_EMISSIVE);
            light.setTint(getBoostedTint(color));
            light.render(graphics);
        }

        @Override
        public CompoundTag serialize() {
            CompoundTag tag = new CompoundTag();
            tag.putString("Type", "Background");
            tag.putInt("startX", startX);
            tag.putInt("startY", startY);
            tag.putString("color", color.getSerializedName());
            return tag;
        }

        public static Background deserialize(CompoundTag tag) {
            int x = tag.getInt("startX");
            int y = tag.getInt("startY");
            DyeColor color = DyeColor.byName(tag.getString("color"), DyeColor.BLACK);
            return new Background(x, y, color);
        }
    }

    public static class Sprite extends BoardElement {
        public List<DyeColor> rows;

        public Sprite(int startX, int startY) {
            super(startX, startY);
            this.rows = new ArrayList<>(Collections.nCopies(256, DyeColor.BLACK));
        }

        public Sprite(int startX, int startY, List<DyeColor> rows) {
            super(startX, startY);
            this.rows = rows;
        }

        public DyeColor get(int x, int y) {
            if (x < 0 || x >= 16 || y < 0 || y >= 16) throw new IndexOutOfBoundsException("Pixel getter input out of bounds");
            return rows.get(y * 16 + x);
        }

        public void set(int x, int y, DyeColor color) {
            if (x < 0 || x >= 16 || y < 0 || y >= 16) throw new IndexOutOfBoundsException("Pixel getter input out of bounds");
            rows.set(y * 16 + x, color);
        }

        @Override
        public void render(BERGraphics<? extends BlockEntity> guiGraphics) {

        }

        @Override
        public CompoundTag serialize() {
            CompoundTag tag = new CompoundTag();
            tag.putString("Type", "Sprite");
            tag.putInt("startX", startX);
            tag.putInt("startY", startX);

            StringBuilder sb = new StringBuilder(256);
            for (DyeColor pixel : rows) {
                sb.append(Integer.toHexString(pixel.getId()));
            }
            tag.putString("rows", sb.toString());

            return tag;
        }

        public static Sprite deserialize(CompoundTag tag) {
            int x = tag.getInt("startX");
            int y = tag.getInt("startY");
            String data = tag.getString("rows");

            if (data.length() != 256) throw new IllegalArgumentException("Invalid pixel data length: " + data.length());

            Sprite pixels = new Sprite(x, y);
            for (int i = 0; i < 256; i++) {
                int id = Character.digit(data.charAt(i), 16);
                pixels.rows.set(i, DyeColor.byId(id));
            }
            return pixels;
        }
    }
}
