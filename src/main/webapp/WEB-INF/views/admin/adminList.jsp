<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>OES | 管理员列表</title>
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
            align-content: center;
        }

        td {
            font-size: 16px;
            vertical-align: center;
            align-content: center;
        }
    </style>
</head>
<body class="hold-transition skin-purple sidebar-mini">
<!-- Site wrapper -->
<div class="wrapper">
    <%@include file="../admin/include/header.jsp" %>
    <!-- 左侧菜单栏 -->
    <jsp:include page="../admin/include/sider.jsp">
        <jsp:param name="menu" value="admin"/>
    </jsp:include>
    <!-- 右侧内容部分 -->
    <div class="content-wrapper">
        <!-- Main content -->
        <section class="content">
            <!-- Default box -->
            <div class="box">
                <div class="box-header with-border">
                    <h3 class="box-title">管理员管理</h3>
                    <div class="box-tools pull-right">
                        <a id="newBtn" class="btn btn-success btn-sm"><i class="fa fa-plus"></i> 新增管理员</a>
                    </div>
                </div>
                <div class="box-body no-padding">
                    <table class="table table-hover">
                        <tbody>
                        <tr>
                            <th width="10"></th>
                            <th width="300">ID</th>
                            <th width="300">姓名</th>
                            <th width="300">用户名</th>
                            <th width="300">上次登陆时间</th>
                            <th width="300">操作</th>
                        </tr>
                        <c:if test="${empty page.list}">
                            <tr>
                                <td colspan="6">没有学生</td>
                            </tr>
                        </c:if>
                        <c:forEach items="${page.list}" var="admin">
                            <tr class="rowDetail" rel="${admin.id}">
                                <td></td>
                                <td>${admin.id}</td>
                                <td>${admin.name}</td>
                                <td>${admin.number}</td>
                                <td><fmt:formatDate value="${admin.lastLoginTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                                <td rel="${admin.id}"><a class="btn btn-danger btn-sm delAdmin"><i
                                        class="fa"></i>删除</a>
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
    <!------------------------------------------------------------------------------------------->

    <!-- 增加管理员信息模态框 -->
    <!------------------------------------------------------------------------------------------->
    <div class="modal fade" id="saveModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span
                            aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">增加管理员</h4>
                </div>
                <div class="modal-body">
                    <div class="form-group form_datetime">
                        <label for="name">姓名</label>
                        <input type="text" name="name" class="form-control"
                               id="name">
                    </div>
                    <div class="form-group form_datetime">
                        <label for="number">用户名</label>
                        <input type="text" name="number" class="form-control"
                               id="number">
                    </div>
                    <div class="form-group form_datetime">
                        <label for="pass">密码</label>
                        <input type="password" name="pass" class="form-control"
                               id="pass">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                    <button type="button" class="btn btn-primary" id="saveBtn">确定</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
    </div><!-- /.modal -->

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

    // 删除
    $('.delAdmin').click(function () {
        // 获取 rel 中的管理员id
        const id = $(this).parents().attr('rel');
        layer.confirm("确定刪除管理员吗?", function () {
            $.post("/admin/delete/" + id).done(function (data) {
                if (data.state === "success") {
                    layer.msg("删除成功!");
                    window.location.reload();
                } else {
                    layer.msg(data.message);
                }
            }).error(function () {
                layer.msg("服务器异常");
            });
        });
    })

    // 打开模态框
    $("#newBtn").click(function () {
        $("#saveModal").modal({
            show: true,
            backdrop: 'static'
        });
    });

    // 新增
    $('#saveBtn').click(function () {
        const a = $('#name').val();
        const b = $('#number').val();
        const c = $('#pass').val();
        $.post("/admin/save", {
            name: a,
            number: b,
            password: c
        }).done(function (data) {
            if (data.state === "success") {
                layer.msg("增加成功!");
                window.location.reload();
            } else {
                layer.msg(data.message);
            }
        }).error(function () {
            layer.msg("服务器异常");
        });
    })
</script>
</body>
</html>


