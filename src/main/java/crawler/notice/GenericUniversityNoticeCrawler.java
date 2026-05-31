package crawler.notice;

import crawler.core.AbstractCrawler;
import crawler.model.NoticeItem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GenericUniversityNoticeCrawler extends AbstractCrawler<NoticeItem> {

    private final int maxCount;
    private final List<String> keywords;

    public GenericUniversityNoticeCrawler(String url, int maxCount, String... keywords) {
        super(url);
        this.maxCount = maxCount;
        this.keywords = Arrays.asList(keywords);
    }

    @Override
    protected List<NoticeItem> parse(String html) {
        List<NoticeItem> result = new ArrayList<>();
        Set<String> usedLinks = new HashSet<>();

        Document doc = Jsoup.parse(html, url);
        Elements links = doc.select("a[href]");

        int count = 0;

        for (Element linkElement : links) {
            if (count >= maxCount) {
                break;
            }

            String title = linkElement.text().trim();
            String link = linkElement.absUrl("href");

            if (!isValidTitle(title) || link.isEmpty()) {
                continue;
            }

            if (usedLinks.contains(link)) {
                continue;
            }

            usedLinks.add(link);

            String date = findDateNearElement(linkElement);
            String summary;

            try {
                summary = fetchSummary(link);
            } catch (Exception e) {
                summary = "摘要抓取失败";
            }

            NoticeItem item = new NoticeItem(title, date, link, summary);
            result.add(item);
            count++;
        }

        return result;
    }

    private boolean isValidTitle(String title) {
        if (title == null || title.isBlank()) {
            return false;
        }

        if (title.length() < 5) {
            return false;
        }

        for (String keyword : keywords) {
            if (title.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

    private String findDateNearElement(Element element) {
        Element current = element;

        for (int i = 0; i < 5 && current != null; i++) {
            String date = extractDate(current.text());

            if (!date.isEmpty()) {
                return date;
            }

            current = current.parent();
        }

        return "未知日期";
    }

    private String extractDate(String text) {
        if (text == null) {
            return "";
        }

        String[] patterns = {
                "\\d{4}-\\d{1,2}-\\d{1,2}",
                "\\d{4}/\\d{1,2}/\\d{1,2}",
                "\\d{4}\\.\\d{1,2}\\.\\d{1,2}",
                "\\d{4}\\s+\\d{1,2}\\s+\\d{1,2}",
                "\\d{1,2}-\\d{1,2}",
                "\\d{1,2}\\.\\d{1,2}"
        };

        for (String pattern : patterns) {
            Matcher matcher = Pattern.compile(pattern).matcher(text);

            if (matcher.find()) {
                String raw = matcher.group().trim();

                if (raw.matches("\\d{4}\\s+\\d{1,2}\\s+\\d{1,2}")) {
                    return raw.replaceAll("\\s+", "-");
                }

                if (raw.matches("\\d{1,2}-\\d{1,2}") || raw.matches("\\d{1,2}\\.\\d{1,2}")) {
                    return Year.now().getValue() + "-" + raw.replace(".", "-");
                }

                return raw;
            }
        }

        return "";
    }

    private String fetchSummary(String detailUrl) {
        String detailHtml = fetch(detailUrl);
        Document doc = Jsoup.parse(detailHtml, detailUrl);

        Elements paragraphs = doc.select("p");
        StringBuilder sb = new StringBuilder();

        for (Element p : paragraphs) {
            String text = p.text().trim();

            if (!text.isEmpty() && text.length() > 8) {
                sb.append(text).append(" ");
            }

            if (sb.length() > 140) {
                break;
            }
        }

        String summary = sb.toString().trim();

        if (summary.isEmpty()) {
            return "无摘要";
        }

        return summary.length() > 140 ? summary.substring(0, 140) + "..." : summary;
    }
}