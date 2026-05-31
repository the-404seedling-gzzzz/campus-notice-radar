package crawler.service;

import crawler.core.Crawler;
import crawler.model.NoticeItem;
import crawler.notice.CsuNoticeCrawler;
import crawler.notice.FudanNoticeCrawler;
import crawler.notice.HitNoticeCrawler;
import crawler.notice.HnuNoticeCrawler;
import crawler.notice.PkuNoticeCrawler;
import crawler.notice.SjtuNoticeCrawler;
import crawler.notice.TsinghuaNoticeCrawler;
import crawler.notice.UstcNoticeCrawler;
import crawler.notice.WhuNoticeCrawler;
import crawler.notice.XjtuNoticeCrawler;
import crawler.notice.ZjuNoticeCrawler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SchoolCrawlerFactory {

    private static final List<SchoolOption> SCHOOLS = new ArrayList<>();

    static {
        SCHOOLS.add(new SchoolOption("hnu", "湖南大学", HnuNoticeCrawler::new));
        SCHOOLS.add(new SchoolOption("csu", "中南大学", CsuNoticeCrawler::new));
        SCHOOLS.add(new SchoolOption("tsinghua", "清华大学", TsinghuaNoticeCrawler::new));
        SCHOOLS.add(new SchoolOption("pku", "北京大学", PkuNoticeCrawler::new));
        SCHOOLS.add(new SchoolOption("sjtu", "上海交通大学", SjtuNoticeCrawler::new));
        SCHOOLS.add(new SchoolOption("fudan", "复旦大学", FudanNoticeCrawler::new));
        SCHOOLS.add(new SchoolOption("zju", "浙江大学", ZjuNoticeCrawler::new));
        SCHOOLS.add(new SchoolOption("whu", "武汉大学", WhuNoticeCrawler::new));
        SCHOOLS.add(new SchoolOption("xjtu", "西安交通大学", XjtuNoticeCrawler::new));
        SCHOOLS.add(new SchoolOption("hit", "哈尔滨工业大学", HitNoticeCrawler::new));
        SCHOOLS.add(new SchoolOption("ustc", "中国科学技术大学", UstcNoticeCrawler::new));
    }

    private SchoolCrawlerFactory() {
    }

    public static List<SchoolOption> getAllSchools() {
        return SCHOOLS;
    }

    public static SchoolOption getSchool(String code) {
        for (SchoolOption school : SCHOOLS) {
            if (school.getCode().equalsIgnoreCase(code)) {
                return school;
            }
        }

        throw new RuntimeException("未知学校类型：" + code);
    }

    public static class SchoolOption {
        private final String code;
        private final String name;
        private final Function<Integer, Crawler<NoticeItem>> creator;

        public SchoolOption(String code, String name, Function<Integer, Crawler<NoticeItem>> creator) {
            this.code = code;
            this.name = name;
            this.creator = creator;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public Crawler<NoticeItem> createCrawler(int maxCount) {
            return creator.apply(maxCount);
        }
    }
}