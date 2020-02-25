<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
<body class="hold-transition skin-blue sidebar-mini">
<!-- Site wrapper -->
<div class="wrapper">
    <!-- 顶部导航栏部分 -->
    <%@ include file="../teacher/include/header.jsp" %>
    <!-- 左侧菜单栏 -->
    <jsp:include page="../teacher/include/sider.jsp">
        <jsp:param name="menu" value="paper"/>
    </jsp:include>
    <!-- 右侧内容部分 -->
    <div class="content-wrapper">
        <!-- Main content -->
        <section class="content">
            <div class="box">
                <div class="box-header with-border">
                    <h3 class="box-title">试卷信息</h3>
                    <div class="box-tools">
                        <a href="<c:url value="/teacher/paper"/>" class="btn btn-primary btn-sm"><i
                                class="fa fa-arrow-left"></i> 返回列表</a>

                        <button id="delBtn" class="btn btn-danger btn-sm"><i class="fa fa-trash-o"></i> 删除试卷</button>
                    </div>
                </div>
                <div class="box-body ">

                    <table class="table">
                        <tr>
                            <th class="td_title" style="background-color: #e4e4e4">试卷名称: ${paper.paperName}</th>
                        </tr>
                        <tr>
                            <td class="td_title">试卷题目：${paper.questionId}</td>
                        </tr>
                        <tr>
                            <td class="td_title">开始时间：${paper.beginTime}</td>
                        </tr>
                        <tr>
                            <td class="td_title">结束时间：${paper.endTime}</td>
                        </tr>
                        <tr>
                            <td class="td_title">考试时长：${paper.allowTime}</td>
                        </tr>
                        <tr>
                            <td class="td_title">试卷总分：${paper.score} 分</td>
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
                            <td class="td_title">试卷类型： ${paper.paperType}考试</td>
                        </tr>
                        <tr>
                            <td class="td_title">考试课程：《${course.courseName}》</td>
                        </tr>
                        <tr>
                            <td class="td_title">考试专业：${major.major}</td>
                        </tr>
                    </table>
                </div>
            </div>

            <!-- 修改答案模态框 -->
            <!------------------------------------------------------------------------------------------->
            <div class="modal fade editAns">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal"><span
                                    aria-hidden="true">&times;</span>
                            </button>
                            <h4 class="modal-title">修改答案</h4>
                        </div>
                        <div class="modal-body">
                            <div class="form-group form_datetime">
                                <label for="modalAns">ID</label>
                                <input type="text" readonly="readonly" name="modalAns" class="form-control"
                                       id="modalAnsId">
                                <label for="modalAns">答案</label>
                                <input type="text" name="modalAns" class="form-control"
                                       id="modalAns">
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                            <button type="button" class="btn btn-primary subEditAns">确定</button>
                        </div>
                    </div><!-- /.modal-content -->
                </div><!-- /.modal-dialog -->
            </div><!-- /.modal -->

            <!-- ///// -->

            <!-- 修改题目模态框（已知） -->
            <!------------------------------------------------------------------------------------------->
            <div class="modal fade editQueAll">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal"><span
                                    aria-hidden="true">&times;</span>
                            </button>
                            <h4 class="modal-title">修改题目</h4>
                        </div>
                        <div class="modal-body">
                            <div class="form-group form_datetime">
                                <label for="modalQueOldId">原题目 ID</label>
                                <input type="text" readonly="readonly" name="modalQueOldId" class="form-control"
                                       id="modalQueOldId">
                                <label for="modalQueNewId">新题目 ID</label>
                                <input type="text" name="modalQueNewId" class="form-control"
                                       id="modalQueNewId">
                                <input hidden="hidden" id="modalQueType">

                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                            <button type="button" class="btn btn-primary subEditQueExist">确定</button>
                        </div>
                    </div><!-- /.modal-content -->
                </div><!-- /.modal-dialog -->
            </div><!-- /.modal -->


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
                                            ${state.count }. ${choice.questionName}
                                            <div class="box-tools pull-right" rel="${choice.id}">
                                                <a class="btn btn-success btn-sm openEditChoiceAnsBtn"
                                                   rel="${choice.answer}">修改答案</a>
                                                <a id="editQue" class="btn btn-success btn-sm editExistBtn"
                                                   rel="${choice.answer}">修改题目（从题库中找）</a>
                                            </div>
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
                                            </div>
                                            <hr>
                                            <br>
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
                                            ${state.count }. ${qMulChoice.questionName}
                                            <div class="box-tools pull-right" rel="${qMulChoice.id}">
                                                <a class="btn btn-success btn-sm openEditChoiceAnsBtn"
                                                   rel="${qMulChoice.answer}">修改答案</a>
                                                <a class="btn btn-success btn-sm editExistBtn"
                                                   rel="${qMulChoice.answer}">修改题目（从题库中找）</a>
                                            </div>
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
                                            ${state.count }.
                                            <span class="ansName">${question.questionName}</span>
                                            <div class="box-tools pull-right" rel="${question.id}">
                                                <a class="btn btn-success btn-sm openEditChoiceAnsBtn"
                                                   rel="${question.answer}">修改答案</a>
                                                <a class="btn btn-success btn-sm editExistBtn"
                                                   rel="${question.answer}">修改题目（从题库中找）</a>
                                            </div>
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
                                            ${state.count }.
                                            <span class="ansName">${question.questionName}</span>
                                            <div class="box-tools pull-right" rel="${question.id}">
                                                <a class="btn btn-success btn-sm openEditChoiceAnsBtn"
                                                   rel="${question.answer}">修改答案</a>
                                                <a class="btn btn-success btn-sm editExistBtn"
                                                   rel="${question.answer}">修改题目（从题库中找）</a>
                                            </div>
                                            <div class="form-group">
                                                <label>答案</label>
                                                <input type="text" value="${question.answer}" readonly="readonly"
                                                       name="${question.id}" class="form-control">
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
                                            ${state.count }.
                                            <span class="ansName">${question.questionName}</span>
                                            <div class="box-tools pull-right" rel="${question.id}">
                                                <a class="btn btn-success btn-sm openEditChoiceAnsBtn"
                                                   rel="${question.answer}">修改答案</a>
                                                <a class="btn btn-success btn-sm editExistBtn"
                                                   rel="${question.answer}">修改题目（从题库中找）</a>
                                            </div>
                                            <div class="form-group">
                                                <label>答案</label>
                                                <textarea rows="5" type="text" readonly="readonly"
                                                          name="${question.id}"
                                                          class="form-control">${question.answer}</textarea>
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
                                            ${state.count }.
                                            <span class="ansName">${question.questionName}</span>
                                            <div class="box-tools pull-right" rel="${question.id}">
                                                <a class="btn btn-success btn-sm openEditChoiceAnsBtn"
                                                   rel="${question.answer}">修改答案</a>
                                                <a class="btn btn-success btn-sm editExistBtn"
                                                   rel="${question.answer}">修改题目（从题库中找）</a>
                                            </div>
                                            <div class="form-group">
                                                <label>答案</label>
                                                <input type="text" readonly="readonly" value="${question.answer}"
                                                       name="${question.id}" class="form-control">
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

    // 删除试卷
    $(function () {
        var paperId = ${paper.id};
        //删除试卷
        $("#delBtn").click(function () {
            layer.confirm("确定要删除试卷吗？",
                function () {
                    $.post("/teacher/paper/delete/" + paperId).done(function (data) {
                        if (data.state === "success") {
                            layer.msg("删除成功!");
                            window.location.href = "/teacher/paper";
                        } else {
                            layer.msg(data.message);
                        }
                    }).error(function () {
                        layer.msg("服务器异常");
                    })
                });
        });
    });

    // 打开修改答案模态框
    $(".openEditChoiceAnsBtn").click(function () {

        var x = ${paper.paperType == '模拟'};
        var y = !${paper.start};
        // 获取考试时间
        if (x || y) {
            $(".editAns").modal({
                show: true,
                backdrop: 'static'
            });
            // 题目id
            const b = $(this).parents().attr('rel');
            // 原答案
            const a = $(this).attr("rel");
            $('#modalAns').val(a);
            $('#modalAnsId').val(b);
        } else {
            layer.msg("只允许修改未开始的考试和模拟考试！")
        }
    });

    //提交答案修改
    $('.subEditAns').click(function () {
        let a = $('#modalAns').val();
        let b = $('#modalAnsId').val();
        layer.confirm("确定修改答案吗?", function () {
            $.post("/teacher/paper/updateAnswer/", {
                "id": b,
                "answer": a,
            }).done(function (data) {
                if (data.state === "success") {
                    layer.msg("修改成功!");
                    window.location.reload();
                }
            }).error(function () {
                layer.msg("服务器异常");
            });
        });
    });


    // 打开修改题目模态框
    $(".editExistBtn").click(function () {
        var x = ${paper.paperType == '模拟'};
        var y = !${paper.start};
        // 获取考试时间
        if (x || y) {
            $(".editQueAll").modal({
                show: true,
                backdrop: 'static'
            });
            // 题目id
            const b = $(this).parents().attr('rel');
            $('#modalQueOldId').val(b);
            // 题目类型id

        } else {
            layer.msg("只允许修改未开始的考试和模拟考试！")
        }
    });

    //提交答案修改
    $('.subEditQueExist').click(function () {
        let a = $('#modalQueOldId').val();
        let b = $('#modalQueNewId').val();
        layer.confirm("确定修改題目吗?", function () {
            $.post("/teacher/paper/updateQuestion/", {
                "oldId": a,
                "newId": b,
                "paperId": "${paper.id}"
            }).done(function (data) {
                if (data.state === "success") {
                    layer.msg("修改成功!");
                    window.location.reload();
                } else {
                    $('#modalQueNewId').val("")
                    layer.msg(data.message);
                }
            }).error(function () {
                layer.msg("服务器异常");
            });
        });
    });
</script>
</body>
</html>

