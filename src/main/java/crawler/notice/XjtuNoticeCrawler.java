package crawler.notice;

public class XjtuNoticeCrawler extends GenericUniversityNoticeCrawler {

    public XjtuNoticeCrawler(int maxCount) {
        super(
                "https://mob.xjtu.edu.cn/index/tzgg.htm",
                maxCount,
                "通知", "公告", "公示", "关于", "安排", "报名", "讲坛", "活动", "招聘", "温馨提示"
        );
    }
}