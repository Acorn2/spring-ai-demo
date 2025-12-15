# 快速启动指南

## 前置准备

### 1. 安装 PostgreSQL + pgvector

**使用 Docker（推荐）：**
```bash
docker run -d \
  --name postgres-pgvector \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=knowledge_db \
  -p 5432:5432 \
  pgvector/pgvector:pg16
```

**验证安装：**
```bash
docker exec -it postgres-pgvector psql -U postgres -d knowledge_db -c "CREATE EXTENSION vector;"
```

### 2. 配置 OpenAI API Key

编辑 `src/main/resources/application.yml`：
```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY:your-api-key-here}
```

或设置环境变量：
```bash
export OPENAI_API_KEY=sk-your-api-key-here
```

## 启动步骤

### 1. 初始化数据库

```bash
# 执行 SQL 脚本
psql -U postgres -d knowledge_db -f src/main/resources/db/migration/init.sql
```

### 2. 编译项目

```bash
# 在项目根目录执行
mvn clean install
```

### 3. 运行应用

```bash
# 方式1：使用 Maven
mvn spring-boot:run -pl knowledge-qa

# 方式2：直接运行 jar
cd knowledge-qa
mvn spring-boot:run
```

### 4. 测试接口

```bash
# 测试问答接口
curl "http://localhost:8080/knowledge/ask?q=餐饮报销标准是多少"
```

**预期响应：**
```
公司餐饮报销标准为每人每天不超过100元。
```

## 常见问题

### Q: 连接数据库失败？
A: 检查 PostgreSQL 是否启动，端口是否为 5432，用户名密码是否正确。

### Q: pgvector 扩展未安装？
A: 执行 `CREATE EXTENSION vector;` 安装扩展。

### Q: OpenAI API 调用失败？
A: 检查 API Key 是否正确，账户是否有余额。

### Q: 向量检索无结果？
A: 确保已执行数据初始化，检查 `DataInitConfig` 是否正常加载文档。

## 下一步

- 添加更多文档到 `src/main/resources/docs/`
- 修改 `PromptBuilder` 优化提示词
- 调整 `TopK` 参数优化检索结果
- 实现文档上传接口

