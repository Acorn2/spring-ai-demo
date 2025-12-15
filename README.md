# Spring AI Demo - 知识库问答系统

基于 Spring AI 框架实现的企业级知识库问答（RAG）系统。

## 📋 项目结构

```
spring-ai-demo/
├── pom.xml                    # 父级 POM（多模块管理）
├── knowledge-qa/              # 知识问答子模块
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/example/knowledgeqa/
│       │   │       ├── KnowledgeQaApplication.java
│       │   │       ├── controller/
│       │   │       │   └── KnowledgeQaController.java
│       │   │       ├── service/
│       │   │       │   ├── DocumentIngestService.java
│       │   │       │   ├── KnowledgeQaService.java
│       │   │       │   └── PromptBuilder.java
│       │   │       ├── store/
│       │   │       │   └── VectorStoreConfig.java
│       │   │       └── config/
│       │   │           └── DataInitConfig.java
│       │   └── resources/
│       │       ├── application.yml
│       │       ├── db/migration/
│       │       │   └── init.sql
│       │       └── docs/
│       │           └── company-rule.md
│       └── test/
└── doc/
    └── 知识库需求设计.md
```

## 🚀 快速开始

### 前置要求

1. **Java 21**（必需）
2. **PostgreSQL 12+**（需安装 pgvector 扩展）
3. **Maven 3.6+**
4. **OpenAI API Key**（或配置 Ollama）

### 1. 安装 PostgreSQL 和 pgvector

#### macOS
```bash
# 使用 Homebrew 安装 PostgreSQL
brew install postgresql

# 安装 pgvector 扩展
brew install pgvector
```

#### Linux
```bash
# Ubuntu/Debian
sudo apt-get install postgresql postgresql-contrib
# 然后按照 pgvector 官方文档安装扩展
```

#### Docker（推荐）
```bash
docker run -d \
  --name postgres-pgvector \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=knowledge_db \
  -p 5432:5432 \
  pgvector/pgvector:pg16
```

### 2. 初始化数据库

```bash
# 连接到 PostgreSQL
psql -U postgres -d knowledge_db

# 执行初始化脚本
\i knowledge-qa/src/main/resources/db/migration/init.sql
```

或者直接执行：
```bash
psql -U postgres -d knowledge_db -f knowledge-qa/src/main/resources/db/migration/init.sql
```

### 3. 配置应用

编辑 `knowledge-qa/src/main/resources/application.yml`：

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}  # 设置你的 OpenAI API Key
```

或者通过环境变量：
```bash
export OPENAI_API_KEY=sk-your-api-key-here
```

### 4. 运行应用

```bash
# 在项目根目录执行
mvn clean install
cd knowledge-qa
mvn spring-boot:run
```

或者直接运行主类：
```bash
mvn spring-boot:run -pl knowledge-qa
```

## 📡 API 使用

### 问答接口

```bash
# 测试问答
curl "http://localhost:8080/knowledge/ask?q=餐饮报销标准是多少"
```

**响应示例：**
```
公司餐饮报销标准为每人每天不超过100元。
```

## 🔧 技术栈

| 模块 | 技术选型 |
|------|---------|
| 框架 | Spring Boot 3.2.0 |
| AI 框架 | Spring AI 1.0.0-M4 |
| LLM | OpenAI GPT-3.5-turbo |
| Embedding | text-embedding-3-small (1536维) |
| 向量数据库 | PostgreSQL + pgvector |
| 构建工具 | Maven |

## 📚 核心功能

### 1. 文档入库（Ingest）
- 自动文档切分（TokenTextSplitter）
- 向量化存储
- 元数据管理

### 2. 向量检索
- 相似度搜索（TopK）
- 余弦距离计算
- HNSW 索引优化

### 3. RAG 问答
- 检索增强生成
- 提示词工程
- 防止幻觉机制

## 🎯 学习重点

### Spring AI 核心概念

1. **VectorStore**：向量存储抽象
   - `PgVectorStore`：PostgreSQL 实现
   - `similaritySearch()`：相似度检索

2. **EmbeddingModel**：嵌入模型
   - 文本转向量
   - 自动注入到 VectorStore

3. **ChatModel**：聊天模型
   - `call(Prompt)`：调用 LLM
   - 支持流式输出（Streaming）

4. **Document**：文档抽象
   - `content`：文本内容
   - `metadata`：元数据（Map）

5. **TextSplitter**：文本切分器
   - `TokenTextSplitter`：按 token 切分
   - 支持重叠（overlap）

## 🔍 关键代码说明

### VectorStore 配置
```java
@Bean
public VectorStore vectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
    return new PgVectorStore.Builder(jdbcTemplate, embeddingModel)
            .withTableName("knowledge_vector")
            .withEmbeddingColumnName("embedding")
            .withContentColumnName("content")
            .withMetadataColumnName("metadata")
            .build();
}
```

### RAG 流程
```java
// 1. 检索
List<Document> docs = vectorStore.similaritySearch(
    SearchRequest.query(question).withTopK(4)
);

// 2. 构建提示词
Prompt prompt = promptBuilder.build(question, docs);

// 3. 生成答案
return chatModel.call(prompt).getResult().getOutput().getContent();
```

## 🚧 后续扩展方向

- [ ] 支持多文档格式（PDF、Word、Excel）
- [ ] 文档权限控制（基于 metadata）
- [ ] SSE 流式输出
- [ ] 多模型支持（查询用便宜模型，汇总用强模型）
- [ ] 问题缓存（Redis）
- [ ] 对话历史管理
- [ ] Agent 多轮规划

## 📖 参考文档

- [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)
- [pgvector 文档](https://github.com/pgvector/pgvector)
- [OpenAI API 文档](https://platform.openai.com/docs)

## 📝 许可证

MIT License

