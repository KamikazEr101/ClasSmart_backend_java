# 🤖 ClasSmart—灵分 Java 后端

## 📚 概述 | Overview

ClasSmart—灵分 是一款智能垃圾分类系统，用户仅需提交待分类的图片，即可获得分类结果。本文档详细介绍了 Java 后端的设计与实现，包括工作流程、定时任务、安全策略和分布式扩展性。

### 🖼️ 工作流程 | Work Flow

Java 后端负责接收用户上传的图片并协调整个分类过程，具体流程如下：

1. **图片上传与缓存检查**
   用户上传图片后，Java 后端计算图片的 MD5 哈希值，并检查 Redis 缓存中是否存在对应的 key：
   - **缓存命中**：直接返回 Redis 中存储的分类结果（`PredictedLabelResponseDTO`）。
   - **缓存未命中**：通过 HTTP 协议将图片传输至 Python 后端进行分类。
2. **模型分类与数据存储**
   Python 后端使用 PyTorch 训练的模型对图片进行分类，并将结果返回给 Java 后端：
   - **相关图片**：Java 后端将图片存储至 MinIO，获取 URL，并将记录存入 MySQL 数据库。存储字段包括：
     - `contentType`：图片内容类型
     - `url`：MinIO 返回的图片存储地址
     - `predictedLabel`：PyTorch 模型预测的分类结果
     - `feedbackLabel`：用户反馈的分类结果（初始为 null）
     - `uploadTime`：图片上传至 MinIO 的时间
   - **无关图片**：不进行存储 (具体原理请参见 python 后端)。
3. **响应与用户反馈**
   - Java 后端通过 MyBatis 主键回显策略，将`图片的主键 ID` 和 `predictedLabel` 封装为 `PredictedLabelResponseDTO` 对象，返回给用户。
   - 在响应前，该对象以图片 MD5 哈希值为 key 存入 Redis。
   - 用户可提交反馈 `feedbackLabel` 及图片主键 ID：
     - 若 `feedbackLabel` 与 `predictedLabel` 不一致，则删除 Redis 中的对应缓存。
     - 无论一致与否，若图片为相关图片，则将 `feedbackLabel` 更新至 MySQL 数据库。

------

## ⏰ 定时任务 | Scheduled Tasks

Java 后端`每日 0 点`执行以下定时任务，确保数据高效管理和模型持续优化：

1. **删除无用图片**
   删除满足以下任一条件的图片记录：
   - `predictedLabel` 为 `null`（无关图片）
   - `predictedLabel` 等于 `feedbackLabel`（用户确认分类正确）
   - `uploadTime` 早于当前时间减去指定小时数（hourCount）且 `feedbackLabel` 为 `null`（长期未反馈）
2. **数据集打包与模型复训练**
   将剩余的有用图片打包为 PyTorch 训练数据集，并上传至 Python 后端进行智能体筛选与模型复训练，以提升分类准确性。

------

## 🔒 后端安全策略 | Security Policies

为保障系统稳定性和安全性，Java 后端实施了以下策略：

1. **令牌桶限流**
   - **总体限流**：采用令牌桶算法控制系统整体请求速率，防止过载。
   - **IP 限流**：针对特定 IP，若请求频率过高，则暂时封禁该 IP。
2. **黑名单策略**
   对频繁触发限流的 IP 加入黑名单，禁止其访问系统一段时间。

------

## 🌐 分布式扩展性 | Distributed Scalability

Java 后端支持多实例单数据源的分布式部署，设计如下：

1. **Redis 分布式锁**
   定时任务使用 Redis 分布式锁，确保多实例环境下任务互斥执行。
2. **Redis IP 存储**
   限流策略通过 Redis 存储 IP 信息，实现跨实例的限流与黑名单管理。
3. **Nginx 负载均衡**
   Nginx 分发请求至多个 Java 后端实例，提升系统吞吐量。
4. **MinIO 分布式存储**
   MinIO 原生支持分布式部署，确保图片存储的高可用性与扩展性。

------

## 🛠️ 技术栈 | Tech Stack

| 模块          | 技术/组件                                    |
| ------------- | -------------------------------------------- |
| **Java 后端** | `SpringBoot`、`SpringMVC`、`MyBatis`、`Redis`、`MinIO` |

------

## 🔮 未来展望 | Future Outlook

Java 后端未来将持续优化，计划引入以下技术提升性能与扩展性：

- **Redis 集群**：提供高性能缓存服务，增强响应速度与稳定性。
- **Spring Cloud 微服务架构**：实现微服务化与分布式事务管理，提升可维护性。
- **数据库优化**：优化 MySQL 表结构，提高查询与存储效率。
- **MinIO 分布式部署**：进一步利用 MinIO 的分布式特性，确保存储的高可用性。