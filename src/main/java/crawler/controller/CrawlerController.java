package crawler.controller;

import crawler.core.Crawler;
import crawler.exception.InvalidCommandException;
import crawler.model.NoticeItem;
import crawler.persist.NoticeJsonExporter;
import crawler.persist.NoticeJsonImporter;
import crawler.service.HistoryService;
import crawler.service.SchoolCrawlerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CrawlerController {

    private static final Logger logger = Logger.getLogger(CrawlerController.class.getName());

    private final NoticeJsonExporter exporter = new NoticeJsonExporter();
    private final NoticeJsonImporter importer = new NoticeJsonImporter();

    private List<NoticeItem> lastResults = new ArrayList<>();

    public List<NoticeItem> crawlSchool(String schoolCode, int count, String keyword) {
        long start = System.currentTimeMillis();

        SchoolCrawlerFactory.SchoolOption option = SchoolCrawlerFactory.getSchool(schoolCode);

        Crawler<NoticeItem> crawler = option.createCrawler(count);

        logger.info("CLI 开始爬取：" + option.getName());

        List<NoticeItem> results = crawler.crawl();
        results = filterByKeyword(results, keyword);

        long cost = System.currentTimeMillis() - start;

        HistoryService.add(option.getName(), count, keyword, results.size(), cost);

        lastResults = results;

        logger.info("CLI 爬取完成：" + option.getName() + "，数量：" + results.size());

        return results;
    }

    public List<NoticeItem> crawlAll(int count, String keyword) {
        long start = System.currentTimeMillis();

        List<NoticeItem> allResults = new ArrayList<>();

        for (SchoolCrawlerFactory.SchoolOption option : SchoolCrawlerFactory.getAllSchools()) {
            try {
                Crawler<NoticeItem> crawler = option.createCrawler(count);

                List<NoticeItem> results = crawler.crawl();
                results = filterByKeyword(results, keyword);

                allResults.addAll(results);

                logger.info("CLI 爬取完成：" + option.getName() + "，数量：" + results.size());

            } catch (Exception e) {
                logger.warning(option.getName() + " 爬取失败：" + e.getMessage());
            }
        }

        long cost = System.currentTimeMillis() - start;

        HistoryService.add("全部学校", count, keyword, allResults.size(), cost);

        lastResults = allResults;

        return allResults;
    }

    public void exportLastResults(String filePath) {
        if (lastResults == null || lastResults.isEmpty()) {
            throw new InvalidCommandException("当前没有可导出的数据，请先执行爬取。");
        }

        exporter.export(lastResults, filePath);
    }

    public List<NoticeItem> importResults(String filePath) {
        List<NoticeItem> results = importer.importFrom(filePath);
        lastResults = results;
        return results;
    }

    public List<NoticeItem> getLastResults() {
        return lastResults;
    }

    private List<NoticeItem> filterByKeyword(List<NoticeItem> items, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return items;
        }

        List<NoticeItem> result = new ArrayList<>();

        for (NoticeItem item : items) {
            String title = item.getTitle() == null ? "" : item.getTitle();
            String summary = item.getSummary() == null ? "" : item.getSummary();

            if (title.contains(keyword) || summary.contains(keyword)) {
                result.add(item);
            }
        }

        return result;
    }
}