package net.nia.witchinghour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scheduler {
    private static final Map<Integer, List<Runnable>> tasks = new HashMap<>();
    private static int currentTick = 0;

    public static void schedule(int delayTicks, Runnable task) {
        tasks.computeIfAbsent(currentTick + delayTicks, k -> new ArrayList<>()).add(task);
    }

    public static void tick() {
        List<Runnable> due = tasks.remove(currentTick);
        if (due != null) {
            due.forEach(Runnable::run);
        }
        currentTick++;
    }
}
