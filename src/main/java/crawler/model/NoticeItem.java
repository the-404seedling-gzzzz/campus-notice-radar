package crawler.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class NoticeItem {

    @JsonProperty("title")
    private String title;

    @JsonProperty("date")
    private String date;

    @JsonProperty("link")
    private String link;

    @JsonProperty("summary")
    private String summary;

    @JsonProperty("crawled_at")
    private LocalDateTime crawledAt;

    public NoticeItem() {
        this.crawledAt = LocalDateTime.now();
    }

    public NoticeItem(String title, String date, String link, String summary) {
        this.title = title;
        this.date = date;
        this.link = link;
        this.summary = summary;
        this.crawledAt = LocalDateTime.now();
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getLink() {
        return link;
    }

    public String getSummary() {
        return summary;
    }

    public LocalDateTime getCrawledAt() {
        return crawledAt;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setCrawledAt(LocalDateTime crawledAt) {
        this.crawledAt = crawledAt;
    }

    @Override
    public String toString() {
        return "NoticeItem{" +
                "title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", link='" + link + '\'' +
                ", summary='" + summary + '\'' +
                ", crawledAt=" + crawledAt +
                '}';
    }
}