package net.nia.witchinghour.events;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class TimeChecker {

    public static boolean isWitchingHour = false;
    private static int lastHour = -1; // prevents spam

    private static final String[] CLOCKS = {
            "🕛","🕐","🕑","🕒","🕓","🕔",
            "🕕","🕖","🕗","🕘","🕙","🕚"
    };

    private static String formatHour12(int hour24) {
        int hour12 = hour24 % 12;
        if (hour12 == 0) hour12 = 12;

        String suffix = (hour24 < 12) ? "AM" : "PM";

        return hour12 + " " + suffix;
    }

    public static int getCurrentTimeColor(MinecraftClient client) {
        if (client.world == null) return 0xFFFFFF;

        long time = client.world.getTimeOfDay() % 24000L;

        // Shift so 0 = midnight
        long shifted = (time + 6000) % 24000;

        // Distance from 3 PM (15000)
        float dist = Math.abs(shifted - 15000);
        dist = Math.min(dist, 24000 - dist);

        // 0 = lightest, 1 = darkest
        float progress = dist / 12000f;

        int start = 0xD28CFF; // light purple
        int end   = 0x6A0DAD; // dark purple

        int r = (int)(((start >> 16) & 0xFF) * (1 - progress) + ((end >> 16) & 0xFF) * progress);
        int g = (int)(((start >> 8)  & 0xFF) * (1 - progress) + ((end >> 8)  & 0xFF) * progress);
        int b = (int)(((start)       & 0xFF) * (1 - progress) + ((end)       & 0xFF) * progress);

        return (r << 16) | (g << 8) | b;
    }


    public static void update(MinecraftServer server) {
        if (server == null) return;

        ServerWorld world = server.getWorld(ServerWorld.OVERWORLD);

        long time = world.getTimeOfDay() % 24000L;

        // Convert ticks → hour (0–23)
        int hour24 = (int)((time + 6000) % 24000) / 1000;

        isWitchingHour = (time >= 21000 && time <= 22000);

        // Only print once per hour
        if (hour24 != lastHour) {
            lastHour = hour24;

            // Convert to 12-hour format
            String hourString = formatHour12(hour24);

            // Pick a cute clock icon
            String icon = CLOCKS[hour24 % 12];

            // Shift time so 0 = midnight
            long shifted = (time + 6000) % 24000;

            // Distance from 3 PM (15000)
            float dist = Math.abs(shifted - 15000);
            dist = Math.min(dist, 24000 - dist); // wrap-around

            // 0 = lightest (3 PM), 1 = darkest (3 AM)
            float progress = dist / 12000f;

            // Color interpolation (light purple → dark purple)
            int start = 0xD28CFF; // lightest
            int end   = 0x6A0DAD; // darkest

            int r = (int)(((start >> 16) & 0xFF) * (1 - progress) + ((end >> 16) & 0xFF) * progress);
            int g = (int)(((start >> 8)  & 0xFF) * (1 - progress) + ((end >> 8)  & 0xFF) * progress);
            int b = (int)(((start)       & 0xFF) * (1 - progress) + ((end)       & 0xFF) * progress);

            int color = (r << 16) | (g << 8) | b;

            // Send the message
            server.getPlayerManager().broadcast(
                    Text.literal(icon + " " + hourString)
                            .styled(style -> style.withColor(color)),
                    false
            );
        }
    }
}