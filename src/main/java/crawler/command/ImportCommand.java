package crawler.command;

import crawler.controller.CrawlerController;
import crawler.model.NoticeItem;
import crawler.view.ConsoleView;

import java.util.List;

public class ImportCommand implements Command {

    private final CrawlerController controller;
    private final ConsoleView view;

    public ImportCommand(CrawlerController controller, ConsoleView view) {
        this.controller = controller;
        this.view = view;
    }

    @Override
    public void execute() {
        String filePath = view.readFilePath("data/notice_export.json");

        List<NoticeItem> results = controller.importResults(filePath);

        view.showMessage("导入完成，数据条数：" + results.size());
        view.showNotices(results);
    }
}