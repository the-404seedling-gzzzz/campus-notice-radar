package crawler.notice;

public class UstcNoticeCrawler extends GenericUniversityNoticeCrawler {

    public UstcNoticeCrawler(int maxCount) {
        super(
                "https://www.ustc.edu.cn/tzgg.htm",
                maxCount,
                "通知", "公告", "公示", "关于", "会议", "安排", "邀请函", "活动", "领取", "治理"
        );
    }
}