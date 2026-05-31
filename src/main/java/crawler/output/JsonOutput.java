package crawler.output;

import crawler.model.NoticeItem;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JsonOutput implements OutputStrategy<NoticeItem> {

    private final String fileName;

    public JsonOutput(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void output(List<NoticeItem> data) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("[\n");

            for (int i = 0; i < data.size(); i++) {
                NoticeItem item = data.get(i);

                writer.write("  {\n");
                writer.write("    \"title\": \"" + escape(item.getTitle()) + "\",\n");
                writer.write("    \"date\": \"" + escape(item.getDate()) + "\",\n");
                writer.write("    \"link\": \"" + escape(item.getLink()) + "\",\n");
                writer.write("    \"summary\": \"" + escape(item.getSummary()) + "\"\n");
                writer.write("  }");

                if (i < data.size() - 1) {
                    writer.write(",");
                }
                writer.write("\n");
            }

            writer.write("]");
            System.out.println("已保存到文件: " + fileName);
        } catch (IOException e) {
            throw new RuntimeException("写入 JSON 文件失败", e);
        }
    }

    private String escape(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", " ")
                .replace("\r", " ");
    }
}