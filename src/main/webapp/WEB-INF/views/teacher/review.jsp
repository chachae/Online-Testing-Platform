<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>OES | 试卷复核</title>
    <%@ include file="../include/css.jsp" %>
</head>
<body class="hold-transition skin-blue sidebar-mini">
<!-- Site wrapper -->
<div class="wrapper">
    <%@include file="../teacher/include/header.jsp" %>
    <jsp:include page="../teacher/include/sider.jsp">
        <jsp:param name="menu" value="review"/>
    </jsp:include>
    <!-- 右侧内容部分 -->
    <div class="content-wrapper">
        <!-- Main content -->
        <section class="content">
            <form method="get" action="<c:url value="/teacher/reviewRes"/>"
                  style="z-index: 999; position: relative">
                <div class="form-group col-sm-12">
                    <label class="control-label col-sm-2" for="paperId"><h4 style="font-weight: 800">请选择试卷名称</h4>
                    </label>
                    <div class="col-sm-8">
                        <select class="form-control " id="paperId" name="paperId">
                            <option value="">请选择</option>
                            <c:forEach items="${paperList}" var="paper">
                                <option value="${paper.id}">${paper.paperName}</option>
                            </c:forEach>
                        </select>
                        <button id="search" class="form-control btn btn-vk btn-flat">查找</button>
                        <c:if test="${!empty message}">
                            <h4 style="color: red;text-align: center"><b>${message}</b></h4>
                        </c:if>
                    </div>
                </div>
            </form>
        </section>
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->
    <%@ include file="../include/footer.jsp" %>
</div>
<!-- ./wrapper -->
<%@include file="../include/js.jsp" %>
<script src="<c:url value="/static/plugins/echarts/echarts.min.js"/>"></script>
<script src="<c:url value="/static/plugins/layer/layer.js"/>"></script>
</body>
<script>
    // select2
    $('#paperId').select2({width: "100%"});
</script>
</html>