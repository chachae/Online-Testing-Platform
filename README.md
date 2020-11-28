# Online-Testing-Platform

一个基于 SpringBoot 2.X 开发的校园在线考试系统。「本项目已申报计算机软件著作权，不得用于商业用途，仅做学习交流。」

[![https://img.shields.io/badge/SpringBoot-2.3.0.RELEASE-brightgreen.svg?style=flat-square](https://img.shields.io/badge/SpringBoot-2.3.0.RELEASE-brightgreen.svg?style=flat-square)](https://img.shields.io/badge/SpringBoot-2.3.0.RELEASE-brightgreen.svg?style=flat-square) [![https://img.shields.io/badge/maven-3.6.0-yellow.svg?longCache=true&style=flat-square](https://img.shields.io/badge/maven-3.6.0-yellow.svg?longCache=true&style=flat-square)](https://img.shields.io/badge/maven-3.6.0-yellow.svg?longCache=true&style=flat-square) [![https://img.shields.io/badge/mybatis-plus-3.3.1-blueviolet.svg?style=flat-square](https://img.shields.io/badge/MyBatisPlus-3.3.1-blueviolet.svg?style=flat-square)](https://img.shields.io/badge/maven-3.6.0-yellow.svg?longCache=true&style=flat-square) 

本项目为个人项目，意旨为高校提供一套完整的权限化校园在校考试解决方案，以此降低出卷成本，提高阅卷效率，展现教学公平性、教研科学性，实现教育智能化。系统可提供在线考试、成绩分析、试题与试卷的信息维护、权限指派,实现了整个考试过程的自动化控制和数据信息化、权限化管理。项目源码均为本人截止今日一年多的 spring 框架学习与实践所得，适合作为同课题的毕设参考和 spring boot 框架学习的参考，欢迎星标 ！

## 三方依赖

| 序号     | 描述                            |
| :------- | :------------------------------ |
| 前端     | AdminLTE 3、JQuery |
| 后端     | spring boot 2、MyBatis-Plus   |
| 模板引擎 | beetl                           |
| 数据库   | MySQL、Redis                    |
| 工具类库 | guava、hutool、Apache-common                   |

## 系统服务

| 服务名称 | 描述                            | 端口 |
| :------- | ------------------------------- | -------- |
| oes-core | 考试服务与信息维护服务系统 | 8080 |
| oes-apm-spring-admin | APM 系统 Spring-Admin 服务监控子系统 | 8400 |

## 运行环境

JDK 8+

MySQL 数据库

Redis K-V 数据库
## 架构总览

| 系统架构图                            | 
| :------- | 
| ![](https://github.com/chachae/OES/raw/master/templates/screenshot/framework-over.png)| 



## 项目模块

### oes-apm
系统 APM 监控模块，目前已集成 Spring-Admin 服务监控子系统和 ELK 日志收集与分析平台，ELK需自行使用 docker-compose 完成平台的搭建。

### oes-common
系统通用模块，定义了系统的通用资源，包括领域模型、自定义工具类、常量池、系统配置、持久层接口、缓存层接口、统一异常处理等开放资源。

### oes-service
系统业务模块，定义了系统业务接口已经对应的实现。

### oes-core
系统核心模块，包含了系统的请求拦截器、权限维护、定时任务、外部化配置、初始化装置、自定义注解等核心组件层。对外开放页面控制层和 RESTful 统一服务接口，使用  controller 和 rest 区分页面模型和 RESTful 接口，其中，对 RESTful 接口进行了基于注解的统一自定义权限检测，采用分布式 session 管理用户会话，可做 Nginx 负载均衡，以实系统集群化部署。

### oes-util
系统的工具模块，已包含一个系统验证码服务和 Excel 文档处理服务，后续可拓展其它周边业务，如：邮箱服务、文件管理服务等。

## 主要实现
### 系统管理员
系统管理员作为系统的顶级角色，具备管理和维护系统顶层数据的权限。
1. 	公告管理。公告发布之后学生和教师可以在各自的消息公告栏看到消息内容，系统管理员具备多次编辑和删除公告的权限，学院管理员则可进行公告发布和编辑，教师和学生对公告信息只有只读权限。
2. 	学院管理。由系统管理员管理校内二级学院，包括新增、删除、修改二级学院信息，为后续的专业、教师、学生、课程进行学院的绑定。
3. 	教师管理。由管理员维护全校教师账号，新增教师的信息包括姓名、工号、登录密码、职位和所属学院等基础信息，其中需要注意的是工号作为教师的登录账号，需要保持唯一性。
4. 	管理员管理。系统管理员作为系统的顶级角色，对系统顶层数据流做维护和管理，因此只有系统管理员间相互协调管理，可通过它来添加系统管理员和学院管理院。
5. 	学生管理：由系统管理员管理全校学生信息，新增学生的姓名、学号、年级、专业，学号作为学生的登录账号并且不可重复。同时，全校学生数据量通常达万级数以上，因此必须具备全文检索、分类筛选的功能，以及服务端分页优化。
6. 	专业管理：由系统管理员管理全校专业数据，可在已存在的学院下新增专业信息。同样的，全校专业通常数据量较庞大，需要做如同学生管理的需求和优化。
### 学院管理员
学院管理员在系统管理员的基础上删减了部分功能和权限，增加了针对试卷和课程的权限指派业务，学院管理员的业务功能更集中在对本学院的管理和维护上。
1. 公告管理。学院管理员可进行公告发布和编辑。
2. 教师管理。学院管理员仅对本学院教师拥有增、删、改、查权限。
3. 	学生管理。学院管理员仅对本学院学生拥有增、删、改、查权限。
4. 	班级管理。学院管理员可对本学院专业进行班级分配，包括增、删、改、查。
5. 	专业管理。学院管理员可在本学院下进行专业维护，包括增、删、改、查四个权限。
6. 	试卷管理。教师完成出卷之后不能立刻将考试信息传达到学生账号下，必须由相应学院的学院管理员进行试卷班级指派，指派完成后，相关班级学生才有权限进行相应的考试，同时也是给老师一定的检查试卷的缓冲时间。
7. 	课程管理。学院管理员仅对本学院课程拥有增、删、改、查权限，增加课程的时候必需指派课程给一位或多位教师，指派完之后相应教师可拥有相应课程的出题、出卷权限。
### 教师
教师作为在线考试系统的主要使用者，从相应课程的题库维护、到课程题目录入、出卷都由教师在操作，本系统更将主要功能放在出题出卷上，确保教师在使用中的用户体验。
1. 消息公告。查看管理员发布的消息。
2. 	课程管理。查看指派到的课程数据。
3. 	试题管理。老师可使用导入本地模板表格和直接新增试题两种方式新增试题并录入题库中，每位老师的题库仅限于指派到的相关课程的题目，且不同出题人之间仅对自己的题目有查看、编辑和删除权限，对于其他出题人的题目只有只读权限。
4. 	试卷管理。老师可以使用导入试卷或者从题库中随机抽题组卷的方式新增一场考试，设置考试日期和时间，在考试未开始和未被学院管理员指派班级之前，可删除试卷。
5. 	试卷分析：教师可通过班级和考试场次筛选出某场考试的班级成绩分析数据表，并可以导出班级成绩分析表格，同时提供多维度数据包括平均分、排名、及格率等数据分析。
6. 	考试复查。作为主观题复查的选项，计算机会做第一次检查并评出基础分数，老师做二次检查并确定最后分数。
### 学生
学生是使用本系统基数最大的用户，在系统具备最低的权限，功能也更简洁，将重点放在考试答题环节和考卷分析详情，减少不必要的功能
1. 消息公告。查看公告内容，只读。
2. 我的考试。查看考试科目、时间、状态。同样也是每场考试的入口，到考试时间时，考试状态会由未开始转为开始，学生点击进入答卷，时间到自动交卷，题目顺序随机打乱。
3. 我的成绩。可以查询已考完的考试的成绩和考试情况详细信息。
4. 成绩分析。提供多维度成绩图表分析，通过雷达图显示学生每个科目的考试成绩与所有考生的每个科目成绩平均分的对比，以及该学生每个科目的成绩分数段比例饼图，同时，该学生可以将考试成绩汇总分析表格导出，形成成绩分析Excel数据表格，提供更多详情，如单科成绩总排名、平均分等更所信息供学生参考。 

## 部分功能截图

|                                                              |                                                              |
| :----------------------------------------------------------: | :----------------------------------------------------------: |
|                           系统登录                           |                           学院管理                           |
| ![](https://github.com/chachae/OES/raw/master/templates/screenshot/login.png) | ![](https://github.com/chachae/OES/raw/master/templates/screenshot/academy-manager.png) |
|                           成绩分析                          |                           公告管理                           |
| ![](https://github.com/chachae/OES/raw/master/templates/screenshot/analysis-1.jpg) | ![](https://github.com/chachae/OES/raw/master/templates/screenshot/announce-edit.png) |
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
