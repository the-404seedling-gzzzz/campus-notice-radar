package crawler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import crawler.core.Crawler;
import crawler.model.NoticeItem;
import crawler.service.FavoriteService;
import crawler.service.HistoryService;
import crawler.service.SchoolCrawlerFactory;

import crawler.util.LogConfig;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CrawlerWebApp {

    private static final Logger logger = Logger.getLogger(CrawlerWebApp.class.getName());

    public static void main(String[] args) {
        LogConfig.init();

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            server.createContext("/", CrawlerWebApp::handleHomePage);
            server.createContext("/api/crawl", CrawlerWebApp::handleCrawlApi);
            server.createContext("/api/favorite", CrawlerWebApp::handleFavoriteApi);
            server.createContext("/api/favorites", CrawlerWebApp::handleFavoritesApi);
            server.createContext("/api/history", CrawlerWebApp::handleHistoryApi);

            server.setExecutor(null);
            server.start();

            logger.info("高校公告雷达启动成功，访问地址：http://localhost:8080");

            System.out.println("高校公告雷达已启动！");
            System.out.println("请在浏览器打开：http://localhost:8080");

        } catch (IOException e) {
            logger.log(Level.SEVERE, "服务器启动失败，可能是 8080 端口被占用", e);
            System.out.println("服务器启动失败，可能是 8080 端口被占用。");
            System.out.println("错误信息：" + e.getMessage());
        }
    }

    private static void handleHomePage(HttpExchange exchange) throws IOException {
        String html = """
                <!DOCTYPE html>
                <html lang="zh-CN">
                <head>
                    <meta charset="UTF-8">
                    <title>高校公告雷达</title>
                    <style>
                        * {
                            box-sizing: border-box;
                        }

                        :root {
                            --bg: #020617;
                            --card: rgba(15, 23, 42, 0.72);
                            --card2: rgba(30, 41, 59, 0.72);
                            --line: rgba(148, 163, 184, 0.18);
                            --text: #e5e7eb;
                            --muted: #94a3b8;
                            --blue: #38bdf8;
                            --purple: #a78bfa;
                            --green: #22c55e;
                            --yellow: #f59e0b;
                            --red: #fb7185;
                        }

                        body {
                            margin: 0;
                            min-height: 100vh;
                            font-family: "Microsoft YaHei", Arial, sans-serif;
                            color: var(--text);
                            background:
                                radial-gradient(circle at 10% 10%, rgba(56, 189, 248, 0.28), transparent 30%),
                                radial-gradient(circle at 90% 20%, rgba(167, 139, 250, 0.25), transparent 28%),
                                radial-gradient(circle at 50% 90%, rgba(34, 197, 94, 0.13), transparent 30%),
                                linear-gradient(135deg, #020617, #0f172a 45%, #111827);
                        }

                        body::before {
                            content: "";
                            position: fixed;
                            inset: 0;
                            background-image:
                                linear-gradient(rgba(148, 163, 184, 0.06) 1px, transparent 1px),
                                linear-gradient(90deg, rgba(148, 163, 184, 0.06) 1px, transparent 1px);
                            background-size: 36px 36px;
                            pointer-events: none;
                            mask-image: linear-gradient(to bottom, black, transparent 90%);
                        }

                        .shell {
                            width: min(1220px, calc(100% - 40px));
                            margin: 28px auto;
                            position: relative;
                            z-index: 1;
                        }

                        .hero {
                            border: 1px solid var(--line);
                            background: linear-gradient(135deg, rgba(15, 23, 42, 0.86), rgba(30, 41, 59, 0.65));
                            backdrop-filter: blur(18px);
                            border-radius: 26px;
                            padding: 30px;
                            box-shadow: 0 24px 70px rgba(0, 0, 0, 0.35);
                            overflow: hidden;
                            position: relative;
                        }

                        .hero::after {
                            content: "";
                            position: absolute;
                            width: 260px;
                            height: 260px;
                            right: -80px;
                            top: -100px;
                            background: radial-gradient(circle, rgba(56, 189, 248, 0.28), transparent 65%);
                            pointer-events: none;
                        }

                        .title-row {
                            display: flex;
                            justify-content: space-between;
                            align-items: flex-start;
                            gap: 20px;
                        }

                        .brand {
                            display: flex;
                            align-items: center;
                            gap: 16px;
                        }

                        .logo {
                            width: 58px;
                            height: 58px;
                            border-radius: 18px;
                            display: grid;
                            place-items: center;
                            background: linear-gradient(135deg, rgba(56,189,248,.95), rgba(167,139,250,.95));
                            box-shadow: 0 0 35px rgba(56, 189, 248, 0.35);
                            font-size: 28px;
                        }

                        h1 {
                            margin: 0;
                            font-size: 34px;
                            letter-spacing: 2px;
                            background: linear-gradient(90deg, #e0f2fe, #93c5fd, #c4b5fd);
                            -webkit-background-clip: text;
                            color: transparent;
                        }

                        .subtitle {
                            color: var(--muted);
                            margin-top: 8px;
                            font-size: 14px;
                        }

                        .badge {
                            padding: 8px 12px;
                            border: 1px solid rgba(56, 189, 248, 0.35);
                            border-radius: 999px;
                            color: #bae6fd;
                            background: rgba(14, 165, 233, 0.12);
                            font-size: 13px;
                            white-space: nowrap;
                        }

                        .control-panel {
                            margin-top: 26px;
                            border: 1px solid var(--line);
                            background: rgba(2, 6, 23, 0.38);
                            border-radius: 20px;
                            padding: 18px;
                        }

                        .setting {
                            display: flex;
                            align-items: center;
                            gap: 12px;
                            flex-wrap: wrap;
                            margin-bottom: 18px;
                        }

                        .setting label {
                            color: #cbd5e1;
                            font-size: 14px;
                        }

                        input {
                            padding: 10px 13px;
                            border: 1px solid rgba(148, 163, 184, 0.32);
                            border-radius: 12px;
                            outline: none;
                            font-size: 14px;
                            color: var(--text);
                            background: rgba(15, 23, 42, 0.72);
                        }

                        input:focus {
                            border-color: var(--blue);
                            box-shadow: 0 0 0 3px rgba(56, 189, 248, 0.14);
                        }

                        #countInput {
                            width: 78px;
                            text-align: center;
                        }

                        #keywordInput {
                            width: 280px;
                        }

                        .panel {
                            display: flex;
                            gap: 10px;
                            flex-wrap: wrap;
                        }

                        button {
                            border: none;
                            border-radius: 12px;
                            padding: 10px 14px;
                            background: linear-gradient(135deg, #2563eb, #0284c7);
                            color: white;
                            cursor: pointer;
                            font-size: 14px;
                            transition: 0.2s;
                            box-shadow: 0 8px 20px rgba(37, 99, 235, 0.18);
                        }

                        button:hover {
                            transform: translateY(-2px);
                            filter: brightness(1.08);
                        }

                        .all-btn {
                            background: linear-gradient(135deg, #16a34a, #059669);
                        }

                        .gray-btn {
                            background: linear-gradient(135deg, #475569, #334155);
                        }

                        .view-btn {
                            background: linear-gradient(135deg, #7c3aed, #4f46e5);
                        }

                        .view-btn.active {
                            outline: 2px solid rgba(196, 181, 253, 0.75);
                            box-shadow: 0 0 25px rgba(167, 139, 250, 0.35);
                        }

                        .favorite-btn {
                            background: linear-gradient(135deg, #f59e0b, #d97706);
                            padding: 7px 10px;
                            font-size: 13px;
                            white-space: nowrap;
                        }

                        .dashboard {
                            display: grid;
                            grid-template-columns: repeat(4, minmax(0, 1fr));
                            gap: 14px;
                            margin-top: 20px;
                        }

                        .stat-card {
                            border: 1px solid var(--line);
                            background: rgba(15, 23, 42, 0.62);
                            border-radius: 18px;
                            padding: 16px;
                            min-width: 0;
                        }

                        .stat-label {
                            color: var(--muted);
                            font-size: 13px;
                        }

                        .stat-value {
                            margin-top: 8px;
                            font-size: 24px;
                            font-weight: bold;
                            color: #e0f2fe;
                            word-break: break-word;
                        }

                        .status {
                            margin-top: 18px;
                            padding: 14px 16px;
                            border: 1px solid var(--line);
                            background: rgba(15, 23, 42, 0.54);
                            border-radius: 16px;
                            color: #cbd5e1;
                            word-break: break-word;
                        }

                        .hotword-box {
                            margin-top: 16px;
                            border: 1px solid var(--line);
                            background: rgba(15, 23, 42, 0.54);
                            border-radius: 16px;
                            padding: 14px 16px;
                        }

                        .hotword-title {
                            color: #bae6fd;
                            font-weight: bold;
                            margin-bottom: 10px;
                        }

                        .hotword-list {
                            display: flex;
                            flex-wrap: wrap;
                            gap: 10px;
                        }

                        .hotword {
                            padding: 6px 10px;
                            border-radius: 999px;
                            background: rgba(56, 189, 248, 0.12);
                            border: 1px solid rgba(56, 189, 248, 0.26);
                            color: #bae6fd;
                            font-size: 13px;
                        }

                        .content-card {
                            margin-top: 18px;
                            border: 1px solid var(--line);
                            background: rgba(2, 6, 23, 0.36);
                            border-radius: 20px;
                            padding: 18px;
                            backdrop-filter: blur(16px);
                            overflow: hidden;
                            width: 100%;
                            max-width: 100%;
                        }

                        table {
                            width: 100%;
                            border-collapse: collapse;
                            font-size: 14px;
                            table-layout: fixed;
                        }

                        th, td {
                            border-bottom: 1px solid rgba(148, 163, 184, 0.15);
                            padding: 13px;
                            text-align: left;
                            vertical-align: top;
                            word-break: break-word;
                            overflow-wrap: anywhere;
                        }

                        th {
                            color: #bae6fd;
                            background: rgba(15, 23, 42, 0.78);
                        }

                        tr:hover {
                            background: rgba(30, 41, 59, 0.48);
                        }

                        a {
                            color: #7dd3fc;
                            text-decoration: none;
                            font-weight: bold;
                            word-break: break-word;
                            overflow-wrap: anywhere;
                        }

                        a:hover {
                            color: #c4b5fd;
                            text-decoration: underline;
                        }

                        .summary {
                            color: var(--muted);
                            font-size: 13px;
                            margin-top: 6px;
                            line-height: 1.6;
                            word-break: break-word;
                            overflow-wrap: anywhere;
                        }

                        .tag {
                            display: inline-block;
                            padding: 4px 9px;
                            background: rgba(14, 165, 233, 0.13);
                            color: #bae6fd;
                            border: 1px solid rgba(56, 189, 248, 0.25);
                            border-radius: 999px;
                            font-size: 12px;
                            white-space: nowrap;
                        }

                        .card-grid {
                            display: grid;
                            grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
                            gap: 16px;
                            width: 100%;
                            max-width: 100%;
                            overflow: hidden;
                        }

                        .notice-card {
                            border: 1px solid rgba(148, 163, 184, 0.18);
                            background:
                                linear-gradient(135deg, rgba(15, 23, 42, 0.88), rgba(30, 41, 59, 0.58));
                            border-radius: 18px;
                            padding: 16px;
                            min-height: 220px;
                            min-width: 0;
                            width: 100%;
                            max-width: 100%;
                            overflow: hidden;
                            word-break: break-word;
                            overflow-wrap: anywhere;
                            display: flex;
                            flex-direction: column;
                            justify-content: space-between;
                            transition: 0.2s;LogConfig
                        }

                        .notice-card:hover {
                            transform: translateY(-4px);
                            border-color: rgba(56, 189, 248, 0.45);
                            box-shadow: 0 16px 32px rgba(0, 0, 0, 0.22);
                        }

                        .notice-card-title {
                            margin-top: 12px;
                            line-height: 1.55;
                            font-size: 15px;
                            min-width: 0;
                            word-break: break-word;
                            overflow-wrap: anywhere;
                        }

                        .notice-card-title a {
                            word-break: break-word;
                            overflow-wrap: anywhere;
                        }

                        .notice-card-meta {
                            display: flex;
                            justify-content: space-between;
                            gap: 12px;
                            color: var(--muted);
                            font-size: 13px;
                            margin-top: 10px;
                            flex-wrap: wrap;
                        }

                        .notice-card-footer {
                            display: flex;
                            justify-content: space-between;
                            align-items: center;
                            gap: 10px;
                            margin-top: 14px;
                            flex-wrap: wrap;
                        }

                        .empty {
                            text-align: center;
                            color: #94a3b8;
                            padding: 28px;
                            grid-column: 1 / -1;
                        }

                        .hidden {
                            display: none !important;
                        }

                        .footer {
                            text-align: center;
                            color: #64748b;
                            font-size: 13px;
                            margin-top: 24px;
                        }

                        @media (max-width: 1100px) {
                            .dashboard {
                                grid-template-columns: repeat(2, minmax(0, 1fr));
                            }

                            .card-grid {
                                grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
                            }
                        }

                        @media (max-width: 760px) {
                            .shell {
                                width: min(100% - 20px, 1220px);
                            }

                            .hero {
                                padding: 20px;
                            }

                            .title-row {
                                flex-direction: column;
                            }

                            .brand {
                                align-items: flex-start;
                            }

                            h1 {
                                font-size: 28px;
                            }

                            .dashboard {
                                grid-template-columns: 1fr;
                            }

                            .card-grid {
                                grid-template-columns: 1fr;
                            }

                            #keywordInput {
                                width: 100%;
                            }

                            table {
                                font-size: 13px;
                            }

                            th, td {
                                padding: 9px;
                            }
                        }
                    </style>
                </head>
                <body>
                    <div class="shell">
                        <div class="hero">
                            <div class="title-row">
                                <div class="brand">
                                    <div class="logo">📡</div>
                                    <div>
                                        <h1>高校公告雷达</h1>
                                        <div class="subtitle">
                                            多所 985 高校通知公告聚合 · 关键词筛选 · 热词分析 · 收藏追踪
                                        </div>
                                    </div>
                                </div>
                                <div class="badge">Java Web 本地版 · localhost:8080</div>
                            </div>

                            <div class="control-panel">
                                <div class="setting">
                                    <label>爬取条数</label>
                                    <input id="countInput" type="number" value="10" min="1" max="30">

                                    <label>关键词</label>
                                    <input id="keywordInput" type="text" placeholder="例如：考试、招生、竞赛、报名">

                                    <button class="gray-btn" onclick="clearKeyword()">清空关键词</button>
                                    <button id="tableBtn" class="view-btn active" onclick="setViewMode('table')">表格视图</button>
                                    <button id="cardBtn" class="view-btn" onclick="setViewMode('card')">卡片视图</button>
                                </div>

                                <div class="panel">
                                    <button onclick="crawl('hnu')">湖南大学</button>
                                    <button onclick="crawl('csu')">中南大学</button>
                                    <button onclick="crawl('tsinghua')">清华大学</button>
                                    <button onclick="crawl('pku')">北京大学</button>
                                    <button onclick="crawl('sjtu')">上海交通大学</button>
                                    <button onclick="crawl('fudan')">复旦大学</button>
                                    <button onclick="crawl('zju')">浙江大学</button>
                                    <button onclick="crawl('whu')">武汉大学</button>
                                    <button onclick="crawl('xjtu')">西安交通大学</button>
                                    <button onclick="crawl('hit')">哈尔滨工业大学</button>
                                    <button onclick="crawl('ustc')">中国科学技术大学</button>
                                    <button class="all-btn" onclick="crawl('all')">一键扫描全部</button>
                                    <button class="gray-btn" onclick="showHistory()">查看历史</button>
                                    <button class="gray-btn" onclick="showFavorites()">查看收藏</button>
                                </div>
                            </div>

                            <div class="dashboard">
                                <div class="stat-card">
                                    <div class="stat-label">当前公告数量</div>
                                    <div id="totalCount" class="stat-value">0</div>
                                </div>
                                <div class="stat-card">
                                    <div class="stat-label">涉及学校数量</div>
                                    <div id="schoolCount" class="stat-value">0</div>
                                </div>
                                <div class="stat-card">
                                    <div class="stat-label">当前关键词</div>
                                    <div id="keywordStat" class="stat-value">无</div>
                                </div>
                                <div class="stat-card">
                                    <div class="stat-label">最热词</div>
                                    <div id="topHotword" class="stat-value">暂无</div>
                                </div>
                            </div>

                            <div id="status" class="status">
                                雷达待命中：请选择学校开始扫描公告。
                            </div>

                            <div class="hotword-box">
                                <div class="hotword-title">公告热词统计</div>
                                <div id="hotwordList" class="hotword-list">
                                    <span class="hotword">暂无数据</span>
                                </div>
                            </div>

                            <div class="content-card">
                                <div id="tableView">
                                    <table>
                                        <thead>
                                            <tr>
                                                <th width="120">学校</th>
                                                <th width="55">序号</th>
                                                <th>公告信息</th>
                                                <th width="120">日期</th>
                                                <th width="90">操作</th>
                                            </tr>
                                        </thead>
                                        <tbody id="resultBody">
                                            <tr>
                                                <td colspan="5" class="empty">暂无数据，请点击上方按钮开始扫描。</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>

                                <div id="cardView" class="card-grid hidden"></div>
                            </div>

                            <div class="footer">
                                本系统仅用于 Java 课程设计学习演示，不进行高频访问。
                            </div>
                        </div>
                    </div>

                    <script>
                        let currentItems = [];
                        let currentViewMode = "table";
                        let currentShowFavoriteButton = true;

                        async function crawl(school) {
                            const count = document.getElementById("countInput").value || 10;
                            const keyword = document.getElementById("keywordInput").value || "";
                            const status = document.getElementById("status");

                            status.innerText = "雷达扫描中：正在连接高校公告页面，请稍等...";
                            currentItems = [];
                            updateDashboard([], keyword);
                            renderNoticeItems([], true, "正在加载数据...");

                            try {
                                const response = await fetch(`/api/crawl?school=${school}&count=${count}&keyword=${encodeURIComponent(keyword)}`);
                                const data = await response.json();

                                if (!data.success) {
                                    status.innerText = "扫描失败：" + data.message;
                                    renderNoticeItems([], true, "扫描失败，请检查网络或网站结构。");
                                    return;
                                }

                                currentItems = data.items;
                                currentShowFavoriteButton = true;

                                status.innerText = `扫描完成：共获取 ${data.items.length} 条公告，用时 ${data.cost} 毫秒`;
                                updateDashboard(data.items, keyword);
                                renderNoticeItems(data.items, true);

                            } catch (e) {
                                status.innerText = "请求失败：请检查 Java 程序是否正在运行。";
                                renderNoticeItems([], true, "请求失败。");
                            }
                        }

                        function setViewMode(mode) {
                            currentViewMode = mode;

                            document.getElementById("tableBtn").classList.toggle("active", mode === "table");
                            document.getElementById("cardBtn").classList.toggle("active", mode === "card");

                            document.getElementById("tableView").classList.toggle("hidden", mode !== "table");
                            document.getElementById("cardView").classList.toggle("hidden", mode !== "card");

                            renderNoticeItems(currentItems, currentShowFavoriteButton);
                        }

                        function renderNoticeItems(items, showFavoriteButton, emptyText) {
                            const resultBody = document.getElementById("resultBody");
                            const cardView = document.getElementById("cardView");

                            resultBody.innerHTML = "";
                            cardView.innerHTML = "";

                            if (!items || items.length === 0) {
                                const text = emptyText || "暂无数据";
                                resultBody.innerHTML = `<tr><td colspan="5" class="empty">${escapeHtml(text)}</td></tr>`;
                                cardView.innerHTML = `<div class="empty">${escapeHtml(text)}</div>`;
                                return;
                            }

                            items.forEach((item, index) => {
                                const action = showFavoriteButton
                                    ? `<button class="favorite-btn" onclick="addFavorite(${index})">收藏</button>`
                                    : "-";

                                const tr = document.createElement("tr");
                                tr.innerHTML = `
                                    <td><span class="tag">${escapeHtml(item.school || "未知")}</span></td>
                                    <td>${index + 1}</td>
                                    <td>
                                        <a href="${escapeAttr(item.link)}" target="_blank">${highlightKeyword(item.title)}</a>
                                        <div class="summary">${highlightKeyword(item.summary || "无摘要")}</div>
                                    </td>
                                    <td>${escapeHtml(item.date || "未知日期")}</td>
                                    <td>${action}</td>
                                `;
                                resultBody.appendChild(tr);

                                const card = document.createElement("div");
                                card.className = "notice-card";
                                card.innerHTML = `
                                    <div>
                                        <span class="tag">${escapeHtml(item.school || "未知")}</span>
                                        <div class="notice-card-title">
                                            <a href="${escapeAttr(item.link)}" target="_blank">${highlightKeyword(item.title)}</a>
                                        </div>
                                        <div class="summary">${highlightKeyword(item.summary || "无摘要")}</div>
                                    </div>
                                    <div>
                                        <div class="notice-card-meta">
                                            <span>序号：${index + 1}</span>
                                            <span>日期：${escapeHtml(item.date || "未知日期")}</span>
                                        </div>
                                        <div class="notice-card-footer">
                                            <a href="${escapeAttr(item.link)}" target="_blank">查看原文</a>
                                            <div>${action}</div>
                                        </div>
                                    </div>
                                `;
                                cardView.appendChild(card);
                            });
                        }

                        function updateDashboard(items, keyword) {
                            document.getElementById("totalCount").innerText = items.length;

                            const schoolSet = new Set();
                            items.forEach(item => {
                                if (item.school) {
                                    schoolSet.add(item.school);
                                }
                            });

                            document.getElementById("schoolCount").innerText = schoolSet.size;
                            document.getElementById("keywordStat").innerText = keyword && keyword.trim() ? keyword.trim() : "无";

                            const hotwords = calculateHotwords(items);
                            renderHotwords(hotwords);

                            document.getElementById("topHotword").innerText =
                                hotwords.length > 0 ? hotwords[0].word : "暂无";
                        }

                        function calculateHotwords(items) {
                            const candidates = [
                                "通知", "公告", "公示", "关于", "招生", "考试", "报名", "申请",
                                "竞赛", "项目", "讲座", "论坛", "招聘", "就业", "实习", "科研",
                                "申报", "选课", "名单", "安排", "会议", "活动", "录取", "研究生",
                                "本科生", "奖学金", "创新", "创业", "资格", "材料", "截止"
                            ];

                            const counter = {};

                            items.forEach(item => {
                                const text = `${item.title || ""} ${item.summary || ""}`;
                                candidates.forEach(word => {
                                    let count = 0;
                                    let index = text.indexOf(word);

                                    while (index !== -1) {
                                        count++;
                                        index = text.indexOf(word, index + word.length);
                                    }

                                    if (count > 0) {
                                        counter[word] = (counter[word] || 0) + count;
                                    }
                                });
                            });

                            return Object.keys(counter)
                                .map(word => ({ word, count: counter[word] }))
                                .sort((a, b) => b.count - a.count)
                                .slice(0, 12);
                        }

                        function renderHotwords(hotwords) {
                            const hotwordList = document.getElementById("hotwordList");
                            hotwordList.innerHTML = "";

                            if (!hotwords || hotwords.length === 0) {
                                hotwordList.innerHTML = `<span class="hotword">暂无数据</span>`;
                                return;
                            }

                            hotwords.forEach(item => {
                                const span = document.createElement("span");
                                span.className = "hotword";
                                span.innerText = `${item.word} · ${item.count}`;
                                hotwordList.appendChild(span);
                            });
                        }

                        async function addFavorite(index) {
                            const item = currentItems[index];

                            if (!item) {
                                alert("收藏失败：没有找到该公告。");
                                return;
                            }

                            const response = await fetch("/api/favorite", {
                                method: "POST",
                                headers: {
                                    "Content-Type": "application/json"
                                },
                                body: JSON.stringify(item)
                            });

                            const result = await response.json();

                            if (result.success) {
                                alert("收藏成功！");
                            } else {
                                alert("收藏失败：" + result.message);
                            }
                        }

                        async function showHistory() {
                            const status = document.getElementById("status");
                            const resultBody = document.getElementById("resultBody");
                            const cardView = document.getElementById("cardView");

                            status.innerText = "历史扫描记录如下：";
                            currentItems = [];
                            currentShowFavoriteButton = false;
                            updateDashboard([], "");

                            resultBody.innerHTML = `<tr><td colspan="5" class="empty">正在加载历史记录...</td></tr>`;
                            cardView.innerHTML = `<div class="empty">正在加载历史记录...</div>`;

                            try {
                                const response = await fetch("/api/history");
                                const data = await response.json();

                                resultBody.innerHTML = "";
                                cardView.innerHTML = "";

                                if (!data.items || data.items.length === 0) {
                                    resultBody.innerHTML = `<tr><td colspan="5" class="empty">暂无历史记录</td></tr>`;
                                    cardView.innerHTML = `<div class="empty">暂无历史记录</div>`;
                                    return;
                                }

                                data.items.forEach((item, index) => {
                                    const tr = document.createElement("tr");
                                    tr.innerHTML = `
                                        <td><span class="tag">历史</span></td>
                                        <td>${index + 1}</td>
                                        <td>
                                            ${escapeHtml(item.time)} 扫描了 ${escapeHtml(item.school)}
                                            <div class="summary">
                                                关键词：${escapeHtml(item.keyword || "无")}，
                                                目标条数：${item.count}，
                                                结果：${item.resultCount} 条，
                                                耗时：${item.cost} 毫秒
                                            </div>
                                        </td>
                                        <td>-</td>
                                        <td>-</td>
                                    `;
                                    resultBody.appendChild(tr);

                                    const card = document.createElement("div");
                                    card.className = "notice-card";
                                    card.innerHTML = `
                                        <div>
                                            <span class="tag">历史</span>
                                            <div class="notice-card-title">${escapeHtml(item.school)}</div>
                                            <div class="summary">
                                                时间：${escapeHtml(item.time)}<br>
                                                关键词：${escapeHtml(item.keyword || "无")}<br>
                                                目标条数：${item.count}，结果：${item.resultCount} 条<br>
                                                耗时：${item.cost} 毫秒
                                            </div>
                                        </div>
                                    `;
                                    cardView.appendChild(card);
                                });

                            } catch (e) {
                                resultBody.innerHTML = `<tr><td colspan="5" class="empty">历史记录读取失败。</td></tr>`;
                                cardView.innerHTML = `<div class="empty">历史记录读取失败。</div>`;
                            }
                        }

                        async function showFavorites() {
                            const status = document.getElementById("status");

                            status.innerText = "我的收藏如下：";
                            currentItems = [];
                            currentShowFavoriteButton = false;
                            updateDashboard([], "");
                            renderNoticeItems([], false, "正在加载收藏...");

                            try {
                                const response = await fetch("/api/favorites");
                                const data = await response.json();

                                if (!data.items || data.items.length === 0) {
                                    renderNoticeItems([], false, "暂无收藏");
                                    return;
                                }

                                currentItems = data.items;
                                updateDashboard(data.items, "");
                                renderNoticeItems(data.items, false);

                            } catch (e) {
                                renderNoticeItems([], false, "收藏读取失败。");
                            }
                        }

                        function clearKeyword() {
                            document.getElementById("keywordInput").value = "";
                        }

                        function highlightKeyword(text) {
                            const keyword = document.getElementById("keywordInput").value.trim();

                            let safeText = escapeHtml(text || "");

                            if (!keyword) {
                                return safeText;
                            }

                            const safeKeyword = escapeHtml(keyword);

                            return safeText.replaceAll(
                                safeKeyword,
                                `<span style="background: rgba(245, 158, 11, 0.35); color: #fde68a; border-radius: 4px; padding: 0 3px;">${safeKeyword}</span>`
                            );
                        }

                        function escapeHtml(text) {
                            if (text === null || text === undefined) {
                                return "";
                            }

                            return String(text)
                                .replaceAll("&", "&amp;")
                                .replaceAll("<", "&lt;")
                                .replaceAll(">", "&gt;")
                                .replaceAll('"', "&quot;")
                                .replaceAll("'", "&#039;");
                        }

                        function escapeAttr(text) {
                            return escapeHtml(text);
                        }
                    </script>
                </body>
                </html>
                """;

        sendResponse(exchange, html, "text/html; charset=UTF-8");
    }

    private static void handleCrawlApi(HttpExchange exchange) throws IOException {
        long start = System.currentTimeMillis();

        try {
            String query = exchange.getRequestURI().getQuery();

            String school = getQueryParam(query, "school", "hnu");
            int count = parseCount(getQueryParam(query, "count", "10"));
            String keyword = getQueryParam(query, "keyword", "");

            List<SchoolNotice> result = new ArrayList<>();

            if ("all".equalsIgnoreCase(school)) {
                for (SchoolCrawlerFactory.SchoolOption option : SchoolCrawlerFactory.getAllSchools()) {
                    crawlAndAdd(
                            result,
                            option.getName(),
                            option.createCrawler(count),
                            keyword
                    );
                }

                long cost = System.currentTimeMillis() - start;
                HistoryService.add("全部学校", count, keyword, result.size(), cost);

                String json = "{"
                        + "\"success\":true,"
                        + "\"cost\":" + cost + ","
                        + "\"items\":" + schoolNoticeListToJson(result)
                        + "}";

                sendResponse(exchange, json, "application/json; charset=UTF-8");
            } else {
                SchoolCrawlerFactory.SchoolOption option = SchoolCrawlerFactory.getSchool(school);

                crawlAndAdd(
                        result,
                        option.getName(),
                        option.createCrawler(count),
                        keyword
                );

                long cost = System.currentTimeMillis() - start;
                HistoryService.add(option.getName(), count, keyword, result.size(), cost);

                String json = "{"
                        + "\"success\":true,"
                        + "\"cost\":" + cost + ","
                        + "\"items\":" + schoolNoticeListToJson(result)
                        + "}";

                sendResponse(exchange, json, "application/json; charset=UTF-8");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "爬取接口处理失败", e);

            String json = "{"
                    + "\"success\":false,"
                    + "\"message\":\"" + escapeJson(e.getMessage()) + "\","
                    + "\"items\":[]"
                    + "}";

            sendResponse(exchange, json, "application/json; charset=UTF-8");
        }
    }

    private static void crawlAndAdd(List<SchoolNotice> result,
                                    String schoolName,
                                    Crawler<NoticeItem> crawler,
                                    String keyword) {
        try {
            logger.info("开始爬取：" + schoolName + "，关键词：" + formatKeyword(keyword));

            List<NoticeItem> items = crawler.crawl();
            items = filterByKeyword(items, keyword);

            for (NoticeItem item : items) {
                result.add(new SchoolNotice(schoolName, item));
            }

            logger.info(schoolName + " 爬取完成，结果数量：" + items.size());

        } catch (Exception e) {
            logger.log(Level.WARNING, schoolName + " 爬取失败", e);
            System.out.println(schoolName + " 爬取失败：" + e.getMessage());
        }
    }

    private static String formatKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return "无";
        }

        return keyword;
    }

    private static List<NoticeItem> filterByKeyword(List<NoticeItem> items, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return items;
        }

        List<NoticeItem> result = new ArrayList<>();

        for (NoticeItem item : items) {
            String title = item.getTitle() == null ? "" : item.getTitle();
            String summary = item.getSummary() == null ? "" : item.getSummary();

            if (title.contains(keyword) || summary.contains(keyword)) {
                result.add(item);
            }
        }

        return result;
    }

    private static void handleFavoriteApi(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, "{\"success\":false,\"message\":\"只支持POST请求\"}", "application/json; charset=UTF-8");
            return;
        }

        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        FavoriteService.addRawJson(body);

        logger.info("用户收藏了一条公告");

        sendResponse(exchange, "{\"success\":true,\"message\":\"收藏成功\"}", "application/json; charset=UTF-8");
    }

    private static void handleFavoritesApi(HttpExchange exchange) throws IOException {
        String json = "{"
                + "\"success\":true,"
                + "\"items\":" + FavoriteService.readAsJsonArray()
                + "}";

        sendResponse(exchange, json, "application/json; charset=UTF-8");
    }

    private static void handleHistoryApi(HttpExchange exchange) throws IOException {
        String json = "{"
                + "\"success\":true,"
                + "\"items\":" + HistoryService.readAsJsonArray()
                + "}";

        sendResponse(exchange, json, "application/json; charset=UTF-8");
    }

    private static String schoolNoticeListToJson(List<SchoolNotice> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < items.size(); i++) {
            SchoolNotice schoolNotice = items.get(i);
            NoticeItem item = schoolNotice.noticeItem;

            sb.append("{")
                    .append("\"school\":\"").append(escapeJson(schoolNotice.schoolName)).append("\",")
                    .append("\"title\":\"").append(escapeJson(item.getTitle())).append("\",")
                    .append("\"date\":\"").append(escapeJson(item.getDate())).append("\",")
                    .append("\"link\":\"").append(escapeJson(item.getLink())).append("\",")
                    .append("\"summary\":\"").append(escapeJson(item.getSummary())).append("\"")
                    .append("}");

            if (i < items.size() - 1) {
                sb.append(",");
            }
        }

        sb.append("]");
        return sb.toString();
    }

    private static String getQueryParam(String query, String key, String defaultValue) {
        if (query == null || query.isBlank()) {
            return defaultValue;
        }

        String[] pairs = query.split("&");

        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);

            if (kv.length == 2 && kv[0].equals(key)) {
                return URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
            }
        }

        return defaultValue;
    }

    private static int parseCount(String value) {
        try {
            int count = Integer.parseInt(value);

            if (count <= 0) {
                return 10;
            }

            return Math.min(count, 30);
        } catch (NumberFormatException e) {
            return 10;
        }
    }

    private static String escapeJson(String text) {
        if (text == null) {
            return "";
        }

        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "")
                .replace("\r", "");
    }

    private static void sendResponse(HttpExchange exchange, String response, String contentType) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static class SchoolNotice {
        private final String schoolName;
        private final NoticeItem noticeItem;

        private SchoolNotice(String schoolName, NoticeItem noticeItem) {
            this.schoolName = schoolName;
            this.noticeItem = noticeItem;
        }
    }
}