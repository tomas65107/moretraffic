package com.tomas65107.moretraffic.block;

import com.tomas65107.moretraffic.data.TrafficLightGroup;
import com.tomas65107.moretraffic.data.lightinstructions.AwaitRedstone;
import com.tomas65107.moretraffic.data.lightinstructions.Delay;
import com.tomas65107.moretraffic.data.lightinstructions.LightInstructionProperty;
import com.tomas65107.moretraffic.data.lightinstructions.ModifyLight;
import com.tomas65107.moretraffic.gui.containers.LightControlCabinetMenu;
import com.tomas65107.moretraffic.registration.MTBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.tomas65107.moretraffic.data.lightinstructions.LightInstructionProperty.PropertyTypes.*;

public class LightControlCabinetBlockEntity extends BlockEntity implements MenuProvider {

    public boolean isRunning;
    public int ticksSinceStart;
    public int lastInstructionFinishedTick;
    public short programStep;
    public boolean shouldLoop;
    public List<TrafficLightGroup> groups = new ArrayList<>();
    public List<LightInstructionProperty> instructions = new ArrayList<>();

    public LightControlCabinetBlockEntity(BlockPos pos, BlockState blockState) {
        super(MTBE.CONTROL_CABINET_BE.get(), pos, blockState);

        isRunning = false;
        ticksSinceStart = 0;
        lastInstructionFinishedTick = 0;
        programStep = 0;
        shouldLoop = false;

        instructions.clear();
        groups.clear();
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);

        isRunning = tag.getBoolean("IsRunning");
        shouldLoop = tag.getBoolean("ShouldLoop");
        ticksSinceStart = tag.getInt("TicksSinceStart");
        lastInstructionFinishedTick = tag.getInt("LastInstructionFinishedTick");
        programStep = tag.getShort("ProgramStep");

        instructions.clear();

        //instructions
        if (tag.contains("Instructions", 9)) { // 9 = ListTag
            ListTag instructionsTag = tag.getList("Instructions", 10); // 10 = CompoundTag

            for (int i = 0; i < instructionsTag.size(); i++) {
                CompoundTag nbtInstruction = instructionsTag.getCompound(i);

                LightInstructionProperty instruction = switch (
                        getPropertyOfName(nbtInstruction.getString("Type"))
                        ) {
                    case AWAIT_REDSTONE -> new AwaitRedstone(
                            nbtInstruction.getInt("InternalTimestamp")
                    );

                    case DELAY -> new Delay(
                            nbtInstruction.getInt("Ticks")
                    );

                    case MODIFY_LIGHT -> new ModifyLight(
                            nbtInstruction.getString("Group"),
                            DyeColor.byName(nbtInstruction.getString("LightColor0"), DyeColor.BLACK),
                            DyeColor.byName(nbtInstruction.getString("LightColor1"), DyeColor.BLACK),
                            DyeColor.byName(nbtInstruction.getString("LightColor2"), DyeColor.BLACK)
                    );

                    case null, default ->
                            throw new IllegalArgumentException("Error loading BE; Unknown instruction type");
                };

                instructions.add(instruction);
            }
        }

        //groups
        groups.clear();
        ListTag groupsTag = tag.getList("Groups", 10); // 10 = CompoundTag
        for (int i = 0; i < groupsTag.size(); i++) {
            CompoundTag Ctag = groupsTag.getCompound(i);
            TrafficLightGroup group = new TrafficLightGroup();
            group.name = Ctag.getString("Name");

            List<BlockPos> positions = new ArrayList<>();
            ListTag positionsTag = Ctag.getList("Positions", 4); // 4 = LongTag
            for (Tag value : positionsTag) if (value instanceof LongTag longTag) positions.add(BlockPos.of(longTag.getAsLong()));

            group.lightsPositions = positions;
            groups.add(group);
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putBoolean("IsRunning", isRunning);
        tag.putBoolean("ShouldLoop", shouldLoop);
        tag.putInt("TicksSinceStart", ticksSinceStart);
        tag.putInt("LastInstructionFinishedTick", lastInstructionFinishedTick);
        tag.putShort("ProgramStep", programStep);

        // instructions
        ListTag instructionsTag = new ListTag();

        for (LightInstructionProperty instruction : instructions) {
            CompoundTag entry = new CompoundTag();

            entry.putString("Type", instruction.getClassType().getNameOfProperty());

            if (instruction instanceof ModifyLight(String group, DyeColor light0, DyeColor light1, DyeColor light2)) {
                entry.putString("Group", group);
                entry.putString("LightColor0", light0.getName());
                entry.putString("LightColor1", light1.getName());
                entry.putString("LightColor2", light2.getName());
            }
            else if (instruction instanceof Delay(int delayInTicks)) {
                entry.putInt("Ticks", delayInTicks);
            }
            else if (instruction instanceof AwaitRedstone(int internalTimestamp)) {
                entry.putInt("InternalTimestamp", internalTimestamp);
            }
            else {
                throw new IllegalArgumentException("Error saving BE; Unknown instruction type");
            }

            instructionsTag.add(entry);
        }

        tag.put("Instructions", instructionsTag);

        // groups
        ListTag groupsTag = new ListTag();
        for (TrafficLightGroup group : groups) {
            CompoundTag Ctag = new CompoundTag();
            Ctag.putString("Name", group.name);

            ListTag positionsTag = new ListTag();
            for (BlockPos pos : group.lightsPositions) positionsTag.add(LongTag.valueOf(pos.asLong()));
            Ctag.put("Positions", positionsTag);

            groupsTag.add(Ctag);
        }
        tag.put("Groups", groupsTag);

    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, LightControlCabinetBlockEntity be) {

        //Cannot run when no instructions are added
        if (be.isRunning && be.instructions.isEmpty()) {
            be.isRunning = false;
        } else

            //Handle illegal values ui prevents setting
            if (be.instructions.size() > 64 || be.instructions.size() < be.programStep) {
                level.destroyBlock(pos, true);
                return;
            } else

            //Loop handling
            if (be.programStep == be.instructions.size()) {
                if (be.shouldLoop) {
                    be.programStep = 0;
                    be.lastInstructionFinishedTick = 0;
                    be.ticksSinceStart = 0;
                } else {
                    be.isRunning = false;
                }
            } else

                //Main loop
                if (be.isRunning) {
                    be.ticksSinceStart++;
                    LightInstructionProperty currentInstruction = be.instructions.get(be.programStep);

                    if (currentInstruction.executePayload(be, currentInstruction , level, pos)) {
                        be.lastInstructionFinishedTick = be.ticksSinceStart;
                        be.programStep++;
                    }
                } else return;

        be.setChanged();
        level.sendBlockUpdated(pos, state, state, 3);
    }

        @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider lookup) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, lookup);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        ClientboundBlockEntityDataPacket packet = ClientboundBlockEntityDataPacket.create(this);
        return packet;
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider lookup) {
        super.onDataPacket(connection, packet, lookup);
        this.loadAdditional(packet.getTag(), lookup);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("null");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new LightControlCabinetMenu(id, inventory, getBlockPos());
    }
}
