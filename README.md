# 📮 Dasi Message Center

> 本项目是我将实习期间学习到的 **Spring Boot、Redis、RabbitMQ、前后端分离、消息中台架构** 等内容进行整合复现，真正完成的 **第一个完整可落地的后端项目**。



## 1. 💡 项目介绍

### 简介

Dasi Message Center 同时面向管理端与用户端提供完整的消息服务体系，能够帮助开发者 **快速搭建自己的“全链路消息系统”**，从消息创建、配置、审核，到投递、接收、存档，全流程统一、可观测、高可靠。

- B 端：包括**账户管理、部门管理、联系人管理、模板配置、占位符配置、敏感词配置、消息发送、消息查询、错误日志等一体化后台功能**，使业务侧能够以极低成本构建稳定、统一的消息调度与投递系统。
- C 端：提供独立设计的站内信系统，用户可在前端系统中**实时查看消息、阅读通知、进行已读/未读管理、下载附件、查看回收站**，实现完整的消息全流程体验。

### **技术栈**

后端：Java 17，Spring Boot 3，MySQL，MyBatis-Plus，Redis，RabbitMQ，WebSocket，JWT，OSS，Maven

前端：Vite、Vue 3、Element-Plus、Pinia、Axios、Echarts



## 2. 🎯 核心功能

### 人员体系

- 支持**条件查询**：基于 `LambdaQueryWrapper`，可按关键字、状态、起止时间等精准筛选
- 支持**分页查询**：基于 `MyBatis-Plus` 内置的 `PaginationInnerInterceptor` 分页插件，提供自研 `PageResult` 返回结构，可以根据传来的页码和页大小返回页面数据
- 支持**数据缓存**：基于 `Redis + SpringCache` 的多级缓存机制，支持缓存精准清除

### 消息体系

- 支持**定时发送**：利用 `RabbitMQ` 的 `rabbitmq_delayed_message_exchange` 延迟插件，实现毫秒级延时投递
- 支持**批量发送**：`Message` 实体负责消息的整体内容，`Dispatch` 实体追踪消息投递到每个联系人的结果
- 支持**重发消息**：前端提供按钮，可对失败的单个联系人进行投递重试
- 支持 **AI 帮写**：接入 DeepSeek / Kimi，可以根据需求自动生成内容文本
- 支持**敏感词检测**：自定义 `SensitiveWord` 实体，自动检测文本是否包含敏感词
- 支持**模版复用**：自定义 `Template` 实体，可在发送页一键加载模板内容
- 支持**占位符渲染**：自定义 `Render` 实体，提供系统变量 `${#account}, ${#contact}, ${#date}`，同时也支持自定义变量`${key}:value` 
- 支持**错误查询**：自定义 `Failure` 实体，可以查看错误时间，错误类型，错误信息和错误栈，提供未处理/处理中/已解决/忽略等状态切换

### 站内箱体系

- 支持**已读/未读**：用绿色/红色背景区分已读情况，支持一键标记已读和撤回已读
- 支持**回收站**：利用软删除字段 deleted，实现恢复/彻底删除等操作
- 支持**附件下载**：依据存储的 `List<url>` 记录可安全下载云存储的附件
- 支持**在线更改联系方式**：用户可在页面更新个人信息，系统自动同步到消息投递逻辑

### 检查体系

- 检查**内容长度是否合法**：校验正文长度不得超过 1800 字符
- 检查**定时时间是否合法**：不允许早于当前，也不允许超过一个月
- 检查**联系人是否可用**：管理员可以设置联系人启用和禁用联系人，阻止向被禁用联系人投递
- 检查**联系人是否在当前渠道有联系方式**：批量发送中可能部分联系人在当前渠道的联系方式不存在
- 检查**内容是否命中敏感词**；可以在敏感词管理界面在线修改、添加、删除敏感词
- 检查**内容是否渲染占位符成功**：校验 key 是否合法且存在，实现 key 到 value 的自动映射转换

### 渠道体系

- **每个渠道都有各自的消息队列**：按 `MsgChannel` 映射到各自的 `Queue`
- **失败投递进入死信队列**：依赖 `x-dead-letter-exchange`，由自定义的 `DlxExchange` 统一处理
- **站内信渠道**：直接存入自定义的 `Mailbox` 表
- **邮件渠道**：在邮件服务器中开启 POP3/IMAP/SMTP 服务，然后利用 `JavaMailSender` 发送邮件
- **企业微信渠道**：创建企业并作为管理人创建企业应用，基于 `WeComClient` 调用企业微信官方 API

### 校验体系

