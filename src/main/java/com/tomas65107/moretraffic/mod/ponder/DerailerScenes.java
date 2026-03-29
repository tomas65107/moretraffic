package com.tomas65107.moretraffic.mod.ponder;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;

public class DerailerScenes {
    public static void scene1(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("derailer_1", "Detecting Trains");
        scene.configureBasePlate(1, 1, 9);
        scene.scaleSceneView(.65f);
        scene.setSceneOffsetY(-1);
        scene.showBasePlate();
    }
}
