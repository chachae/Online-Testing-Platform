# OES

OES，一个基于 spring-boot 2.X 的校园在线考试系统

[![https://img.shields.io/badge/maven-3.6.0-yellow.svg?longCache=true&style=flat-square](https://img.shields.io/badge/maven-3.6.0-yellow.svg?longCache=true&style=flat-square)](https://img.shields.io/badge/maven-3.6.0-yellow.svg?longCache=true&style=flat-square) [![https://img.shields.io/badge/mybatis-plus-3.3.1-blueviolet.svg?style=flat-square](https://img.shields.io/badge/MyBatisPlus-3.3.1-blueviolet.svg?style=flat-square)](https://img.shields.io/badge/maven-3.6.0-yellow.svg?longCache=true&style=flat-square) [![https://img.shields.io/badge/SpringBoot-2.2.6.RELEASE-brightgreen.svg?style=flat-square](https://img.shields.io/badge/SpringBoot-2.2.6.RELEASE-brightgreen.svg?style=flat-square)](https://img.shields.io/badge/SpringBoot-2.2.6.RELEASE-brightgreen.svg?style=flat-square) 

本项目为个人项目，以高校为背景构建的一个在线考试系统。该在线考试系统的用户类型有三类，分别为教师、学生和管理员。本系统的实现包括试题和试卷的多样化定制和导入 Excel 的批处理能力，试卷的发布与信息修改，学生考试，自动化评分，批量复查试卷，信息化等功能。实现了 AOP 权限检测机制，对外暴露接口统一管理，分布式 session，基于注解的接口限流策略，可插拔的缓存接口，前端页面无感刷新机制，RSA 非对称文本加密传输，定时任务等，功能丰富，采用模块化开发，对各类资源进行了较为合理的区分，以阿里规范为编码基准，详尽的源代码注释。项目源码均为本人截止今日一年多的 spring 框架学习与实践所得，适合作为同课题的毕设参考和 spring-boot 框架学习的参考，欢迎星标 ！

## 三方依赖

| 序号     | 描述                            |
| :------- | :------------------------------ |
| 前端     | AdminLTE 3、JQuery |
| 后端     | spring boot 2、MyBatis-Plus   |
| 模板引擎 | beetl                           |
| 数据库   | MySQL、Redis                    |
| 工具类库 | guava、hutool                   |

## 系统服务

| 服务名称 | 描述                            | 端口 |
| :------- | ------------------------------- | -------- |
| oes-core | 考试服务与信息维护服务系统 | 8080 |
| oes-apm-spring-admin | APM 系统 Spring-Admin 服务监控子系统 | 8400 |

## 运行环境

JDK 8+

MySQL 数据库

Redis K-V 数据库

## 项目模块

oes-apm：系统 APM 监控模块，目前已集成 Spring-Admin 服务监控子系统和 ELK 日志收集与分析平台，ELK需自行使用 docker-compose 完成平台的搭建。

oes-common：系统通用模块，定义了系统的通用资源，包括领域模型、自定义工具类、常量池、系统配置、持久层接口、缓存层接口、统一异常处理等开放资源。

oes-service：系统业务模块，定义了系统业务接口已经对应的实现。

oes-core：系统核心模块，包含了系统的请求拦截器、权限维护、定时任务、外部化配置、初始化装置、自定义注解等核心组件层。对外开放页面控制层和 RESTful 统一服务接口，使用  controller 和 rest 区分页面模型和 RESTful 接口，其中，对 RESTful 接口进行了基于注解的统一自定义权限检测，采用分布式 session 管理用户会话，可做 Nginx 负载均衡，以实系统集群化部署。

oes-util：系统的工具模块，已包含一个系统验证码服务模块，包括验证码的生成和校验，后续可拓展其它周边业务，如：邮箱服务、文件管理服务等。

## 主要实现

所有功能的体验以系统的具体实现为准

教师端

1. 信息管理：登录密码修改
2. 课程管理：教师添加自己的任课课程信息，默认一个课程只能有一个老师
3. 试题管理：教师可以通过添加试题丰富题库，也可以对已存在的题目进行修改和删除操作
4. 试卷管理：教师可学则导入试卷或自动随机组合试卷来生成试卷
5. 考试管理：试卷产生后会自动出现在学生考试系统中，若需要取消考试，可以在此处设置
6. 试卷复查：考生的主观题答案会被保存到数据库中，教师可以对其进行复查
7. 专业管理：对学院专业进行统一管理
8. 学生管理：对学生信息进行统一管理
9. 查看公告：查看管理员发布的公告信息
10. 个人设置：登录密码修改

学生端

1. 信息管理：登录密码修改
2. 我的考试：学生进入后会看到试卷信息，当有需要参加的考试时，点击进入即可来到考试界面。 考试结束系统会自动提交考卷并完成自动改卷任务
3. 成绩分析：系统会统计出该生本学期参加每门考试的成绩，以及该门课程的平均成绩，使用雷达图进行对比
5. 查看公告：查看管理员发布的公告信息
5. 个人设置：登录密码修改

管理端

1. 公告管理：在必要时发布系统公告
2. 教师管理：教师信息统一管理
3. 学院管理：对学院进行统一管理
4. 管理员管理：对管理员信息进行统一管理
5. 个人设置：登录密码修改

## 部分功能截图

|                                                              |                                                              |
| :----------------------------------------------------------: | :----------------------------------------------------------: |
|                           系统登录                           |                           学院管理                           |
| ![](https://github.com/chachae/OES/raw/master/templates/screenshot/login.png) | ![](https://github.com/chachae/OES/raw/master/templates/screenshot/academy-manager.png) |
|                          管理员管理                          |                           公告管理                           |
| ![](https://github.com/chachae/OES/raw/master/templates/screenshot/admin-manager.png) | ![](https://github.com/chachae/OES/raw/master/templates/screenshot/announce-edit.png) |
|                           在线考试                           |                          主观题复查                          |
| ![](https://github.com/chachae/OES/raw/master/templates/screenshot/examing.png) | ![](https://github.com/chachae/OES/raw/master/templates/screenshot/review-manager.png) |
|                           考试管理                           |                         学生考试信息                         |
| ![](https://github.com/chachae/OES/raw/master/templates/screenshot/exam-manager.png) | ![](https://github.com/chachae/OES/raw/master/templates/screenshot/exam-manager2.png) |
|                         个人信息管理                         |                           试题管理                           |
| ![](https://github.com/chachae/OES/raw/master/templates/screenshot/update-password.png) | ![](https://github.com/chachae/OES/raw/master/templates/screenshot/question-manager.png) |

## 留言

目前作者大三在读，除开发初期阶段外更多的是利用课余时间维护本项目，精力有限，有任何问题可通过 issues 提出，无格式限定，只要有留意到就会给予答复，并且会在时间精力允许的情况下解决问题。

## License

```reStructuredText
Copyright [2020] [chachae]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```









