package net.netherway.starwarschaincode.worldgen.asteroid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.ArrayList;
import java.util.List;

public class PendingGarrisonData extends SavedData {

    public record PendingGarrison(BlockPos center, int radius) {}

    private final List<PendingGarrison> pending = new ArrayList<>();
    public List<PendingGarrison> all() {
        return new ArrayList<>(pending);
    }

    public static PendingGarrisonData get(DimensionDataStorage storage) {
        return storage.computeIfAbsent(
                new SavedData.Factory<>(PendingGarrisonData::new, PendingGarrisonData::load),
                "starwarschaincode_pending_garrisons"
        );
    }

    public void markPending(BlockPos center, int radius) {
        pending.add(new PendingGarrison(center, radius));
        setDirty();
    }

    public List<PendingGarrison> forChunk(ChunkPos chunkPos) {
        List<PendingGarrison> result = new ArrayList<>();
        for (PendingGarrison g : pending) {
            if (new ChunkPos(g.center()).equals(chunkPos)) {
                result.add(g);
            }
        }
        return result;
    }

    public void remove(PendingGarrison garrison) {
        pending.remove(garrison);
        setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (PendingGarrison g : pending) {
            CompoundTag entry = new CompoundTag();
            entry.putInt("x", g.center().getX());
            entry.putInt("y", g.center().getY());
            entry.putInt("z", g.center().getZ());
            entry.putInt("radius", g.radius());
            list.add(entry);
        }
        tag.put("pending", list);
        return tag;
    }

    private static PendingGarrisonData load(CompoundTag tag, HolderLookup.Provider registries) {
        PendingGarrisonData data = new PendingGarrisonData();
        ListTag list = tag.getList("pending", 10); // 10 = CompoundTag
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            BlockPos pos = new BlockPos(entry.getInt("x"), entry.getInt("y"), entry.getInt("z"));
            data.pending.add(new PendingGarrison(pos, entry.getInt("radius")));
        }
        return data;
    }
}