<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>OES | 试卷模板列表</title>
    <!-- css style -->
    <%@ include file="../include/css.jsp" %>
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
                    <h3 class="box-title">模版列表</h3>
                    <div class="box-tools">
                        <a href="<c:url value="/teacher/paper"/>" class="btn btn-primary btn-sm"><i
                                class="fa fa-arrow-left"></i> 返回列表</a>
                    </div>
                </div>
                <div class="box-body no-padding">
                    <table class="table table-hover table-bordered">
                        <thead>
                        <tr>
                            <th></th>
                            <th>题型</th>
                            <th>数量</th>
                            <th>分数</th>
                            <th>题型</th>
                            <th>数量</th>
                            <th>分数</th>
                            <th>题型</th>
                            <th>数量</th>
                            <th>分数</th>
                            <th>题型</th>
                            <th>数量</th>
                            <th>分数</th>
                            <th>题型</th>
                            <th>数量</th>
                            <th>分数</th>
                            <th>题型</th>
                            <th>数量</th>
                            <th>分数</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${formList}" var="form" varStatus="vs">
                            <tr>
                                <th scope="row">模版 ${vs.count}</th>
                                <td>单选题</td>
                                <td> ${form.QChoiceNum} </td>
                                <td> ${form.QChoiceScore}</td>
                                <td>多选题</td>
                                <td> ${form.QMulChoiceNum} </td>
                                <td> ${form.QMulChoiceScore}</td>
                                <td>判断题</td>
                                <td> ${form.QTofNum} </td>
                                <td> ${form.QTofScore}</td>
                                <td>填空题</td>
                                <td> ${form.QFillNum} </td>
                                <td> ${form.QFillScore}</td>
                                <td>简答题</td>
                                <td> ${form.QSaqNum} </td>
                                <td> ${form.QSaqScore}</td>
                                <td>编程题</td>
                                <td> ${form.QProgramNum} </td>
                                <td> ${form.QProgramScore}</td>
                                <td><a href="/teacher/paper/newPaper/${form.id}">使用</a> <a class="rowDel"
                                                                                           rel="${form.id}">删除</a></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
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

<script>

    // 操作试卷模板
    $(function () {
        $(".rowDel").click(function () {
            const id = $(this).attr("rel");
            layer.confirm("确定删除该模版么?", function () {
                $.get("/teacher/paper/delPaperForm/" + id).done(function (data) {
                    if (data.state === "success") {
                        layer.msg("删除成功!");
                        window.location.href = "/teacher/paper/showPaperForm";
                    } else {
                        layer.msg(data.messge);
                    }
                }).error(function () {
                    layer.msg("服务器异常");
                });
            });
        });
    });
</script>
</body>
</html>

