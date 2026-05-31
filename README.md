# 高校公告雷达 Campus Notice Radar

一个基于 Java 的多高校通知公告聚合爬虫系统。  
项目支持爬取多个高校官网通知公告，并提供网页端展示、关键词筛选、热词统计、收藏、历史记录、日志记录和异常处理等功能。

## 功能特点

- 多高校通知公告爬取
- 单校爬取 / 一键扫描全部学校
- 网页端展示
- 表格视图 / 卡片视图切换
- 关键词筛选与高亮
- 公告热词统计
- 收藏公告
- 历史记录
- 日志记录
- 网络异常重试

## 技术栈

- Java
- Maven
- Jsoup
- Java HttpServer
- Java Logger
- HTML / CSS / JavaScript

## 已接入高校

- 湖南大学
- 中南大学
- 清华大学
- 北京大学
- 上海交通大学
- 复旦大学
- 浙江大学
- 东南大学
- 武汉大学
- 西安交通大学
- 哈尔滨工业大学
- 中国科学技术大学

## 项目结构

```text
src/main/java/crawler
├── core        # 爬虫接口与抽象类
├── model       # 公告数据模型
├── notice      # 各高校爬虫实现
├── service     # 工厂、历史记录、收藏服务
├── output      # 控制台与 JSON 输出
├── util        # 日志与工具类
├── CrawlerApp.java
└── CrawlerWebApp.java
```

## 运行方式

### 1. 使用 IDEA 打开项目

确保 Maven 依赖加载成功。

### 2. 运行网页端

运行：

```text
crawler.CrawlerWebApp
```

浏览器打开：

```text
http://localhost:8080
```

### 3. 运行控制台端

运行：

```text
crawler.CrawlerApp
```

## 核心设计

- `Crawler<T>`：爬虫接口
- `AbstractCrawler<T>`：抽象爬虫类，封装网页请求、重试和日志
- `GenericUniversityNoticeCrawler`：通用高校公告爬虫
- `SchoolCrawlerFactory`：工厂类，负责创建不同学校爬虫
- `NoticeItem`：公告数据模型
- `OutputStrategy<T>`：输出策略接口

## 异常处理

项目处理了以下常见异常：

- 网络请求失败
- 用户输入非法
- 文件读写失败
- 未知学校编号
- 端口占用
- 页面结构变化

## 运行数据

运行时可能生成：

```text
logs/
crawl_history.jsonl
favorite_notice.jsonl
```

这些是本地运行数据，不建议上传到 GitHub。

## 项目声明

本项目仅用于 Java 课程设计和学习实践，不用于商业用途，不进行高频访问。

