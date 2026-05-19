package com.tomas65107.moretraffic.helpers;

import java.util.ArrayList;
import java.util.List;

public class ClientScheduler {

    private static final List<Task> tasks = new ArrayList<>();
    private static final List<Task> pending = new ArrayList<>();

    // optional: simple spam protection
    private static long lastAddTick = -1;
    private static final int MIN_TICK_GAP = 1;

    private static long tickCounter = 0;

    public static void runLater(int ticks, Runnable run) {
        Task task = new Task(ticks, run);

        // safety: prevents rapid spam in same tick
        if (tickCounter == lastAddTick) {
            pending.add(task);
            return;
        }

        pending.add(task);
        lastAddTick = tickCounter;
    }

    public static void tick() {
        tickCounter++;

        // safely move pending to active
        if (!pending.isEmpty()) {
            tasks.addAll(pending);
            pending.clear();
        }

        // update tasks safely
        tasks.removeIf(Task::tick);
    }

    private static class Task {
        int ticks;
        Runnable run;

        Task(int ticks, Runnable run) {
            this.ticks = ticks;
            this.run = run;
        }

        boolean tick() {
            if (--ticks <= 0) {
                run.run();
                return true;
            }
            return false;
        }
    }
}