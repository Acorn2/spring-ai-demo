package com.example.knowledgeqa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 文档入库服务
 * 负责将文档切分、向量化并存储到向量数据库
 * 
 * @author Spring AI Demo
 */
@Service
@RequiredArgsConstructor
public class DocumentIngestService {

    private final VectorStore vectorStore;

    /**
     * 文档入库方法
     * 
     * @param text 文档文本内容
     * @param source 文档来源标识（如文件名、URL等）
     */
    public void ingest(String text, String source) {
        // 使用 TokenTextSplitter 进行文档切分
        // 参数说明：
        // - 400: chunkSize - 每段最大 token 数（建议 300-500）
        // - 100: minChunkSizeChars - 最小块字符数
        // - 50: minChunkLengthToEmbed - 最小嵌入长度（重叠 token 数）
        // - 1000: maxNumChunks - 最大块数限制
        // - false: keepSeparator - 不保留分隔符
        TokenTextSplitter splitter = new TokenTextSplitter(400, 100, 50, 1000, false);
        
        // 创建文档对象，包含内容和元数据
        Document document = new Document(text, Map.of("source", source));
        
        // 切分文档
        List<Document> documents = splitter.apply(List.of(document));

        // 存储到向量数据库（会自动进行 Embedding）
        vectorStore.add(documents);
    }
}

