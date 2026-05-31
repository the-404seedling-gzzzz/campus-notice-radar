package crawler.notice;

public class PkuNoticeCrawler extends GenericUniversityNoticeCrawler {

    public PkuNoticeCrawler(int maxCount) {
        super(
                "https://dean.pku.edu.cn/web/notice.php",
                maxCount,
                "通知", "公告", "公示", "关于", "报名", "申请", "选课", "排课", "项目", "名单", "安排"
        );
    }

    public PkuNoticeCrawler(String url, int maxCount) {
        super(
                url,
                maxCount,
                "通知", "公告", "公示", "关于", "报名", "申请", "选课", "排课", "项目", "名单", "安排"
        );
    }
}