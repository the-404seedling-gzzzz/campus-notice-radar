package crawler.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FavoriteService {

    private static final Logger logger = Logger.getLogger(FavoriteService.class.getName());

    private static final String FILE_NAME = "favorite_notice.jsonl";

    private FavoriteService() {
    }

    public static void addRawJson(String itemJson) {
        if (itemJson == null || itemJson.isBlank()) {
            logger.warning("收藏失败：收藏内容为空");
            return;
        }

        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            writer.write(itemJson + "\n");
            logger.info("收藏写入成功");
        } catch (IOException e) {
            logger.log(Level.WARNING, "收藏写入失败", e);
        }
    }

    public static String readAsJsonArray() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            logger.info("收藏文件不存在，返回空数组");
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

            logger.info("收藏读取成功");

        } catch (IOException e) {
            logger.log(Level.WARNING, "收藏读取失败", e);
            return "[]";
        }

        sb.append("]");
        return sb.toString();
    }
}