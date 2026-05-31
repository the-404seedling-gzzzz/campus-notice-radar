package crawler.output;

import crawler.model.NoticeItem;
import crawler.util.ConsoleColor;

import java.util.List;

public class PrettyConsoleOutput implements OutputStrategy<NoticeItem> {

    @Override
    public void output(List<NoticeItem> data) {
        if (data == null || data.isEmpty()) {
            System.out.println(ConsoleColor.YELLOW + "暂无数据。" + ConsoleColor.RESET);
            return;
        }

        System.out.println(ConsoleColor.CYAN + "┌────┬────────────────────────────────────────────┬──────────────┐" + ConsoleColor.RESET);
        System.out.println(ConsoleColor.CYAN + "│ 序号 │ 标题                                       │ 日期         │" + ConsoleColor.RESET);
        System.out.println(ConsoleColor.CYAN + "├────┼────────────────────────────────────────────┼──────────────┤" + ConsoleColor.RESET);

        for (int i = 0; i < data.size(); i++) {
            NoticeItem item = data.get(i);

            String title = cut(item.getTitle(), 20);
            String date = item.getDate() == null ? "未知日期" : item.getDate();

            System.out.printf("│ %-2d │ %-40s │ %-12s │%n", i + 1, title, date);
        }

        System.out.println(ConsoleColor.CYAN + "└────┴────────────────────────────────────────────┴──────────────┘" + ConsoleColor.RESET);

        System.out.println();
        System.out.println(ConsoleColor.GREEN + "详细链接如下：" + ConsoleColor.RESET);

        for (int i = 0; i < data.size(); i++) {
            NoticeItem item = data.get(i);
            System.out.println((i + 1) + ". " + item.getTitle());
            System.out.println("   链接：" + item.getLink());
            System.out.println("   摘要：" + cut(item.getSummary(), 60));
            System.out.println();
        }
    }

    private String cut(String text, int maxLength) {
        if (text == null) {
            return "";
        }

        text = text.replace("\n", "").replace("\r", "").trim();

        if (text.length() <= maxLength) {
            return text;
        }

        return text.substring(0, maxLength) + "...";
    }
}