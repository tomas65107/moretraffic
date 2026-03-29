package com.tomas65107.moretraffic.mod.ponder;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import com.tomas65107.moretraffic.block.AdvancedTrafficLightBlockEntity;
import com.tomas65107.moretraffic.block.FlashingBlinkerBlockEntity;
import com.tomas65107.moretraffic.data.TrafficLightLight;
import com.tomas65107.moretraffic.registration.MTRegistrate;
import com.tomas65107.moretraffic.rendering.BlinkerBlockEntityRenderer;
import de.mrjulsen.trafficcraft.data.PaintColor;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

import java.util.ArrayList;
import java.util.Collection;

public class ControlCabinetScenes {

    public static void scene1(SceneBuilder sceneBuilder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(sceneBuilder);
        scene.title("cabinet_1", "cabinet");
        scene.showBasePlate();

        BlockPos controlCabinet = util.grid().at(2, 1, 2);
        BlockPos blinker = util.grid().at(3, 1, 1);
        BlockPos trafficLight = util.grid().at(1, 1, 1);

        scene.world().showSection(util.select().position(controlCabinet), Direction.NORTH);
//        scene.world().setBlock(controlCabinet, AllBlocks.LARGE_COGWHEEL.getDefaultState().setValue(CogWheelBlock.AXIS, Direction.Axis.X), false);

        scene.overlay()
                .showText(90)
                .text("1") //Can be used to make complex systems and automations that control other TrafficCraft blocks
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector()
                        .blockSurface(controlCabinet, Direction.SOUTH));
        scene.idle(100);

        scene.overlay()
                .showText(40)
                .text("2") //It has 2 main categories: Groups and Instructions
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector()
                        .blockSurface(controlCabinet, Direction.SOUTH));
        scene.idle(50);

//        scene.overlay().showOutline(PonderPalette.BLUE, "menuOutline1", util.select().fromTo(controlCabinet, controlCabinet), 100);

        scene.world().showSection(util.select().position(trafficLight), Direction.NORTH);
        scene.world().showSection(util.select().position(blinker), Direction.NORTH);
        scene.idle(5);

        scene.overlay().showOutline(PonderPalette.MEDIUM, "1", util.select().fromTo(blinker, blinker), 50);
        scene.overlay().showOutline(PonderPalette.MEDIUM, "2", util.select().fromTo(trafficLight, trafficLight), 50);
        scene.overlay()
                .showText(90)
                .text("3"); //Groups allow you to group different MoreTraffic Blocks and modify them together
        scene.idle(50);
        scene.overlay().showOutline(PonderPalette.GREEN, "3", util.select().fromTo(blinker, trafficLight), 40);

        scene.idle(50);

        scene.world().modifyBlockEntity(blinker, FlashingBlinkerBlockEntity.class, be -> {
            be.lightStatus = true;
            be.setColor(PaintColor.GREEN);
        });
        scene.world().modifyBlockEntity(trafficLight, AdvancedTrafficLightBlockEntity.class, be -> {
            be.lights = new ArrayList<>();
            be.lights.add(new TrafficLightLight(DyeColor.RED, new TrafficLightLight.TrafficLightMask()));
            be.lights.add(new TrafficLightLight(DyeColor.ORANGE, new TrafficLightLight.TrafficLightMask()));
            be.lights.add(new TrafficLightLight(DyeColor.GREEN, new TrafficLightLight.TrafficLightMask()));
        });

        scene.overlay()
                .showText(40)
                .text("4") //You can modify them with instructions
                .colored(PonderPalette.GREEN);
        scene.idle(50);

//        scene.overlay().showControls(util.vector().blockSurface(controlCabinet, Direction.NORTH), Pointing.RIGHT, 87)
//                .withItem(AllItems.BRASS_HAND.asStack());

        scene.overlay()
                .showText(60)
                .text("5") //Open the GUI and explore! There are tips included
                .colored(PonderPalette.RED);
        scene.overlay().showControls(util.vector().blockSurface(controlCabinet, Direction.NORTH), Pointing.RIGHT, 60)
                .withItem(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("trafficcraft", "wrench")).asItem().getDefaultInstance());


        scene.markAsFinished();

    }

}
