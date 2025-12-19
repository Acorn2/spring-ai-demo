# Spring AI MCP Lab (通义千问版)

本项目是一个基于 **Spring AI** 和 **MCP (Model Context Protocol)** 协议的实操练习项目。它展示了如何通过自然语言指令，驱动大模型（通义千问 Qwen）自动调用本地业务接口。

## 🌟 核心特性
- **自然语言控制**：使用“白话”指令操作复杂的业务逻辑。
- **自动化工具映射**：通过 `@Tool` 注解，零成本将 Service 方法暴露给 AI。
- **国产大模型集成**：适配阿里 **通义千问 (Qwen-Max)**，对中文指令理解更精准。
- **解耦设计**：基于 Spring AI 的 `ChatClient` 抽象，模型切换仅需修改配置。

## 🏗 项目架构
- **大脑 (AI Host)**：通义千问 Qwen-Max
- **工具箱 (Tools)**：`DeviceControlService`（模拟家电控制接口）
- **接口 (Controller)**：提供标准 RESTful 对话接口

## 🚀 快速开始

### 1. 获取 API Key
前往 [阿里云灵积控制台](https://dashscope.console.aliyun.com/) 获取 API Key。

### 2. 配置环境变量
在终端或 IDE 启动参数中设置环境变量：
```bash
export DASHSCOPE_API_KEY='您的API-KEY'
```

### 3. 运行项目
在根目录下或本子项目下运行：
```bash
./mvnw spring-boot:run
```

## 🧪 测试指令
应用启动后（端口 8081），可以通过以下接口进行交互：

### 测试 1：查询设备状态
- **指令**：`http://localhost:8081/chat?message=客厅的灯现在亮着吗？`
- **预期**：AI 会识别出需要调用 `getDeviceStatus` 工具，并告知你状态。

### 测试 2：控制设备动作
- **指令**：`http://localhost:8081/chat?message=帮我把空调调到26度，辛苦了。`
- **预期**：AI 识别出 `controlDevice` 工具，设置参数 `value="26"`，并返回执行成功的确认。

## 🛠 进阶开发
如果你想添加新的功能，只需两步：
1. 在 `service` 包下创建新的方法。
2. 在该方法上添加 `@Tool(description = "清晰的功能描述")`。
3. 在 `ChatController` 的 `.functions()` 中加入该方法名。

---
*愿 AI 成为您得力的业务助手！*
