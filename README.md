# 基于 spring-boot 2.X 的在线考试系统

## 一、概述

本项目为个人项目，该在线考试系统的用户类型有三种：教师、学生、管理员。 系统实现了试题导入、试卷导入、随机合成试卷、试卷发布、考试、自动化评分、批量复查试卷等功能，实现了自定义权限检测机制、开放接口统一管理、分布式 session、接口限流策略、缓存、页面异步刷新机制、非对称文本加密传输。功能繁多，代码中注释十分详细，采用模块化开发，区分了各类资源，适合同样在做该课题的毕业生参考。



## 二、技术栈

- 前端：Bootstrap、AdminLTE 3、JQuery
- 后端：SpringBoot、MyBatis-Plus
- 模板引擎：Beetl
- 数据库：MySQL
- 缓存中间件：Redis
- 工具类库：Guava、HuTool、Apache-Common



## 三、模块

### oes-common

系统通用模块：定义了系统的常用资源，包括 JavaBean、工具类、常量池、系统配置、持久层接口、缓存操作接口、统一异常处理等开放资源。

### oes-service

系统业务模块：定义了系统的业务接口和对应实现。

### oes-core

系统核心模块：包含了系统的请求拦截、权限维护、定时任务、数据配置、系统配置、初始化装置、自定义注解等核心组件、对外开放页面模型和 RESTful 统一服务接口，使用  controller 和 rest 区分页面模型和 RESTful 接口，其中，对 RESTful 接口进行统一自定义权限校验，采用分布式 session 管理用户会话。

### oes-util

系统工具模块：包含一个系统验证码服务模块，后续可拓展其它周边业务，如：邮箱、文件管理服务等。



## 四、系统主要实现功能

### 教师端

1. 个人设置：更换密码

2. 课程管理：教师添加自己的任课课程信息，默认一个课程只能有一个老师

3. 试题管理：教师可以通过添加试题丰富题库，也可以对已存在的题目进行修改和删除操作

   - Excel 批量导入试题

   - 手动录入试题

4. 试卷管理：教师可学则导入试卷或自动随机组合试卷来生成试卷

   - 随机组合试卷

   - Excel 导入试卷

   - 局部题型随机

5. 考试管理：试卷产生后会自动出现在学生考试系统中，若需要取消考试，可以在此处设置

6. 试卷复查：考生的主观题答案会被保存到数据库中，教师可以对其进行复查

7. 专业管理：对学院专业进行统一管理

   学生管理：对学生信息进行统一管理

9. 查看公告：查看管理员发布的公告信息


### 学生端

1. 个人信息管理：登录密码修改

2. 我的考试：学生进入后会看到试卷信息，当有需要参加的考试时，点击进入即可来到考试界面。 考试结束系统会自动提交考卷并完成自动改卷任务

   

4. 成绩分析：系统会统计出该生本学期参加每门考试的成绩，以及该门课程的平均成绩，使用雷达图进行对比

5. 查看公告：查看管理员发布的公告信息

### 管理端

1. 系统公告管理：在必要时发布系统公告

2. 教师管理：教师信息统一管理

3. 学院管理：对学院进行统一管理

4. 管理员管理：对管理员信息进行统一管理

## 五、部分功能展示

![](https://github.com/chachae/OES/raw/master/templates/screenshot/login.png)

![](https://github.com/chachae/OES/raw/master/templates/screenshot/academy-manager.png)

![](https://github.com/chachae/OES/raw/master/templates/screenshot/admin-manager.png)

![](https://github.com/chachae/OES/raw/master/templates/screenshot/announce-edit.png)

![](https://github.com/chachae/OES/raw/master/templates/screenshot/examing.png)

![](https://github.com/chachae/OES/raw/master/templates/screenshot/review-manager.png)

![](https://github.com/chachae/OES/raw/master/templates/screenshot/update-password.png)

![](https://github.com/chachae/OES/raw/master/templates/screenshot/exam-manager.png)

![](https://github.com/chachae/OES/raw/master/templates/screenshot/exam-manager2.png)

![](https://github.com/chachae/OES/raw/master/templates/screenshot/question-manager.png)