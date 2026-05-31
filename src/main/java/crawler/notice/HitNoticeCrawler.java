package crawler.notice;

public class HitNoticeCrawler extends GenericUniversityNoticeCrawler {

    public HitNoticeCrawler(int maxCount) {
        super(
                "https://hitgs.hit.edu.cn/tzgg/list.htm",
                maxCount,
                "通知", "公告", "公示", "关于", "项目", "立项", "考试", "名单", "安排", "申报"
        );
    }
}