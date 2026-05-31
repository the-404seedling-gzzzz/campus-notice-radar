package crawler.command;

import crawler.view.ConsoleView;

public class ExitCommand implements Command {

    private final ConsoleView view;

    public ExitCommand(ConsoleView view) {
        this.view = view;
    }

    @Override
    public void execute() {
        view.showMessage("系统已退出。");
    }
}