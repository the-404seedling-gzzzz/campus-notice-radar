package crawler.core;

import crawler.exception.NetworkException;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractCrawler<T> implements Crawler<T> {

    private static final Logger logger = Logger.getLogger(AbstractCrawler.class.getName());

    private static final int MAX_RETRY_TIMES = 3;
    private static final long RETRY_WAIT_MILLIS = 1500;

    protected String url;

    public AbstractCrawler(String url) {
        this.url = url;
    }

    @Override
    public List<T> crawl() {
        logger.info("开始抓取网页：" + url);

        String html = fetch(url);

        logger.info("网页源码获取成功：" + url);

        return parse(html);
    }

    protected String fetch(String url) {
        IOException lastException = null;

        for (int attempt = 1; attempt <= MAX_RETRY_TIMES; attempt++) {
            try {
                logger.info("正在请求网页，第 " + attempt + " 次：" + url);

                String html = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .header("Accept-Language", "zh-CN,zh;q=0.9")
                        .referrer("https://www.baidu.com/")
                        .timeout(10000)
                        .ignoreContentType(true)
                        .get()
                        .html();

                logger.info("网页请求成功，第 " + attempt + " 次成功：" + url);

                return html;

            } catch (IOException e) {
                lastException = e;

                logger.log(
                        Level.WARNING,
                        "网页请求失败，第 " + attempt + " 次，准备重试：" + url,
                        e
                );

                if (attempt < MAX_RETRY_TIMES) {
                    try {
                        Thread.sleep(RETRY_WAIT_MILLIS);
                    } catch (InterruptedException interruptedException) {
                        Thread.currentThread().interrupt();
                        logger.log(Level.WARNING, "重试等待被中断：" + url, interruptedException);
                        break;
                    }
                }
            }
        }

        logger.log(Level.SEVERE, "网页请求最终失败，已达到最大重试次数：" + url, lastException);

        throw new NetworkException("请求失败，已重试 " + MAX_RETRY_TIMES + " 次: " + url, lastException);
    }

    protected abstract List<T> parse(String html);
}