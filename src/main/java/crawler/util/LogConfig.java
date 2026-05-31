package crawler.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.Formatter;

public class LogConfig {

    private static boolean initialized = false;

    private LogConfig() {
    }

    public static void init() {
        if (initialized) {
            return;
        }

        try {
            Files.createDirectories(Paths.get("logs"));

            Logger rootLogger = Logger.getLogger("");

            for (Handler handler : rootLogger.getHandlers()) {
                rootLogger.removeHandler(handler);
            }

            rootLogger.setLevel(Level.INFO);

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.INFO);
            consoleHandler.setEncoding(StandardCharsets.UTF_8.name());
            consoleHandler.setFormatter(new CrawlerLogFormatter());

            FileHandler fileHandler = new FileHandler("logs/crawler_app.log", true);
            fileHandler.setLevel(Level.INFO);
            fileHandler.setEncoding(StandardCharsets.UTF_8.name());
            fileHandler.setFormatter(new CrawlerLogFormatter());

            rootLogger.addHandler(consoleHandler);
            rootLogger.addHandler(fileHandler);

            initialized = true;

        } catch (IOException e) {
            System.out.println("日志系统初始化失败：" + e.getMessage());
        }
    }

    private static class CrawlerLogFormatter extends Formatter {

        @Override
        public String format(LogRecord record) {
            LocalDateTime time = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(record.getMillis()),
                    ZoneId.systemDefault()
            );

            String timeText = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            StringBuilder sb = new StringBuilder();

            sb.append("[")
                    .append(timeText)
                    .append("] ");

            sb.append("[")
                    .append(record.getLevel().getName())
                    .append("] ");

            sb.append("[")
                    .append(record.getLoggerName())
                    .append("] ");

            sb.append(formatMessage(record))
                    .append(System.lineSeparator());

            if (record.getThrown() != null) {
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                record.getThrown().printStackTrace(printWriter);
                sb.append(stringWriter);
            }

            return sb.toString();
        }
    }
}