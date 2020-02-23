<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>OES | 学生作答记录</title>
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
        <jsp:param name="menu" value="review"/>
    </jsp:include>
    <!-- 右侧内容部分 -->
    <div class="content-wrapper">
        <!-- /.box-body -->
        <!-- 修改成绩模态框 -->
        <div class="modal fade" id="modifyModal">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal"><span
                                aria-hidden="true">&times;</span>
                        </button>
                        <h4 class="modal-title">修改主观题得分</h4>
                    </div>
                    <div class="modal-body">
                        <div class="form-group form_datetime">
                            <input type="hidden" name="id" class="form-control"
                                   id="id">
                            <!-- 原来的分数禁止修改 -->
                            <label for="oldScore">原来分数</label>
                            <input type="text" name="oldScore" class="form-control"
                                   id="oldScore" readonly="readonly">
                        </div>
                        <div class="form-group form_datetime">
                            <label for="newScore">新的分数</label>
                            <input type="text" name="newScore" class="form-control" id="newScore">
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        <button type="button" class="btn btn-primary" id="saveBtn">确定</button>
                    </div>
                </div>
                <!-- /.modal-content -->
            </div>
            <!-- /.modal-dialog -->
        </div>
        <!-- /.modal -->

        <section class="content">
            <div class="box">
                <div class="box-header with-border">
                    <h3 class="box-title">试卷详情</h3>
                    <div class="box-tools">
                        <a href="<c:url value="/teacher/reviewPaper"/>" class="btn btn-primary btn-sm"><i
                                class="fa fa-arrow-left"></i> 返回列表</a>
                    </div>
                </div>
                <!-- /.box-header -->
                <table class="table table-condensed">
                    <tr>
                        <td>
                            试卷编号：${paper.id}<br>
                            考试名称：${paper.paperName}<br>
                            试卷分值：${paper.score}<br>
                            考试时间：${paper.beginTime}<br>
                            结束时间：${paper.endTime}<br>
                            考试类型：${paper.paperType}考试<br>
                        </td>
                    </tr>
                </table>
            </div>

            <div class="box ">
                <div class="box-body ">
                    <table class="table table-hover">
                        <th>题库答案</th>
                        <tbody>
                        <c:if test="${empty questionList}">
                            <tr>
                                <td colspan="6">未找到主观题</td>
                            </tr>
                        </c:if>
                        <c:forEach items="${questionList}" var="question" varStatus="vs">
                            <tr class="rowDetail success" rel="${question.id}">
                                <td>${vs.count}. ${question.questionName}</td>
                            </tr>
                            <tr class="rowDetail" rel="${question.id}">
                                <td><b style="color: green">[ 标准答案 ]</b><br>${question.answer}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>

            <div class="box">
                <div class="box-body">
                    <table class="table table-hover">
                        <th>学生答案（点击学生答案进行主观题分数修改）</th>
                        <tbody>
                        <c:if test="${empty stuAnswer}">
                            <tr>
                                <td colspan="6">未找到主观题</td>
                            </tr>
                        </c:if>
                        <c:forEach items="${stuAnswer}" var="answer" varStatus="vs">
                            <tr class="danger">
                                <td>
                                    <b>
                                        姓名：${answer.student.name}<br>
                                        学号：${answer.student.stuNumber}<br>
                                        成绩：${answer.score.score}
                                    </b>
                                </td>
                            </tr>
                            <c:forEach items="${answer.records}" var="record" varStatus="rvs">
                                <tr class="success">
                                    <td>${rvs.count}. ${record.questionName} <b
                                            class="pull-right">题目得分：${record.score}分</b></td>
                                </tr>
                                <tr>
                                    <td class="editAns" rel="${record.id}"><b style="color: red">[ 学生答案
                                        ]</b><br>${record.answer}</td>
                                    <td hidden> ${record.score}</td>
                                </tr>
                            </c:forEach>
                            <tr>
                                <td>
                                    <h4 style="text-align: center"><b>[ ${answer.student.name} ]</b> 主观题结束查阅</h4>
                                    <hr>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>

                </div>
            </div>
        </section>
    </div>
    <!-- /.content-wrapper -->
    <!-- 底部 -->
    <%@ include file="../include/footer.jsp" %>
</div>
<!-- 公共 js -->
<%@ include file="../include/js.jsp" %>
<script src="<c:url value="/static/plugins/layer/layer.js"/>"></script>
<script src="<c:url value="/static/plugins/moment/moment.js"/>"></script>
<script src="<c:url value="/static/plugins/jquery-countdown/jquery.countdown.min.js"/>"></script>
<!-- BSA AdPacks code. Please ignore and remove.-->
<script>

    // 启动模态框
    $(".editAns").click(function () {
        $("#modifyModal").modal({
            show: true,
            backdrop: 'static'
        });
        const oldScore = $(this).parents('tr').find('td').eq(1).text();
        $('#oldScore').val(oldScore);
        // 题目id
        const id = $(this).attr("rel");
        $('#id').val(id);
    });

    $("#saveBtn").click(function () {
        layer.confirm("确定修改分数么?", function () {
            let oldScore = $("#oldScore").val();
            let newScore = $("#newScore").val();
            let id = $("#id").val();
            $.post("/teacher/editScore", {
                "oldScore": oldScore,
                "newScore": newScore,
                "id": id,
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
</script>
</body>
</html>

