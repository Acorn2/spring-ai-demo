package com.example.knowledgeqa.service;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 提示词构建器
 * 负责构建 RAG 提示词，这是决定回答质量的关键组件
 * 
 * @author Spring AI Demo
 */
@Component
public class PromptBuilder {

    /**
     * 构建 RAG 提示词
     * 
     * @param question 用户问题
     * @param docs 检索到的相关文档
     * @return 构建好的 Prompt 对象
     */
    public Prompt build(String question, List<Document> docs) {

        // 将多个文档内容合并，用分隔符连接
        String context = docs.stream()
                .map(doc -> doc.getText())  // 使用 getText() 方法获取文档内容
                .collect(Collectors.joining("\n---\n"));

        // 构建提示词模板
        // 关键点：明确约束模型只能基于提供的资料回答
        String template = """
                你是公司内部知识库助手。
                只能基于以下资料回答问题，如果资料中没有答案，请回答"未找到相关信息"。

                【资料】
                %s

                【问题】
                %s
                """;

        String promptText = String.format(template, context, question);
        
        // 使用 UserMessage 和 Prompt 构建提示词
        return new Prompt(new UserMessage(promptText));
    }
}

