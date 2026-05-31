package crawler.notice;

public class HnuNoticeCrawler extends GenericUniversityNoticeCrawler {

    public HnuNoticeCrawler(int maxCount) {
        super(
                "https://www.hnu.edu.cn/tzgg.htm",
                maxCount,
                "通知", "公告", "公示", "关于", "名单", "安排", "挑战杯", "选拔", "论坛", "招生"
        );
    }

    public HnuNoticeCrawler(String url, int maxCount) {
        super(
                url,
                maxCount,
                "通知", "公告", "公示", "关于", "名单", "安排", "挑战杯", "选拔", "论坛", "招生"
        );
    }
}