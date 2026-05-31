package crawler.persist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import crawler.exception.PersistenceException;
import crawler.model.NoticeItem;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NoticeJsonExporter {

    private static final Logger logger = Logger.getLogger(NoticeJsonExporter.class.getName());

    private final ObjectMapper mapper;

    public NoticeJsonExporter() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void export(List<NoticeItem> notices, String filePath) {
        File file = new File(filePath);

        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            mapper.writeValue(file, notices);

            logger.info("公告数据导出成功，数量：" + notices.size() + "，文件：" + filePath);

        } catch (IOException e) {
            logger.log(Level.SEVERE, "公告数据导出失败：" + filePath, e);
            throw new PersistenceException("公告数据导出失败：" + filePath, e);
        }
    }
}