- 支持**流量校验**：自定义 `@RateLimit` 和 `RateLimitAspect`，基于 `Redis + Lua` 实现高性能限流
- 支持**管理员校验**：自定义 `@AdminOnly` 和 `AdminOnlyAspect`，限制关键操作必须为 ADMIN
- 支**持唯一性校验**：自定义 `@UniqueField` 和 `UniqueFieldAspect`，保证唯一字段不重复，避免在数据库层触发异常
- 支持**存在性校验**：自定义 `@EnumValid` 和 `EnumValidator`， 校验参数是否属于指定枚举范围
- 支持**时间信息校验**：自定义 `@AutoFill` 和 `AutoFillAspect`，自动维护创建时间与更新时间

### 通用类体系

- 自定义**常量**：包括 `RedisConstant`，`SendConstant`，`SystemConstant `等，统一维护关键常量，防止明文写入，增强维护性
- 自定义**异常**：包括 `MessageCenterException`，`SendException`，`AccountException`，`ContactException`，`WeComException` 等，统一错误结构，实现代码语义化
- 自定义**枚举**；包括 `MsgStatus`，`FailureStatus`，`MsgChannel`，`AccountRole`，`FillType`，`ResultInfo`，保证类型安全与业务可读性

### 调度体系

- 支持**检查派送信息**：筛查状态切换异常和未正确完成投递的 `Dispatch`，转发到死信队列处理
- 支持**检查未使用的云存储文件**：通过 `used` 字段标记文件是否使用，短期内清理未使用的文件，同时清理 7 天前的所有文件



## 3. 🖥️ 项目演示

### 页面布局

#### 登陆注册

![image-20251117023654619](./assets/image-20251117023654619.png)

#### 仪表盘

![image-20251117023749407](./assets/image-20251117023749407.png)

### 人员管理

#### 账户

![image-20251117023759573](./assets/image-20251117023759573.png)

#### 部门

![image-20251117023812808](./assets/image-20251117023812808.png)

#### 联系人

![image-20251117023821136](./assets/image-20251117023821136.png)

### 消息管理

#### 发送

![image-20251117023855015](./assets/image-20251117023855015.png)

#### 查询

![image-20251117023902964](./assets/image-20251117023902964.png)

#### 派送

![image-20251117024009417](./assets/image-20251117024009417.png)

#### 审查

![image-20251117024022609](./assets/image-20251117024022609.png)

### 内容管理

#### 模版

![image-20251117024031462](./assets/image-20251117024031462.png)

#### 敏感词

![image-20251117024105020](./assets/image-20251117024105020.png)

#### 占位符

![image-20251117024116428](./assets/image-20251117024116428.png)

### 模式切换

#### 收起

![image-20251117024202233](./assets/image-20251117024202233.png)

#### 全屏

![image-20251117024218545](./assets/image-20251117024218545.png)

#### 暗色

![image-20251117024239781](./assets/image-20251117024239781.png)

### 站内信箱

#### 收件箱

![image-20251117024300289](./assets/image-20251117024300289.png)

#### 回收站

![image-20251117024310909](./assets/image-20251117024310909.png)

### 消息接收

#### 站内信

![image-20251117024336131](./assets/image-20251117024336131.png)

#### 邮件

![image-20251117024433488](./assets/image-20251117024433488.png)

#### 企业微信

![image-20251117024600810](./assets/image-20251117024600810.png)



## 4. 🚀 快速开始

> 需要提前利用 Docker 安装好 MySQL、Redis、RabbitMQ，并且配置好 .env，你可以通过提供的 docker-init.sh 一键初始化所有中间件
>
> ```bash
> chmod +x docker-init.sh
> ./docker-init.sh
> cp .env.example .env
> ```

### **方式一：Docker 启动**

1. 拉取后端镜像

    ```bash
    docker pull dasi0227/message-center:1.0
    ```

3. 启动 Dasi MessageCenter

    ```bash
    docker run -d \
      --name message-center \
      --env-file .env \
      --network message-net \
      -p 8080:8080 \
      dasi0227/message-center:1.0
    ```

### **方式二：本地启动**

1. 在 VSCode 启动前端

    ```java
    cd FrontEnd
    npm install
    npm run dev
    ```

2. 在 IDEA 启动后端

    ```bash
    cd BackEnd
    ./mvnw spring-boot:run
    ```

### 方式三：打包启动

1. 编译并生成 JAR

    ```bash
    cd BackEnd
    ./mvnw clean package -DskipTests
    ```

2. 指定环境文件并运行

    ```java
    export $(grep -v '^#' .env | xargs) && java -jar target/message-center.jar
    ```

    