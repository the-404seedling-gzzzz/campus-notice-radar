package crawler.persist;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import crawler.exception.PersistenceException;
import crawler.model.NoticeItem;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NoticeJsonImporter {

    private static final Logger logger = Logger.getLogger(NoticeJsonImporter.class.getName());

    private final ObjectMapper mapper;

    public NoticeJsonImporter() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    public List<NoticeItem> importFrom(String filePath) {
        File file = new File(filePath);

        if (!file.exists()) {
            logger.warning("导入失败，文件不存在：" + filePath);
            return Collections.emptyList();
        }

        try {
            TypeReference<List<NoticeItem>> typeRef = new TypeReference<>() {};
            List<NoticeItem> notices = mapper.readValue(file, typeRef);

            logger.info("公告数据导入成功，数量：" + notices.size() + "，文件：" + filePath);

            return notices;

        } catch (IOException e) {
            logger.log(Level.SEVERE, "公告数据导入失败：" + filePath, e);
            throw new PersistenceException("公告数据导入失败：" + filePath, e);
        }
    }
}