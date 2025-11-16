INSERT INTO account (name, password, role, created_at, updated_at)
VALUES ('测试管理员', 'e10adc3949ba59abbe56e057f20f883e', 'ADMIN', NOW(), NOW());

INSERT INTO department (name, address, description, email, phone, created_at, updated_at) VALUES
('测试部门', '测试地址', '测试描述', 'test-email@example.com', 'test-phone', NOW(), NOW());

INSERT INTO contact (name, password, inbox, phone, email, status, created_at, updated_at) VALUES
('测试联系人', 'e10adc3949ba59abbe56e057f20f883e', 10000, 'test-phone', 'testemail@example.com', 1, NOW(), NOW());

INSERT INTO account (name, password, role, created_at, updated_at) VALUES
('李老师',  'e10adc3949ba59abbe56e057f20f883e', 'USER', NOW(), NOW()),
('王老师',  'e10adc3949ba59abbe56e057f20f883e', 'USER', NOW(), NOW()),
('张老师',  'e10adc3949ba59abbe56e057f20f883e', 'USER', NOW(), NOW()),
('刘老师',  'e10adc3949ba59abbe56e057f20f883e', 'USER', NOW(), NOW()),
('陈老师',  'e10adc3949ba59abbe56e057f20f883e', 'USER', NOW(), NOW()),
('杨老师',  'e10adc3949ba59abbe56e057f20f883e', 'USER', NOW(), NOW()),
('黄老师',  'e10adc3949ba59abbe56e057f20f883e', 'USER', NOW(), NOW()),
('赵老师',  'e10adc3949ba59abbe56e057f20f883e', 'USER', NOW(), NOW()),
('周老师',  'e10adc3949ba59abbe56e057f20f883e', 'USER', NOW(), NOW()),
('吴老师',  'e10adc3949ba59abbe56e057f20f883e', 'USER', NOW(), NOW()),
('郑老师',  'e10adc3949ba59abbe56e057f20f883e', 'USER', NOW(), NOW()),
('何老师',  'e10adc3949ba59abbe56e057f20f883e', 'USER', NOW(), NOW()),
('林老师',  'e10adc3949ba59abbe56e057f20f883e', 'USER', NOW(), NOW()),
('宋老师',  'e10adc3949ba59abbe56e057f20f883e', 'USER', NOW(), NOW()),
('谢老师',  'e10adc3949ba59abbe56e057f20f883e', 'USER', NOW(), NOW()),
('唐老师',  'e10adc3949ba59abbe56e057f20f883e', 'USER', NOW(), NOW()),
('许老师',  'e10adc3949ba59abbe56e057f20f883e', 'USER', NOW(), NOW()),
('邓老师',  'e10adc3949ba59abbe56e057f20f883e', 'USER', NOW(), NOW()),
('冯老师',  'e10adc3949ba59abbe56e057f20f883e', 'USER', NOW(), NOW()),
('赖老师',  'e10adc3949ba59abbe56e057f20f883e', 'USER', NOW(), NOW());

