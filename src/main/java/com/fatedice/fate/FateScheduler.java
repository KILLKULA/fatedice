package com.fatedice.fate;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class FateScheduler {
    private static final List<ScheduledTask> TASKS = new ArrayList<>();

    public static synchronized void schedule(int delayTicks, Runnable action) {
        TASKS.add(new ScheduledTask(delayTicks, action));
    }

    public static synchronized void scheduleRepeating(int intervalTicks, int totalTimes, Consumer<Integer> actionWithCount) {
        class RepeatingTaskRunner implements Runnable {
            int current = 0;

            @Override
            public void run() {
                actionWithCount.accept(current);
                current++;
                if (current < totalTimes) {
                    schedule(intervalTicks, this);
                }
            }
        }
        schedule(intervalTicks, new RepeatingTaskRunner());
    }

    @SubscribeEvent
    public static synchronized void onServerTick(ServerTickEvent.Post event) {
        if (TASKS.isEmpty()) return;

        Iterator<ScheduledTask> iterator = TASKS.iterator();
        List<Runnable> readyToExecute = new ArrayList<>();

        while (iterator.hasNext()) {
            ScheduledTask task = iterator.next();
            task.ticksLeft--;
            if (task.ticksLeft <= 0) {
                readyToExecute.add(task.action);
                iterator.remove();
            }
        }

        for (Runnable action : readyToExecute) {
            try {
                action.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class ScheduledTask {
        int ticksLeft;
        final Runnable action;

        ScheduledTask(int ticksLeft, Runnable action) {
            this.ticksLeft = ticksLeft;
            this.action = action;
        }
    }
}
