package com.example.knowledgeqa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 知识问答服务
 * 实现 RAG（检索增强生成）核心逻辑
 * 
 * @author Spring AI Demo
 */
@Service
@RequiredArgsConstructor
public class KnowledgeQaService {

    private final VectorStore vectorStore;
    private final ChatModel chatModel;
    private final PromptBuilder promptBuilder;

    /**
     * 回答问题
     * 
     * @param question 用户问题
     * @return AI 生成的答案
     */
    public String ask(String question) {
        // 1. 向量相似度检索：从向量库中查找最相关的文档片段
        // TopK=4 表示返回最相似的 4 个文档片段
        // 使用 SearchRequest Builder 模式
        SearchRequest searchRequest = SearchRequest.builder()
                .query(question)
                .topK(4)
                .build();
        List<Document> docs = vectorStore.similaritySearch(searchRequest);

        // 2. 构建包含检索结果的提示词
        Prompt prompt = promptBuilder.build(question, docs);

        // 3. 调用 LLM 生成答案
        return chatModel.call(prompt)
                .getResult()
                .getOutput()
                .getText();  // 使用 getText() 方法获取内容
    }
}

