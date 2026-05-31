package crawler.command;

import crawler.controller.CrawlerController;
import crawler.view.ConsoleView;

public class ExportCommand implements Command {

    private final CrawlerController controller;
    private final ConsoleView view;

    public ExportCommand(CrawlerController controller, ConsoleView view) {
        this.controller = controller;
        this.view = view;
    }

    @Override
    public void execute() {
        String filePath = view.readFilePath("data/notice_export.json");

        controller.exportLastResults(filePath);

        view.showMessage("导出成功，文件路径：" + filePath);
    }
}