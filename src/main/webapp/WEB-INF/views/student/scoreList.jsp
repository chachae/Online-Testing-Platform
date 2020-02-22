<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>OES | 分数列表</title>
    <!-- Tell the browser to be responsive to screen width -->
    <%@include file="../include/css.jsp" %>

    <style>

        .table > tbody > tr:hover {
            cursor: pointer;
        }

        .table > tbody > tr > td {
            vertical-align: middle;
        }

        th {
            font-size: 16px;
            text-align: center;
        }

        td {
            font-size: 16px;
            text-align: center;
        }

    </style>


</head>
<body class="hold-transition skin-green sidebar-mini">
<!-- Site wrapper -->
<div class="wrapper">

    <%@include file="../student/include/header.jsp" %>
    <!-- 左侧菜单栏 -->
    <jsp:include page="../student/include/sider.jsp">
        <jsp:param name="menu" value="score"/>
    </jsp:include>
    <!-- 右侧内容部分 -->
    <div class="content-wrapper">
        <!-- Main content -->
        <section class="content">
            <!-- Default box -->
            <div class="box">
                <div class="box-header with-border">
                    <h3 class="box-title">成绩列表</h3>
                    <div class="box-tools pull-right">
                    </div>
                </div>
                <div class="box-body no-padding">
                    <table class="table table-hover">
                        <tbody>
                        <c:if test="${empty scoreList}">
                            <tr>
                                <td colspan="6">暂无成绩</td>
                            </tr>
                        </c:if>
                        <c:if test="${not empty scoreList}">
                            <tr>
                                <th>序号</th>
                                <th>试卷名称</th>
                                <th>考试分数</th>
                                <th>错题ID集合</th>
                                <th>操作
                                <th>
                            </tr>
                        </c:if>
                        <c:forEach items="${scoreList}" var="score" varStatus="state">
                            <tr class="rowDetail" rel="${score.id}">
                                <td>${state.count}</td>
                                <td>${score.paperName}</td>
                                <td>${score.score} 分</td>
                                <td>${score.wrongIds}</td>
                                <td>
                                    <button class="btn btn-vk detailBtn">查看试卷详情</button>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
                <!-- /.box-body -->
            </div>
        </section>
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->
    <%@include file="../include/footer.jsp" %>
</div>
<!-- ./wrapper -->
<%@include file="../include/js.jsp" %>
<script src="<c:url value="/static/plugins/layer/layer.js"/>"></script>
<script src="/static/plugins/moment/moment.js"></script>
<script src="/static/plugins/jquery-countdown/jquery.countdown.min.js"></script>
<script>
    $('.detailBtn').click(function () {
        var id = $(this).parents('tr').eq(0).attr('rel');
        console.log(id);
        window.location.href = "/student/score/detail/" + id;
    })
</script>
</body>
</html>


