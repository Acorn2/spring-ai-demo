# 基于 Spring AI 与 MCP 协议的自然语言接口调用方案设计

## 1. 项目背景
目前大语言模型（LLM）已具备强大的推理能力，但在处理特定业务逻辑或调用内部私有接口时，需要一种标准化的协议来连接 AI 与本地工具。MCP（Model Context Protocol）是由 Anthropic 提出的一种开放标准，旨在简化 AI 模型与外部数据源及工具的集成。

本项目旨在通过 Spring AI 集成 MCP 协议，实现从自然语言指令到业务接口调用的自动化映射，构建一个具备“插件化”能力的 AI Agent。

## 2. AI 原生开发思维：为什么是 Service 而不是 Controller？

对于习惯传统 Web 开发的同学，可能会疑惑：**为什么 `@Tool` 注解加在 Service 方法上，而不是 Controller 接口上？**

这是 AI 原生（AI-Native）架构与传统 Web 架构的核心区别：

1.  **调用者身份转换**：
    *   **Controller** 是给“人类用户”或“外部系统”通过 HTTP 调用的“前台柜台”。
    *   **Tool (Service)** 是给“AI 大脑”直接驱动的“内部业务主管”。AI 不需要模拟 HTTP 请求，它直接通过函数调用执行逻辑。
2.  **元数据驱动**：
    *   AI 识别工具不看 URL 路径，而是依赖 `@Tool` 中的 `description`（自然语言描述）。Service 方法通常更纯粹，易于被 AI 理解。
3.  **内网安全与复用**：
    *   有些功能我们不希望暴露外网接口（即不写 Controller），但希望 AI 可以在后台调用。Service 层的工具可以实现“外部隐身，AI 可见”。

| 特性 | 传统 Controller | AI Tool (Service) |
| :--- | :--- | :--- |
| **调用者** | 人类/外部系统 | LLM (大模型) |
| **索引方式** | URL 路径 (`/api/control`) | 自然语言描述 (`"控制设备"`) |
| **耦合度** | 与 Web 容器强耦合 | 纯业务逻辑，高度解耦 |

## 3. 整体架构
根据设计思路，系统分为三个核心角色：

### 2.1 AI Host / Agent (大脑)
*   **组件**：Spring AI 中的 `VertexAiGeminiChatModel` (或 `OpenAiChatModel`)。
*   **职责**：接收用户自然语言输入，结合当前上下文，决定是否需要调用工具。如果需要调用，则解析出对应的工具名称和参数。

### 2.2 MCP Client (传话筒)
*   **组件**：`spring-ai-mcp-client-spring-boot-starter`。
*   **职责**：作为 AI 模型与 MCP Server 之间的桥梁。它负责发现 Server 暴露的工具列表，并将 AI 的请求转发给 Server，最后将执行结果返回给 AI。

### 2.3 MCP Server (工具管家)
*   **组件**：基于 Spring Boot 的 MCP Server 实现。
*   **职责**：将本地的 Service 方法通过 `@Tool` 注解封装为 MCP 工具，并维持与 Client 的连接（通常通过 Stdio 或 HTTP）。

---

## 3. 技术栈
*   **核心框架**：Spring Boot 3.3+
*   **AI 框架**：Spring AI (1.0.0-SNAPSHOT/M1+)
*   **MCP SDK**：`mcp-spring` (Spring AI 官方推荐的 Java SDK)
*   **模型**：Google Vertex AI Gemini 1.5 Pro/Flash
*   **构建工具**：Maven/Gradle

---

## 4. 详细设计

### 4.1 MCP Server 端开发
1.  **定义业务方法**：在普通的 Spring Service 中编写业务逻辑。
2.  **工具映射**：使用 `@Tool` 注解标记方法，Spring AI MCP 会自动将其解析为 MCP 工具元数据。
3.  **传输层配置**：配置 Server 运行模式（例如本地 STDIO 或基于 HTTP 的远程调用）。

### 4.2 MCP Client 端配置
1.  **连接管理**：配置连接到 MCP Server 的参数。
2.  **工具集成**：将加载到的工具动态注入到 AI 的接口调用选项中。

### 4.3 交互逻辑流程
1.  **输入**：用户提问：“帮我查询一下用户 ID 为 101 的最新订单状态”。
2.  **规划**：Gemini 模型识别出需要调用 `query_order_status` 工具。
3.  **调用**：AI 发出工具调用请求 -> MCP Client 转发给 MCP Server -> 执行查询逻辑。
4.  **反馈**：Server 返回 “订单：待发货” -> AI 总结并回复：“好的，用户 101 的最新订单目前处于待发货状态”。

---

## 5. 项目结构规划 (后续)
拟创建子项目：`spring-ai-mcp-lab`
*   `mcp-server-provider`：具体的业务接口提供者。
*   `mcp-client-agent`：负责与用户交互的 AI 代理。

## 6. 开发参考 (子项目配置)

### 6.1 Maven 依赖 (pom.xml)
在子项目中需要引入以下核心依赖：

```xml
<dependencies>
    <!-- MCP Server 核心支持 -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-mcp</artifactId>
    </dependency>

    <!-- MCP Client Starter (用于 Agent 端) -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-mcp-client</artifactId>
    </dependency>

    <!-- Vertex AI Gemini 支持 -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-vertex-ai-gemini-spring-boot-starter</artifactId>
    </dependency>
</dependencies>
```

### 6.2 核心注解示例
```java
@Service
public class OrderService {
    
    @Tool(description = "根据订单ID查询状态")
    public String getOrderStatus(String orderId) {
        // 业务逻辑
        return "SUCCESS";
    }
}
```

## 7. 开发计划
1.  **环境准备**：配置 Google Cloud Vertex AI 权限及 Spring AI 依赖。
2.  **Server 开发**：实现一个简单的业务工具（如订单查询）并使用 `@Tool` 暴露。
3.  **Client 集成**：集成 MCP Client，实现 AI 对工具的自动发现。
4.  **全链路调试**：验证 “自然语言问答 -> 工具执行 -> 结果总结” 的闭环。
