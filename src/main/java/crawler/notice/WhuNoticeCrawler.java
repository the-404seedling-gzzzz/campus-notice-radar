package crawler.notice;

public class WhuNoticeCrawler extends GenericUniversityNoticeCrawler {

    public WhuNoticeCrawler(int maxCount) {
        super(
                "https://www.whu.edu.cn/tzgg/249.htm",
                maxCount,
                "通知", "公告", "公示", "关于", "讲坛", "试运行", "安排", "活动", "招聘", "会议"
        );
    }
}