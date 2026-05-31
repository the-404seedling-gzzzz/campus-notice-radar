package crawler.core;

import java.util.List;

public interface Crawler<T> {
    List<T> crawl();
}
