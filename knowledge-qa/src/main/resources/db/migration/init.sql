-- 知识库向量表初始化脚本
-- 需要先安装 pgvector 扩展

-- 创建 pgvector 扩展（如果不存在）
CREATE EXTENSION IF NOT EXISTS vector;

-- 创建知识库向量表
CREATE TABLE IF NOT EXISTS knowledge_vector (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    embedding VECTOR(1536) NOT NULL,
    metadata JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建向量相似度搜索索引（使用 HNSW 算法，性能更好）
CREATE INDEX IF NOT EXISTS knowledge_vector_embedding_idx 
ON knowledge_vector 
USING hnsw (embedding vector_cosine_ops);

-- 创建元数据索引（用于后续权限过滤等功能）
CREATE INDEX IF NOT EXISTS knowledge_vector_metadata_idx 
ON knowledge_vector 
USING GIN (metadata);

COMMENT ON TABLE knowledge_vector IS '知识库向量存储表';
COMMENT ON COLUMN knowledge_vector.content IS '文档内容';
COMMENT ON COLUMN knowledge_vector.embedding IS '向量嵌入（1536维，对应 text-embedding-3-small）';
COMMENT ON COLUMN knowledge_vector.metadata IS '元数据（JSON格式，可存储来源、权限等信息）';

