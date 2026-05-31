package crawler.notice;

public class TsinghuaNoticeCrawler extends GenericUniversityNoticeCrawler {

    public TsinghuaNoticeCrawler(int maxCount) {
        super(
                "https://www.tsinghua.edu.cn/rsc/tzgg.htm",
                maxCount,
                "通知", "公告", "公示", "关于", "招聘", "认定", "名单", "安排", "申报", "资格"
        );
    }

    public TsinghuaNoticeCrawler(String url, int maxCount) {
        super(
                url,
                maxCount,
                "通知", "公告", "公示", "关于", "招聘", "认定", "名单", "安排", "申报", "资格"
        );
    }
}