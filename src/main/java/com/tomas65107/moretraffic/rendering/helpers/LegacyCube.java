package com.tomas65107.moretraffic.rendering.helpers;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tomas65107.moretraffic.mod.MoreTraffic;
import de.mrjulsen.mcdragonlib.client.model.mesh.CornerType;

import de.mrjulsen.mcdragonlib.client.ber.BERGraphics;
import de.mrjulsen.mcdragonlib.client.model.mesh.*;
import de.mrjulsen.mcdragonlib.util.DLColor;
import de.mrjulsen.mcdragonlib.util.Pair;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.function.Function;
import java.util.function.Predicate;

public class LegacyCube {

    private static final float PIXEL = 1.0F / 16.0F;

    private final BasicMesh mesh;
    private int light = 0;
    private DLColor tint = DLColor.WHITE;

    public static LegacyCube cube(
            ResourceLocation texture,
            float w, float h, float d,
            Predicate<Direction> filter,
            Function<Direction, Pair<Vec2, Vec2>> uvFunc
    ) {
        BasicMesh mesh = new BasicMesh();

        for (Direction dir : Direction.values()) {
            if (!filter.test(dir)) continue;

            Face face = createFaceForDirection(dir, w, h, d);

            // texture
            face.setTexture(texture);

            // UV
            Pair<Vec2, Vec2> uv = uvFunc.apply(dir);
            applyUV(face, uv);

            mesh.addFace(face);
        }

        mesh.cleanUp();

        return new LegacyCube(mesh);
    }

    private LegacyCube(BasicMesh mesh) {
        this.mesh = mesh;
    }

    // ------------------------
    // FACE CREATION
    // ------------------------

    private static Face createFaceForDirection(Direction dir, float w, float h, float d) {
        return switch (dir) {
            case SOUTH -> Face.createFace(dir, 0, 0, d, w, h);
            case NORTH -> Face.createFace(dir, 0, 0, 0, w, h);
            case EAST  -> Face.createFace(dir, w, 0, 0, d, h);
            case WEST  -> Face.createFace(dir, 0, 0, 0, d, h);
            case UP    -> Face.createFace(dir, 0, h, 0, w, d);
            case DOWN  -> Face.createFace(dir, 0, 0, 0, w, d);
        };
    }

    // ------------------------
    // UV HANDLING
    // ------------------------

    private static void applyUV(Face face, Pair<Vec2, Vec2> uv) {
        Vec2 min = uv.getFirst();
        Vec2 max = uv.getSecond();

        // Flip horizontally (this fixes winding in most cases)
        face.getCorner(CornerType.TOP_LEFT).setUV(new Vector2f(max.x, min.y));
        face.getCorner(CornerType.TOP_RIGHT).setUV(new Vector2f(min.x, min.y));
        face.getCorner(CornerType.BOTTOM_LEFT).setUV(new Vector2f(max.x, max.y));
        face.getCorner(CornerType.BOTTOM_RIGHT).setUV(new Vector2f(min.x, max.y));
    }

    // ------------------------
    // SETTINGS
    // ------------------------

    public void setLight(int light) {
        this.light = light;
    }

    public void setTint(int color) {
        this.tint = DLColor.fromInt(color);
    }

    // ------------------------
    // RENDER
    // ------------------------

    public void render(BERGraphics<?> graphics) {
        graphics.poseStack().pushPose();
        graphics.poseStack().scale(1f, -1f, 1f);
        graphics.poseStack().translate(0f, -0.25f, 0f);

        for (Face face : mesh.getFaces()) {
            face.setLight(light);
            face.setColor(tint);

            face.render(graphics, light, graphics.packedOverlay(), false, false);
        }

        graphics.poseStack().popPose();
    }
}