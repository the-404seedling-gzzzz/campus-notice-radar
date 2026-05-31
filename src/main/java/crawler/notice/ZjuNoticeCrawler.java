package crawler.notice;

public class ZjuNoticeCrawler extends GenericUniversityNoticeCrawler {

    public ZjuNoticeCrawler(int maxCount) {
        super(
                "https://zdzsc.zju.edu.cn/zxgg/list.htm",
                maxCount,
                "通知", "公告", "公示", "关于", "招生", "简章", "名单", "录取", "测试", "安排"
        );
    }
}