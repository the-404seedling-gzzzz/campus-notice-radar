package crawler.notice;

public class FudanNoticeCrawler extends GenericUniversityNoticeCrawler {

    public FudanNoticeCrawler(int maxCount) {
        super(
                "https://news.fudan.edu.cn/45/list.htm",
                maxCount,
                "通知", "公告", "公示", "关于", "招生", "活动", "报名", "安排", "校历", "开放日"
        );
    }
}