INSERT INTO department (name, address, description, email, phone, created_at, updated_at) VALUES
('计算机学院', '东校区信息楼201', '负责计算机科学与技术专业教学与科研工作', 'cs@univ.edu.cn', '13800010001', NOW(), NOW()),
('电子信息学院', '东校区实验楼305', '负责电子信息工程相关课程与实验教学', 'ee@univ.edu.cn', '13800010002', NOW(), NOW()),
('自动化学院', '南校区科技楼102', '专注于自动化与智能控制系统研究', 'automation@univ.edu.cn', '13800010003', NOW(), NOW()),
('软件学院', '西校区软件楼301', '软件工程及人工智能研究教学单位', 'software@univ.edu.cn', '13800010004', NOW(), NOW()),
('物理学院', '北校区理科楼203', '物理学基础研究与应用方向培养基地', 'physics@univ.edu.cn', '13800010005', NOW(), NOW()),
('化学学院', '北校区实验楼209', '开展化学合成与分析方向科研教学', 'chemistry@univ.edu.cn', '13800010006', NOW(), NOW()),
('数学学院', '东校区数学楼401', '数学与统计相关专业培养单位', 'math@univ.edu.cn', '13800010007', NOW(), NOW()),
('外国语学院', '南校区外语楼106', '承担英语、日语及多语种教学任务', 'foreign@univ.edu.cn', '13800010008', NOW(), NOW()),
('经济管理学院', '西校区经管楼502', '经济学与工商管理领域教学科研', 'economics@univ.edu.cn', '13800010009', NOW(), NOW()),
('艺术学院', '南校区艺术楼201', '美术、音乐及设计学科教学与创作', 'arts@univ.edu.cn', '13800010010', NOW(), NOW()),
('新闻传播学院', '西校区传媒楼202', '新闻学与传播学研究与实践基地', 'media@univ.edu.cn', '13800010011', NOW(), NOW()),
('法学院', '东校区法学楼305', '培养法律专业人才与法治研究', 'law@univ.edu.cn', '13800010012', NOW(), NOW()),
('生命科学学院', '北校区生科楼404', '生物科学与技术方向教学科研', 'biology@univ.edu.cn', '13800010013', NOW(), NOW()),
('环境科学学院', '南校区环保楼301', '环境监测与生态保护研究方向', 'environment@univ.edu.cn', '13800010014', NOW(), NOW()),
('教育学院', '东校区教育楼501', '教育学与心理学相关专业培养单位', 'education@univ.edu.cn', '13800010015', NOW(), NOW()),
('材料学院', '北校区材料楼306', '新材料开发与材料物理研究', 'materials@univ.edu.cn', '13800010016', NOW(), NOW()),
('建筑学院', '西校区建筑楼102', '建筑设计与城市规划方向教学科研', 'architecture@univ.edu.cn', '13800010017', NOW(), NOW()),
('体育学院', '南校区体育馆201', '体育教育与运动训练研究', 'sports@univ.edu.cn', '13800010018', NOW(), NOW()),
('医学部', '南校区医学楼301', '医学基础与临床教学科研单位', 'medicine@univ.edu.cn', '13800010019', NOW(), NOW()),
('国际交流学院', '东校区国际楼101', '负责留学生教育与国际合作交流', 'international@univ.edu.cn', '13800010020', NOW(), NOW());

INSERT INTO contact (name, password, inbox, phone, email, status, created_at, updated_at) VALUES
('张伟', '123456', 10001, '13800138001', 'zhangwei@example.com', 1, NOW(), NOW()),
('李娜', '123456', 10002, '13800138002', 'lina@example.com', 1, NOW(), NOW()),
('王强', '123456', 10003, '13800138003', 'wangqiang@example.com', 1, NOW(), NOW()),
('刘洋', '123456', 10004, '13800138004', 'liuyang@example.com', 1, NOW(), NOW()),
('陈晨', '123456', 10005, '13800138005', 'chenchen@example.com', 0, NOW(), NOW()),
('赵磊', '123456', 10006, '13800138006', 'zhaolei@example.com', 1, NOW(), NOW()),
('孙婷', '123456', 10007, '13800138007', 'sunting@example.com', 1, NOW(), NOW()),
('周杰', '123456', 10008, '13800138008', 'zhoujie@example.com', 1, NOW(), NOW()),
('吴敏', '123456', 10009, '13800138009', 'wumin@example.com', 1, NOW(), NOW()),
('郑浩', '123456', 10010, '13800138010', 'zhenghao@example.com', 0, NOW(), NOW()),
('何静', '123456', 10011, '13800138011', 'hejing@example.com', 1, NOW(), NOW()),
('杨凯', '123456', 10012, '13800138012', 'yangkai@example.com', 1, NOW(), NOW()),
('胡悦', '123456', 10013, '13800138013', 'huyue@example.com', 1, NOW(), NOW()),
('许鹏', '123456', 10014, '13800138014', 'xupeng@example.com', 0, NOW(), NOW()),
('郭婷', '123456', 10015, '13800138015', 'guoting@example.com', 1, NOW(), NOW()),
('冯宇', '123456', 10016, '13800138016', 'fengyu@example.com', 1, NOW(), NOW()),
('高峰', '123456', 10017, '13800138017', 'gaofeng@example.com', 1, NOW(), NOW()),
('梁静', '123456', 10018, '13800138018', 'liangjing@example.com', 0, NOW(), NOW()),
('唐硕', '123456', 10019, '13800138019', 'tangshuo@example.com', 1, NOW(), NOW()),
('黄琦', '123456', 10020, '13800138020', 'huangqi@example.com', 1, NOW(), NOW());

