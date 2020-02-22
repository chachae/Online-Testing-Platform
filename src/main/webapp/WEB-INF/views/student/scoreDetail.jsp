<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>OES | 试卷详情</title>
    <!-- css style -->
    <%@ include file="../include/css.jsp" %>
    <style>
        th {
            font-size: 18px;
        }

        td {
            font-size: 17px;
        }
    </style>

</head>
<body class="hold-transition skin-green sidebar-mini">
<!-- Site wrapper -->
<div class="wrapper">
    <!-- 顶部导航栏部分 -->
    <%@ include file="../student/include/header.jsp" %>
    <!-- 左侧菜单栏 -->
    <jsp:include page="../student/include/sider.jsp">
        <jsp:param name="menu" value="scoreDeatil"/>
    </jsp:include>
    <!-- 右侧内容部分 -->
    <div class="content-wrapper">
        <!-- Main content -->
        <section class="content">
            <div class="box">
                <div class="box-header with-border">
                    <h3 class="box-title">试卷信息</h3>
                    <div class="box-tools">
                        <a href="javascript:history.go(-1)" class="btn btn-google btn-sm"><i
                                class="fa fa-arrow-left"></i> 返回列表</a>
                    </div>
                </div>
                <div class="box-body ">

                    <table class="table">
                        <tr>
                            <th class="td_title" style="background-color: #e4e4e4">试卷名称：${paper.paperName}</th>
                        </tr>
                        <tr>
                            <td class="td_title">开始时间：${paper.beginTime}</td>
                        </tr>
                        <tr>
                            <td class="td_title">结束时间：${paper.endTime}</td>
                        </tr>
                        <tr>
                            <td class="td_title">试卷总分：${paper.score} 分</td>
                        </tr>
                        <tr>
                            <td class="td_title">我的成绩：${score.score} 分</td>
                        </tr>
                        <tr>
                            <td class="td_title">我的错题：${score.wrongIds}</td>
                        </tr>
                        <tr>
                            <td class="td_title">试卷状态：
                                <c:choose>
                                    <c:when test="${paper.end}">已结束</c:when>
                                    <c:when test="${paper.start}">考试中</c:when>
                                    <c:otherwise>未开始</c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        <tr>
                            <td class="td_title">试卷类型：${paper.paperType}考试</td>
                        </tr>
                        <tr>
                            <td class="td_title">考试课程：《 ${course.courseName} 》</td>
                        </tr>
                        <tr>
                            <td class="td_title">考试专业：${major.major}</td>
                        </tr>
                    </table>
                </div>
            </div>

            <div class="box">
                <div class="box-header with-border">
                    <h3 class="box-title">试卷详情</h3>
                </div>
                <div class="box-body">
                    <form id="paper" method="post">
                        <ul class="list-group">
                            <%--单选题--%>
                            <c:if test="${not empty qChoiceList}">
                                <div class="panel panel-primary">
                                    <div class="panel-heading">
                                        <h4 class="panel-title">单选题</h4>
                                    </div>
                                    <div class="panel-body">
                                        <c:forEach items="${qChoiceList}" var="choice" varStatus="state">
                                            <c:choose>
                                                <c:when test="${fn:contains(wrongList,fn:trim(',').concat(fn:trim(choice.id)).concat(','))}">
                                                    <span style="color: red">${state.count }.  【错题】 ${choice.questionName}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    ${state.count }. ${choice.questionName}
                                                </c:otherwise>
                                            </c:choose>
                                            <div class="radio">
                                                <label>
                                                    A. <span style="display: inline-block;"> <c:out
                                                        value="${choice.optionA}" escapeXml="true"/> </span>
                                                </label>
                                            </div>
                                            <div class="radio">
                                                <label>
                                                    B. <span style="display: inline-block;"><c:out
                                                        value="${choice.optionB}" escapeXml="true"/></span>
                                                </label>
                                            </div>
                                            <div class="radio">
                                                <label>
                                                    C. <span style="display: inline-block;"><c:out
                                                        value="${choice.optionC}" escapeXml="true"/></span>
                                                </label>
                                            </div>
                                            <div class="radio">
                                                <label>
                                                    D. <span style="display: inline-block;"><c:out
                                                        value="${choice.optionD}" escapeXml="true"/></span>
                                                </label>
                                            </div>
                                            <div class="form-group">
                                                <label>答案</label>
                                                <input type="text" readonly="readonly" value="${choice.answer}"
                                                       class="form-control choiceAns">
                                                <br>
                                                <label>解析</label>
                                                <c:choose>
                                                    <c:when test="${choice.remark != null}">
                                                         <textarea rows="2" type="text" class="form-control"
                                                                   readonly="readonly">${choice.remark}</textarea>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input type="text" readonly="readonly" value="无解析"
                                                               class="form-control">
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <hr>
                                        </c:forEach>
                                    </div>
                                </div>

                            </c:if>

                            <%--多选题--%>
                            <c:if test="${not empty qMulChoiceList}">
                                <div class="panel panel-primary">
                                    <div class="panel-heading">
                                        <h4 class="panel-title">多选题 <small>(不选、错选、漏选均不得分)</small></h4>
                                    </div>
                                    <div class="panel-body">
                                        <c:forEach items="${qMulChoiceList}" var="qMulChoice" varStatus="state">
                                            <c:choose>
                                                <c:when test="${fn:contains(wrongList,fn:trim(',').concat(fn:trim(qMulChoice.id)).concat(','))}">
                                                    <span style="color: red">${state.count }.  【错题】 ${qMulChoice.questionName}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    ${state.count }. ${qMulChoice.questionName}
                                                </c:otherwise>
                                            </c:choose>
                                            <div class="checkbox">
                                                <label>
                                                    A. <span style="display: inline-block;"><c:out
                                                        value="${qMulChoice.optionA}" escapeXml="true"/> </span>
                                                </label>
                                            </div>
                                            <div class="checkbox">
                                                <label>
                                                    B. <span style="display: inline-block;"><c:out
                                                        value="${qMulChoice.optionB}" escapeXml="true"/></span>
                                                </label>
                                            </div>
                                            <div class="checkbox">
                                                <label>
                                                    C. <span style="display: inline-block;"><c:out
                                                        value="${qMulChoice.optionC}" escapeXml="true"/></span>
                                                </label>
                                            </div>
                                            <div class="checkbox">
                                                <label>
                                                    D. <span style="display: inline-block;"><c:out
                                                        value="${qMulChoice.optionD}" escapeXml="true"/></span>
                                                </label>
                                            </div>
                                            <div class="form-group">
                                                <label>答案</label>
                                                <input type="text" readonly="readonly" value="${qMulChoice.answer}"
                                                       class="form-control">
                                                <br>
                                                <label>解析</label>
                                                <c:choose>
                                                    <c:when test="${qMulChoice.remark != null}">
                                                         <textarea rows="2" type="text" class="form-control"
                                                                   readonly="readonly">${qMulChoice.remark}</textarea>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input type="text" readonly="readonly" value="无解析"
                                                               class="form-control">
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <hr/>
                                        </c:forEach>
                                    </div>
                                </div>
                            </c:if>
                            <%--判断题--%>
                            <c:if test="${not empty qTofList}">
                                <div class="panel panel-primary">
                                    <div class="panel-heading">
                                        <h4 class="panel-title">判断题</h4>
                                    </div>
                                    <div class="panel-body">
                                        <c:forEach items="${qTofList}" var="question" varStatus="state">
                                            <c:choose>
                                                <c:when test="${fn:contains(wrongList,fn:trim(',').concat(fn:trim(question.id)).concat(','))}">
                                                    <span style="color: red">${state.count }.  【错题】 ${question.questionName}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    ${state.count }. ${question.questionName}
                                                </c:otherwise>
                                            </c:choose>
                                            <div class="radio">
                                                <label>
                                                    A. 对<span style="display: inline-block;"></span>
                                                </label>
                                            </div>
                                            <div class="radio">
                                                <label>
                                                    B. 错<span style="display: inline-block;"></span>
                                                </label>
                                            </div>
                                            <div class="form-group">
                                                <label>答案</label>
                                                <input type="text" readonly="readonly" value="${question.answer}"
                                                       class="form-control">
                                                <br>
                                                <label>解析</label>
                                                <c:choose>
                                                    <c:when test="${question.remark != null}">
                                                         <textarea rows="2" type="text" class="form-control"
                                                                   readonly="readonly">${question.remark}</textarea>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input type="text" readonly="readonly" value="无解析"
                                                               class="form-control">
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <hr/>
                                        </c:forEach>
                                    </div>
                                </div>

                            </c:if>
                            <%--填空题--%>
                            <c:if test="${not empty qFillList}">
                                <div class="panel panel-primary">
                                    <div class="panel-heading">
                                        <h4 class="panel-title">填空题</h4>
                                    </div>
                                    <div class="panel-body">
                                        <c:forEach items="${qFillList}" var="question" varStatus="state">
                                            <c:choose>
                                                <c:when test="${fn:contains(wrongList,1)}">
                                                    <span style="color: red">${state.count }.  【错题】 ${question.questionName}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    ${state.count }. ${question.questionName}
                                                </c:otherwise>
                                            </c:choose>
                                            <div class="form-group">
                                                <label>答案</label>
                                                <input type="text" value="${question.answer}" readonly="readonly"
                                                       name="${question.id}" class="form-control">
                                                <br>
                                                <label>解析</label>
                                                <c:choose>
                                                    <c:when test="${question.remark != null}">
                                                         <textarea rows="2" type="text" class="form-control"
                                                                   readonly="readonly">${question.remark}</textarea>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input type="text" readonly="readonly" value="无解析"
                                                               class="form-control">
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                            </c:if>
                            <%--简答题--%>
                            <c:if test="${not empty qSaqList}">
                                <div class="panel panel-primary">
                                    <div class="panel-heading">
                                        <h4 class="panel-title">简答题</h4>
                                    </div>
                                    <div class="panel-body">
                                        <c:forEach items="${qSaqList}" var="question" varStatus="state">
                                            <c:choose>
                                                <c:when test="${fn:contains(wrongList,fn:trim(',').concat(fn:trim(question.id)).concat(','))}">
                                                    <span style="color: red">${state.count }.  【错题】 ${question.questionName}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    ${state.count }. ${question.questionName}
                                                </c:otherwise>
                                            </c:choose>
                                            <div class="form-group">
                                                <label>答案</label>
                                                <textarea rows="5" type="text" readonly="readonly"
                                                          name="${question.id}"
                                                          class="form-control">${question.answer}</textarea>
                                                <br>
                                                <label>解析</label>
                                                <c:choose>
                                                    <c:when test="${question.remark != null}">
                                                         <textarea rows="2" type="text" class="form-control"
                                                                   readonly="readonly">${question.remark}</textarea>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input type="text" readonly="readonly" value="无解析"
                                                               class="form-control">
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div class="form-group">

                                            </div>
                                            <hr/>
                                        </c:forEach>
                                    </div>
                                </div>

                            </c:if>
                            <%--编程题--%>
                            <c:if test="${not empty qProgramList}">
                                <div class="panel panel-primary">
                                    <div class="panel-heading">
                                        <h4 class="panel-title">编程题</h4>
                                    </div>
                                    <div class="panel-body">
                                        <c:forEach items="${qProgramList}" var="question" varStatus="state">
                                            <c:choose>
                                                <c:when test="${fn:contains(wrongList,fn:trim(',').concat(fn:trim(question.id)).concat(','))}">
                                                    <span style="color: red">${state.count }.  【错题】 ${question.questionName}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    ${state.count }. ${question.questionName}
                                                </c:otherwise>
                                            </c:choose>
                                            <div class="form-group">
                                                <label>答案</label>
                                                <input type="text" readonly="readonly" value="${question.answer}"
                                                       name="${question.id}" class="form-control">
                                                <br>
                                                <label>解析</label>
                                                <c:choose>
                                                    <c:when test="${question.remark != null}">
                                                         <textarea rows="2" type="text" class="form-control"
                                                                   readonly="readonly">${question.remark}</textarea>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input type="text" readonly="readonly" value="无解析"
                                                               class="form-control">
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <hr/>
                                        </c:forEach>
                                    </div>
                                </div>
                            </c:if>
                        </ul>
                    </form>
                </div>
            </div>
        </section>
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->
    <!-- 底部 -->
    <%@ include file="../include/footer.jsp" %>
</div>
<!-- ./wrapper -->

<!-- js -->
<%@ include file="../include/js.jsp" %>
<script src="<c:url value="/static/plugins/layer/layer.js"/>"></script>
<script src="<c:url value="/static/plugins/moment/moment.js"/>"></script>
<script src="<c:url value="/static/plugins/jquery-countdown/jquery.countdown.min.js"/>"></script>

<script>
</script>
</body>
</html>

