package crawler.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HistoryService {

    private static final Logger logger = Logger.getLogger(HistoryService.class.getName());

    private static final String FILE_NAME = "crawl_history.jsonl";

    private HistoryService() {
    }

    public static void add(String school, int count, String keyword, int resultCount, long cost) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String line = "{"
                + "\"time\":\"" + escape(time) + "\","
                + "\"school\":\"" + escape(school) + "\","
                + "\"count\":" + count + ","
                + "\"keyword\":\"" + escape(keyword) + "\","
                + "\"resultCount\":" + resultCount + ","
                + "\"cost\":" + cost
                + "}";

        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            writer.write(line + "\n");
            logger.info("历史记录写入成功：" + school + "，结果数量：" + resultCount);
        } catch (IOException e) {
            logger.log(Level.WARNING, "历史记录写入失败", e);
        }
    }

    public static String readAsJsonArray() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            logger.info("历史记录文件不存在，返回空数组");
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean first = true;

            while ((line = reader.readLine()) != null) {
                if (!first) {
                    sb.append(",");
                }

                sb.append(line);
                first = false;
            }

            logger.info("历史记录读取成功");

        } catch (IOException e) {
            logger.log(Level.WARNING, "历史记录读取失败", e);
            return "[]";
        }

        sb.append("]");
        return sb.toString();
    }

    private static String escape(String text) {
        if (text == null) {
            return "";
        }

        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "")
                .replace("\r", "");
    }
}