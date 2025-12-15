package com.example.knowledgeqa.controller;

import com.example.knowledgeqa.service.KnowledgeQaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 知识问答控制器
 * 提供 REST API 接口
 * 
 * @author Spring AI Demo
 */
@RestController
@RequiredArgsConstructor
public class KnowledgeQaController {

    private final KnowledgeQaService qaService;

    /**
     * 知识问答接口
     * 
     * @param q 用户问题
     * @return AI 生成的答案
     */
    @GetMapping("/knowledge/ask")
    public String ask(@RequestParam String q) {
        return qaService.ask(q);
    }
}

