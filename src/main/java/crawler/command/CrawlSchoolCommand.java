package crawler.command;

import crawler.controller.CrawlerController;
import crawler.model.NoticeItem;
import crawler.view.ConsoleView;

import java.util.List;

public class CrawlSchoolCommand implements Command {

    private final CrawlerController controller;
    private final ConsoleView view;

    public CrawlSchoolCommand(CrawlerController controller, ConsoleView view) {
        this.controller = controller;
        this.view = view;
    }

    @Override
    public void execute() {
        String schoolCode = view.readSchoolCode();
        int count = view.readCount();
        String keyword = view.readKeyword();

        List<NoticeItem> results = controller.crawlSchool(schoolCode, count, keyword);

        view.showNotices(results);
    }
}