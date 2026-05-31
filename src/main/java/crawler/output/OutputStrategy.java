package crawler.output;

import java.util.List;

public interface OutputStrategy<T> {
    void output(List<T> data);
}
