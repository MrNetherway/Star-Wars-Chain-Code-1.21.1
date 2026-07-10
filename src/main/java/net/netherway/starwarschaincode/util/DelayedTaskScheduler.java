package net.netherway.starwarschaincode.util;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = "starwarschaincode")
public class DelayedTaskScheduler {

    private record ScheduledTask(long executeAtTick, Runnable task) {}

    private static final List<ScheduledTask> tasks = new ArrayList<>();

    /** Executa "task" depois de "delayTicks" ticks (20 ticks = 1 segundo). */
    public static void schedule(ServerLevel level, int delayTicks, Runnable task) {
        tasks.add(new ScheduledTask(level.getGameTime() + delayTicks, task));
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (tasks.isEmpty()) return;

        long now = event.getServer().overworld().getGameTime();
        List<ScheduledTask> toRun = tasks.stream()
                .filter(t -> t.executeAtTick() <= now)
                .toList();

        toRun.forEach(t -> t.task().run());
        tasks.removeAll(toRun);
    }
}