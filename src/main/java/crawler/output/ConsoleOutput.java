package crawler.output;

import java.util.List;

public class ConsoleOutput<T> implements OutputStrategy<T> {

    @Override
    public void output(List<T> data) {
        System.out.println("===== 控制台输出 =====");
        for (T item : data) {
            System.out.println(item);
        }
    }
}
