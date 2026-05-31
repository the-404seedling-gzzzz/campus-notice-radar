package crawler.command;

import crawler.controller.CrawlerController;
import crawler.model.NoticeItem;
import crawler.view.ConsoleView;

import java.util.List;

public class CrawlAllCommand implements Command {

    private final CrawlerController controller;
    private final ConsoleView view;

    public CrawlAllCommand(CrawlerController controller, ConsoleView view) {
        this.controller = controller;
        this.view = view;
    }

    @Override
    public void execute() {
        int count = view.readCount();
        String keyword = view.readKeyword();

        List<NoticeItem> results = controller.crawlAll(count, keyword);

        view.showNotices(results);
    }
}