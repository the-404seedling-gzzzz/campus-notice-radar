package crawler.notice;

public class SjtuNoticeCrawler extends GenericUniversityNoticeCrawler {

    public SjtuNoticeCrawler(int maxCount) {
        super(
                "https://www.sjtu.edu.cn/tg/index.html",
                maxCount,
                "通知", "公告", "公示", "关于", "名单", "安排", "通告", "申报", "征集"
        );
    }
}