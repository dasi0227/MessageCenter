# 消息中台需求设计文档



## 1. 登陆注册

消息中台本身就是提供给管理员（ADMIN），不存在普通用户（USER），但是管理员中有且有一个超级管理员（SUPER_ADMIN）
- SUPER_ADMIN 可以新增/注销 ADMIN、重置其密码
- 鉴权：登录成功发行 JWT，后续请求头携带 `Authorization: Bearer <token>`
- 用户名：≥4 位，仅字母数字，全局唯一
- 密码：≥6 位，仅字母数字

> TODO：超级管理员给普通管理员设置不同权限，比如发消息时间、联系人等



## 2. 通讯录

消息的接收人必须来自于通讯录，所有管理员共享同一个通讯录

- 管理员可以添加、删除、修改联系人信息
- 联系人字段包括：name（姓名）、phone（手机号）、email（邮箱）、inbox（站内信箱）
    - phone：需要检查手机号格式，仅支持中国大陆号码
    - email：需要校验邮箱格式，发信邮箱走系统预设的邮箱
    - inbox：6位字符串，整数递增，"000000" 保留给 SUPER_ADMIN，管理员注册时自动分配
    - 允许为空，但三者必须存在一个

> TODO：增加联系人的所属组 dept



## 3. 消息内容

| 字段       | 类型                               | 说明            |
| ---------- | ---------------------------------- | --------------- |
| msgd       | String                             | 消息全局唯一 ID |
| type       | Enum{INBOX, SMS, EMAIL}            | 消息类型        |
| status     | Enum{Draft,SENDING,SUCCESS,FAILED} | 消息状态        |
| subject    | String                             | 消息标题        |
| content    | String                             | 消息内容        |
| sendBy     | Long                               | 发信人          |
| sendTo     | Long                               | 收信人          |
| scheduleAt | Datetime                           | 定时时间        |
| createdAt  | Datetime                           | 创建时间        |
| updatedAt  | Datetime                           | 更新时间        |
| sentAt     | Datetime                           | 发送时间        |
| finishedAt | Datetime                           | 结束时间        |
| errorMsg   | String                             | 错误消息        |
| isDelete   | Boolean                            | 是否删除        |
| isDraft    | Boolean                            | 是否是草稿      |



## 4. 消息队列与渠道

所有消息一开始由统一的 Sender 来接受，并根据渠道类型分发到不同的消息队列之中，然后由三个不同的 ChannelSender 从消息队列中获取消息并调用相关 SDK 发送

- Sender 会进行敏感词检测，如果非法会放入死信队列，并注明错误原因
- Sender 会检查发信人和收信人是否都合法，如果非法会放入死信队列，并注明错误原因
- 发消息过程中如果抛出异常，会放入死信队列，并注明错误原因

> TODO：先完成 INBOX 和 EMAIL，SMS 最后完成



## 5. 发送形态

- 单发 / 群发：sendTo 为联系人 ID 数组
- 定时：scheduleAt 设置未来时间，到点才进入对应处理队列



## 6. 信箱

- 收件箱（Inbox）：仅接受站内消息
- 草稿箱（Draft）：与消息的 isDraft 字段关联，仅作者可见
- 垃圾箱（Dustbin）：与消息的 isDelete 字段关联，仅作者可见，定期 7 天清理



## 7. 查询

- 过滤选项：
- 分页：默认 page = 1，size = 10，排序按照 create desc
- 可视化：总消息数、成功消息数、失败消息数、联系人数、消息发送成功率、不同月份的消息数量