INSERT INTO sensitive_word (word, created_at, updated_at) VALUES
('赌博', NOW(), NOW()),
('色情', NOW(), NOW()),
('毒品', NOW(), NOW()),
('暴力', NOW(), NOW()),
('恐怖组织', NOW(), NOW()),
('ISIS', NOW(), NOW()),
('走私', NOW(), NOW()),
('贪污', NOW(), NOW()),
('假币', NOW(), NOW()),
('诈骗', NOW(), NOW()),
('恐吓', NOW(), NOW()),
('洗钱', NOW(), NOW()),
('暗网', NOW(), NOW()),
('杀人', NOW(), NOW()),
('军火', NOW(), NOW()),
('造谣', NOW(), NOW()),
('辱骂', NOW(), NOW()),
('违规交易', NOW(), NOW()),
('成人网站', NOW(), NOW()),
('非法集资', NOW(), NOW());

INSERT INTO template (name, subject, content, created_at, updated_at) VALUES
('欢迎信模板', '欢迎加入校园通知系统', '亲爱的用户，欢迎使用本系统，祝您体验愉快！', NOW(), NOW()),
('活动通知模板', '校园活动报名开始啦', '本周末将举办迎新晚会，欢迎同学们积极报名参加！', NOW(), NOW()),
('安全警告模板', '账户异常登录提醒', '检测到您的账户存在异常登录，请及时修改密码！', NOW(), NOW()),
('课程更新模板', '课程表更新通知', '您的课程表已更新，请登录系统查看最新安排。', NOW(), NOW()),
('成绩发布模板', '成绩查询通知', '本学期成绩已发布，请登录学生系统进行查看。', NOW(), NOW()),
('系统维护模板', '系统维护公告', '系统将于本周六凌晨 2:00-4:00 进行维护，请提前保存工作。', NOW(), NOW()),
('问卷调查模板', '课程满意度调查', '请花费一分钟填写课程满意度问卷，我们重视您的反馈。', NOW(), NOW()),
('迟到提醒模板', '迟到警告通知', '您今日上课迟到已记录，请注意上课时间。', NOW(), NOW()),
('缴费通知模板', '学费缴纳提醒', '您的学费缴纳截止日期为本月 30 日，请及时缴纳。', NOW(), NOW()),
('节日祝福模板', '中秋节快乐', '祝您中秋节快乐，阖家幸福，月圆人圆事事圆！', NOW(), NOW()),
('请假审批模板', '请假申请审批结果', '您的请假申请已通过，请注意返回时间。', NOW(), NOW()),
('会议通知模板', '部门会议通知', '本周三下午 3 点在教学楼 302 开部门会议，请准时参加。', NOW(), NOW()),
('警示公告模板', '考试违规通报', '以下同学考试违规，现予以通报批评，请引以为戒。', NOW(), NOW()),
('系统升级模板', '系统功能升级公告', '新版本已上线，优化了用户体验与安全性能。', NOW(), NOW()),
('奖学金通知模板', '奖学金发放通知', '恭喜您获得本学期奖学金，请尽快前往财务处办理。', NOW(), NOW()),
('志愿活动模板', '志愿者活动报名', '本周六志愿活动火热报名中，点击报名参与社区服务！', NOW(), NOW()),
('毕业典礼模板', '毕业典礼邀请函', '亲爱的同学，诚邀您参加 2025 届毕业典礼，共赴青春盛宴！', NOW(), NOW());

INSERT INTO render (name, value, remark, created_at, updated_at) VALUES
('#account', NULL, '系统内置变量 account', NOW(), NOW()),
('#contact', NULL, '系统内置变量 contact', NOW(), NOW()),
('#department', NULL, '系统内置变量 department', NOW(), NOW()),
('#date', NULL, '系统内置变量 date', NOW(), NOW()),
('#datetime', NULL, '系统内置变量 datetime', NOW(), NOW()),
('#uuid', NULL, '系统内置变量 uuid', NOW(), NOW()),
('school', '中山大学', '中山大学名称', NOW(), NOW()),
('blog', 'https://dasi.plus', 'Dasi 的博客网站', NOW(), NOW()),
('greet', '尊敬的先生/女士，您好！', '问候文本', NOW(), NOW()),
('salute', '此致，敬礼', '敬礼文本', NOW(), NOW());