package eclass.kr;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AlertPublisher {

    public static class DeadlineItem {
        public final String course;
        public final String event;
        public final String type;
        public final String deadline;

        public DeadlineItem(String course, String event, String type, String deadline) {
            this.course = course;
            this.event = event;
            this.type = type;
            this.deadline = deadline;
        }
    }

    public static void publish(String status, List<DeadlineItem> items) {
        try {
            Files.createDirectories(Paths.get("web-alert"));
            byte[] json = buildJson(status, items).getBytes(StandardCharsets.UTF_8);
            Files.write(Paths.get("web-alert", "alert.json"), json);
            System.out.println("\nAlert published: zayniev.uz/deadline-notifier-iot-alert/web-alert (status=" + status + ")");
        } catch (IOException e) {
            System.err.println("Failed to write alert.json: " + e.getMessage());
        }
    }

    private static String buildJson(String status, List<DeadlineItem> items) {
        String timestamp = OffsetDateTime.now(ZoneOffset.ofHours(5))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " (GMT+5)";
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"status\": \"").append(status).append("\",\n");
        sb.append("  \"updated\": \"").append(timestamp).append("\",\n");
        sb.append("  \"deadlines\": [");
        if (!items.isEmpty()) {
            sb.append("\n");
            for (int i = 0; i < items.size(); i++) {
                DeadlineItem item = items.get(i);
                sb.append("    {\n");
                sb.append("      \"course\": ").append(jsonStr(item.course)).append(",\n");
                sb.append("      \"event\": ").append(jsonStr(item.event)).append(",\n");
                sb.append("      \"type\": ").append(jsonStr(item.type)).append(",\n");
                sb.append("      \"deadline\": ").append(jsonStr(item.deadline)).append("\n");
                sb.append("    }");
                if (i < items.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("  ");
        }
        sb.append("]\n}");
        return sb.toString();
    }

    private static String jsonStr(String s) {
        return "\"" + (s == null ? "" : s)
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r") + "\"";
    }
}
