package com.example.knowledgeqa.config;

import com.example.knowledgeqa.service.DocumentIngestService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

/**
 * 数据初始化配置
 * 应用启动时自动加载示例文档到向量库
 * 
 * @author Spring AI Demo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitConfig {

    private final DocumentIngestService ingestService;

    @Value("classpath:docs/company-rule.md")
    private Resource companyRuleResource;

    /**
     * 应用启动时初始化数据
     */
    @PostConstruct
    public void init() {
        try {
            // 读取示例文档
            String content = StreamUtils.copyToString(
                    companyRuleResource.getInputStream(),
                    StandardCharsets.UTF_8
            );

            // 入库
            ingestService.ingest(content, "company-rule.md");
            log.info("✅ 示例文档已成功加载到向量库");
        } catch (Exception e) {
            log.error("❌ 初始化文档失败", e);
        }
    }
}

