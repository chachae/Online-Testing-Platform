<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>OES | 专业管理</title>
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
            font-size: 15px;
            vertical-align: center;
        }

        td {
            font-size: 16px;
            vertical-align: center;
        }
    </style>


</head>
<body class="hold-transition skin-blue sidebar-mini">
<!-- Site wrapper -->
<div class="wrapper">
    <%@include file="../teacher/include/header.jsp" %>
    <!-- 左侧菜单栏 -->
    <jsp:include page="../teacher/include/sider.jsp">
        <jsp:param name="menu" value="major"/>
    </jsp:include>
    <!-- 右侧内容部分 -->
    <div class="content-wrapper">
        <!-- Main content -->
        <section class="content">
            <!-- Default box -->
            <div class="box">
                <div class="box-header with-border">
                    <h3 class="box-title">专业管理</h3>
                    <div class="box-tools pull-right">
                        <a id="newBtn" class="btn btn-success btn-sm"><i class="fa fa-plus"></i> 新增专业</a>
                    </div>
                </div>
                <div class="box-body no-padding">
                    <table class="table table-hover">
                        <tbody>
                        <tr>
                            <th style="width:10px"></th>
                            <th>专业ID</th>
                            <th>所属学院</th>
                            <th>专业名称</th>
                            <th>操作
                            <th>
                        </tr>
                        <c:if test="${empty page.list}">
                            <tr>
                                <td colspan="6">没有专业</td>
                            </tr>
                        </c:if>
                        <c:forEach items="${page.list}" var="major">
                            <tr class="rowDetail" rel="${major.id}">
                                <td></td>
                                <td>${major.id}</td>
                                <td rel="${major.academy.id}">${major.academy.name}</td>
                                <td>${major.major}</td>
                                <td><a class="btn btn-success btn-sm editStudent"><i class="fa"></i>编辑</a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
                <!-- /.box-body -->
            </div>
            <!-- /.box -->
            <ul id="pagination-demo" class="pagination-sm pull-right"></ul>
        </section>
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->
    <!-- 增加专业信息模态框 -->
    <!------------------------------------------------------------------------------------------->
    <div class="modal fade" id="saveModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span
                            aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">新增专业</h4>
                </div>
                <div class="modal-body">
                    <div class="form-group form_datetime">
                        <label for="sacademy">所属学院</label>
                        <select id="sacademy" name="sacademy">
                            <option value="">请选择</option>
                            <c:forEach var="academy" items="${academyList}">
                                <option value="${academy.id}">${academy.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group form_datetime">
                        <label for="smajor">专业名称</label>
                        <input type="text" name="smajor" class="form-control"
                               id="smajor">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                    <button type="button" class="btn btn-primary" id="ssaveBtn">确定</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
    </div><!-- /.modal -->
    <!------------------------------------------------------------------------------------------->
    <!-- 修改专业信息模态框 -->
    <!------------------------------------------------------------------------------------------->
    <div class="modal fade" id="modifyModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span
                            aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">修改专业信息</h4>
                </div>
                <div class="modal-body">
                    <div class="form-group form_datetime">
                        <label for="id">ID</label>
                        <input type="text" name="id" class="form-control"
                               id="id" readonly="readonly">
                    </div>
                    <div class="form-group form_datetime">
                        <label for="academy">所属学院</label>
                        <select id="academy" name="academy">
                            <option value="">请选择</option>
                            <c:forEach var="academy" items="${academyList}">
                                <option value="${academy.id}">${academy.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group form_datetime">
                        <label for="major">专业名称</label>
                        <input type="text" name="major" class="form-control"
                               id="major">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                    <button type="button" class="btn btn-primary" id="saveBtn">确定</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
    </div><!-- /.modal -->
    <!------------------------------------------------------------------------------------------->
    <%@include file="../include/footer.jsp" %>
</div>
<!-- ./wrapper -->
<%@include file="../include/js.jsp" %>
<script src="<c:url value="/static/plugins/layer/layer.js"/>"></script>
<script src="<c:url value="/static/plugins/jquery-pagination/jquery.twbsPagination.min.js"/>"></script>
<script>
    //分页
    $('#pagination-demo').twbsPagination({
        totalPages: "${page.pages}",
        visiblePages: 3,
        first: '首页',
        last: '末页',
        prev: '上一页',
        next: '下一页',
        href: "?p={{number}}"
    });

    // 模态框
    // 启动修改模态框
    $(".editStudent").click(function () {
        $("#modifyModal").modal({
            show: true,
            backdrop: 'static'
        });
        // 模态框赋值
        const majorId = $(this).parents('tr').find('td').eq(1).text();
        const academyId = $(this).parents('tr').find('td').eq(2).attr("rel");
        const major = $(this).parents('tr').find('td').eq(3).text();
        $('#id').val(majorId);
        $('#major').val(major);
        $('#academy').val(academyId).select2({width: "100%"});

        $("#saveBtn").click(function () {
            let a = $("#id").val();
            let b = $("#academy").val();
            let c = $("#major").val();
            layer.confirm("确定修改信息吗?", function () {
                $.post("/teacher/major/update/", {
                    "id": a,
                    "academyId": b,
                    "major": c,
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
    });

    // ------------------------------------------------

    // 启动模态框
    $("#newBtn").click(function () {
        $("#saveModal").modal({
            show: true,
            backdrop: 'static'
        });

        // select2
        $('#sacademy').select2({width: "100%"});

        $("#ssaveBtn").click(function () {
            let a = $("#sacademy").val();
            let b = $("#smajor").val();
            layer.confirm("确定增加专业吗?", function () {
                $.post("/teacher/major/save/", {
                    "academyId": a,
                    "major": b,
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
    });
</script>
</body>
</html>


