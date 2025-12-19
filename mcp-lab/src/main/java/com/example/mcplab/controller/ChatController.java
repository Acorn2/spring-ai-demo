package com.example.mcplab.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder chatClientBuilder) {
        // 配置 ChatClient 使用 DeviceControlService 中定义的工具
        // Spring AI 会自动识别标记了 @Tool 的 Bean 方法并作为函数公开
        this.chatClient = chatClientBuilder
                .defaultAdvisors()
                .build();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam(value = "message", defaultValue = "你好，你能帮我做什么？") String message) {
        return chatClient.prompt()
                .user(message)
                // 显式指定允许调用的工具名称（对应 Service 中的方法名或 Tool 注解指定的名称）
//                .functions("controlDevice", "getDeviceStatus")
                .call()
                .content();
    }

    @GetMapping("/chat/stream")
    public Flux<String> chatStream(@RequestParam(value = "message", defaultValue = "你好") String message) {
        return chatClient.prompt()
                .user(message)
//                .functions("controlDevice", "getDeviceStatus")
                .stream()
                .content();
    }
}
