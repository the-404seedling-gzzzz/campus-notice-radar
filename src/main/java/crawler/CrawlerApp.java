package crawler;

import crawler.command.Command;
import crawler.command.CrawlAllCommand;
import crawler.command.CrawlSchoolCommand;
import crawler.command.ExitCommand;
import crawler.command.ExportCommand;
import crawler.command.ImportCommand;
import crawler.controller.CrawlerController;
import crawler.exception.CrawlerException;
import crawler.util.LogConfig;
import crawler.view.ConsoleView;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CrawlerApp {

    private static final Logger logger = Logger.getLogger(CrawlerApp.class.getName());

    public static void main(String[] args) {
        LogConfig.init();

        ConsoleView view = new ConsoleView();
        CrawlerController controller = new CrawlerController();

        Map<String, Command> commands = new HashMap<>();
        commands.put("1", new CrawlSchoolCommand(controller, view));
        commands.put("2", new CrawlAllCommand(controller, view));
        commands.put("3", new ExportCommand(controller, view));
        commands.put("4", new ImportCommand(controller, view));
        commands.put("0", new ExitCommand(view));

        view.showWelcome();

        boolean running = true;

        while (running) {
            view.showMenu();

            String choice = view.readChoice();

            if ("5".equals(choice)) {
                view.showSupportedSchools();
                continue;
            }

            Command command = commands.get(choice);

            if (command == null) {
                view.showError("无效命令，请重新输入。");
                continue;
            }

            try {
                command.execute();

                if ("0".equals(choice)) {
                    running = false;
                }

            } catch (CrawlerException e) {
                logger.log(Level.WARNING, "业务异常：" + e.getMessage(), e);
                view.showError(e.getMessage());

            } catch (Exception e) {
                logger.log(Level.SEVERE, "系统未知异常", e);
                view.showError("系统发生未知异常：" + e.getMessage());
            }
        }
    }
}