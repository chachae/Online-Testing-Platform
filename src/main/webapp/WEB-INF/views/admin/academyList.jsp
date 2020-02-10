<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>OES | 学院列表</title>
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
<body class="hold-transition skin-purple">
<!-- Site wrapper -->
<div class="wrapper">
    <%@include file="../admin/include/header.jsp" %>
    <!-- 左侧菜单栏 -->
    <jsp:include page="../admin/include/sider.jsp">
        <jsp:param name="menu" value="academy"/>
    </jsp:include>
    <!-- 右侧内容部分 -->
    <div class="content-wrapper">
        <!-- Main content -->
        <section class="content">
            <!-- Default box -->
            <div class="box">
                <div class="box-header with-border">
                    <h3 class="box-title">学院管理</h3>
                    <div class="box-tools pull-right">
                        <a id="newBtn" class="btn btn-success btn-sm"><i class="fa fa-plus"></i> 新增学院</a>
                    </div>
                </div>
                <div class="box-body no-padding">
                    <table class="table table-hover">
                        <tbody>
                        <tr>
                            <th style="width:10px"></th>
                            <th>学院 ID</th>
                            <th>学院名称</th>
                            <th>操作
                            <th>
                        </tr>
                        <c:if test="${empty academyList}">
                            <tr>
                                <td colspan="6">没有学院</td>
                            </tr>
                        </c:if>
                        <c:forEach items="${academyList}" var="academy">
                            <tr class="rowDetail" rel="${academy.id}">
                                <td></td>
                                <td>${academy.id}</td>
                                <td>${academy.name}</td>
                                <td><a class="btn btn-success btn-sm editAcademy"><i class="fa"></i>编辑</a>
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

    <!-- 增加学生信息模态框 -->
    <!------------------------------------------------------------------------------------------->
    <div class="modal fade" id="saveModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span
                            aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">增加学院</h4>
                </div>
                <div class="modal-body">
                    <div class="form-group form_datetime">
                        <label for="sname">学院名称</label>
                        <input type="text" name="name" class="form-control"
                               id="sname">
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        <button type="button" class="btn btn-primary" id="ssaveBtn">确定</button>
                    </div>
                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div><!-- /.modal -->
    </div>
    <!------------------------------------------------------------------------------------------->
    <!-- 修改学生信息模态框 -->
    <!------------------------------------------------------------------------------------------->
    <div class="modal fade" id="modifyModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span
                            aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">修改学院信息</h4>
                </div>
                <div class="modal-body">
                    <div class="form-group form_datetime">
                        <label for="id">ID</label>
                        <input type="text" name="id" class="form-control"
                               id="id" readonly="readonly">
                    </div>
                    <div class="form-group form_datetime">
                        <label for="name">名称</label>
                        <input type="text" name="name" class="form-control"
                               id="name">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                    <button type="button" class="btn btn-primary" id="saveBtn">确定</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
    </div><!-- /.modal -->
    <!-- ./wrapper -->
    <%@include file="../include/js.jsp" %>
    <script src="<c:url value="/static/plugins/layer/layer.js"/>"></script>
    <script src="<c:url value="/static/bootstrap/js/jquery.twbsPagination.min.js"/>"></script>
    <script>
        // 模态框
        // 启动修改模态框
        $(".editAcademy").click(function () {
            $("#modifyModal").modal({
                show: true,
                backdrop: 'static'
            });

            // 模态框赋值
            const id = $(this).parents('tr').find('td').eq(1).text();
            const name = $(this).parents('tr').find('td').eq(2).text();
            $('#id').val(id);
            $('#name').val(name);
        });

        $("#saveBtn").click(function () {
            let a = $("#id").val();
            let b = $("#name").val();
            layer.confirm("确定修改信息吗?", function () {
                $.post("/admin/academy/update", {
                    "id": a,
                    "name": b,
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

        // 启动模态框
        $("#newBtn").click(function () {
            $("#saveModal").modal({
                show: true,
                backdrop: 'static'
            });

            $("#ssaveBtn").click(function () {
                let a = $("#sname").val();
                layer.confirm("确定增加学院吗?", function () {
                    $.post("/admin/academy/save", {
                        "name": a,
                    }).done(function (data) {
                        if (data.state === "success") {
                            layer.msg("增加成功!");
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


