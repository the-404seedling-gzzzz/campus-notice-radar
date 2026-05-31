package crawler.view;

import crawler.model.NoticeItem;
import crawler.service.SchoolCrawlerFactory;

import java.util.List;
import java.util.Scanner;

public class ConsoleView {

    private final Scanner scanner = new Scanner(System.in);

    public void showWelcome() {
        System.out.println("======================================");
        System.out.println("          高校公告雷达 CLI");
        System.out.println("======================================");
    }

    public void showMenu() {
        System.out.println();
        System.out.println("============== 主菜单 ==============");
        System.out.println("1. 爬取指定学校公告");
        System.out.println("2. 一键爬取全部学校公告");
        System.out.println("3. 导出最近一次爬取结果");
        System.out.println("4. 从 JSON 文件导入公告");
        System.out.println("5. 查看支持的学校");
        System.out.println("0. 退出系统");
        System.out.println("====================================");
    }

    public String readChoice() {
        System.out.print("请输入菜单编号：");
        return scanner.nextLine().trim();
    }

    public String readSchoolCode() {
        System.out.print("请输入学校编号，例如 hnu、csu、pku：");
        return scanner.nextLine().trim();
    }

    public int readCount() {
        System.out.print("请输入爬取条数，直接回车默认 10：");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            return 10;
        }

        try {
            int count = Integer.parseInt(input);
            if (count <= 0) {
                return 10;
            }
            return Math.min(count, 30);
        } catch (NumberFormatException e) {
            return 10;
        }
    }

    public String readKeyword() {
        System.out.print("请输入关键词，直接回车表示不过滤：");
        return scanner.nextLine().trim();
    }

    public String readFilePath(String defaultPath) {
        System.out.print("请输入文件路径，直接回车默认 " + defaultPath + "：");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            return defaultPath;
        }

        return input;
    }

    public void showSupportedSchools() {
        System.out.println("当前支持的学校：");

        for (SchoolCrawlerFactory.SchoolOption option : SchoolCrawlerFactory.getAllSchools()) {
            System.out.println(option.getCode() + " - " + option.getName());
        }
    }

    public void showNotices(List<NoticeItem> notices) {
        if (notices == null || notices.isEmpty()) {
            System.out.println("暂无公告数据。");
            return;
        }

        System.out.println();
        System.out.println("共获取 " + notices.size() + " 条公告：");

        for (int i = 0; i < notices.size(); i++) {
            NoticeItem item = notices.get(i);

            System.out.println("--------------------------------------");
            System.out.println("序号：" + (i + 1));
            System.out.println("标题：" + item.getTitle());
            System.out.println("日期：" + item.getDate());
            System.out.println("链接：" + item.getLink());
            System.out.println("摘要：" + item.getSummary());
            System.out.println("爬取时间：" + item.getCrawledAt());
        }

        System.out.println("--------------------------------------");
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void showError(String message) {
        System.out.println("[错误] " + message);
    }
}