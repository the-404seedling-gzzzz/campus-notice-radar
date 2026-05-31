package crawler.notice;

public class CsuNoticeCrawler extends GenericUniversityNoticeCrawler {

    public CsuNoticeCrawler(int maxCount) {
        super(
                "https://rsc.csu.edu.cn/xwgg/tzgg.htm",
                maxCount,
                "通知", "公告", "公示", "关于", "名单", "安排", "招聘", "拟聘", "论坛", "资格审查"
        );
    }

    public CsuNoticeCrawler(String url, int maxCount) {
        super(
                url,
                maxCount,
                "通知", "公告", "公示", "关于", "名单", "安排", "招聘", "拟聘", "论坛", "资格审查"
        );
    }
}