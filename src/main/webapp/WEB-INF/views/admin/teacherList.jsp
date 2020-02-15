<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>OES | 教师列表</title>
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
        <jsp:param name="menu" value="teacher"/>
    </jsp:include>
    <!-- 右侧内容部分 -->
    <div class="content-wrapper">
        <!-- Main content -->
        <section class="content">
            <!-- Default box -->
            <div class="box">
                <div class="box-header with-border">
                    <h3 class="box-title">教师管理</h3>
                    <div class="box-tools pull-right">
                        <a id="newBtn" class="btn btn-success btn-sm"><i class="fa fa-plus"></i> 新增教师</a>
                    </div>
                </div>
                <div class="box-body no-padding">
                    <table class="table table-hover">
                        <tbody>
                        <tr>
                            <th style="width:10px"></th>
                            <th width="300">教师 ID</th>
                            <th width="300">姓名</th>
                            <th width="300">工号</th>
                            <th width="300">职位</th>
                            <th width="300">性别</th>
                            <th width="300">操作<th>
                        </tr>
                        <c:if test="${empty page.list}">
                            <tr>
                                <td colspan="6">没有教师</td>
                            </tr>
                        </c:if>
                        <c:forEach items="${page.list}" var="teacher">
                            <tr class="rowDetail" rel="${teacher.workNumber}">
                                <td></td>
                                <td>${teacher.id}</td>
                                <td>${teacher.name}</td>
                                <td>${teacher.workNumber}</td>
                                <td>${teacher.job}</td>
                                <td>${teacher.sex}</td>
                                <td rel="${teacher.id}">
                                    <a class="btn btn-success btn-sm editTeacher"><i class="fa"></i>编辑</a>
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
                    <h4 class="modal-title">增加教师信息</h4>
                </div>
                <div class="modal-body">
                    <div class="form-group form_datetime">
                        <label for="sname">姓名</label>
                        <input type="text" name="sname" class="form-control"
                               id="sname">
                    </div>
                    <div class="form-group form_datetime">
                        <label for="sworkNumber">工号</label>
                        <input type="text" name="sworkNumber" class="form-control"
                               id="sworkNumber">
                    </div>
                    <div class="form-group form_datetime">
                        <label for="spass">密码</label>
                        <input type="text" name="spass" class="form-control"
                               id="spass">
                    </div>
                    <div class="form-group form_datetime">
                        <label for="sjob">职位</label>
                        <select id="sjob" name="sjob" style="width: 100%">
                            <option value="">请选择</option>
                            <option value="教授">教授</option>
                            <option value="副教授">副教授</option>
                            <option value="讲师">讲师</option>
                            <option value="高级讲师">高级讲师</option>
                        </select>
                    </div>
                    <div class="form-group form_datetime">
                        <label for="ssex">性别</label>
                        <select id="ssex" class="form-control" style="width: 100%">
                            <option value="">请选择</option>
                            <option value="男">男</option>
                            <option value="女">女</option>
                        </select>
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
    <!-- 修改教师信息模态框 -->
    <!------------------------------------------------------------------------------------------->
    <div class="modal fade" id="modifyModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span
                            aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">修改教师信息</h4>
                </div>
                <div class="modal-body">
                    <div class="form-group form_datetime">
                        <label for="id">ID</label>
                        <input type="text" name="id" class="form-control"
                               id="id" readonly="readonly">
                    </div>
                    <div class="form-group form_datetime">
                        <label for="name">姓名</label>
                        <input type="text" name="name" class="form-control"
                               id="name">
                    </div>
                    <div class="form-group form_datetime">
                        <label for="workNumber">工号</label>
                        <input type="text" name="workNumber" class="form-control"
                               id="workNumber" readonly="readonly">
                    </div>
                    <div class="form-group form_datetime">
                        <label for="job">职位</label>
                        <select id="job" name="job">
                            <option value="">请选择</option>
                            <option value="教授">教授</option>
                            <option value="副教授">副教授</option>
                            <option value="讲师">讲师</option>
                            <option value="高级讲师">高级讲师</option>
                        </select>
                    </div>
                    <div class="form-group form_datetime">
                        <label for="sex">性别</label>
                        <select id="sex" class="form-control" style="width: 100%">
                            <option value="">请选择</option>
                            <option value="男">男</option>
                            <option value="女">女</option>
                        </select>
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
    $(".editTeacher").click(function () {
        $("#modifyModal").modal({
            show: true,
            backdrop: 'static'
        });

        // 模态框赋值
        const id = $(this).parents('tr').find('td').eq(1).text();
        const name = $(this).parents('tr').find('td').eq(2).text();
        const workNumber = $(this).parents('tr').find('td').eq(3).text();
        const job = $(this).parents('tr').find('td').eq(4).text();
        const sex = $(this).parents('tr').find('td').eq(5).text();
        $('#id').val(id);
        $('#name').val(name);
        $('#workNumber').val(workNumber);
        $('#job').val(job).select2({width: "100%"});
        $('#sex').val(sex).select2({width: "100%"});

        $("#sex option").each(function () {
            if ($(this).val() === sex) {
                $(this).attr('selected', true);
            }
        });

        // select2
        $("#ssex").select2();

        $("#saveBtn").click(function () {
            let a = $("#id").val();
            let b = $("#name").val();
            let c = $("#workNumber").val();
            let d = $("#job").val();
            let e = $("#sex").val();
            layer.confirm("确定修改信息吗?", function () {
                $.post("/admin/teacher/update/", {
                    "id": a,
                    "name": b,
                    "workNumber": c,
                    "job": d,
                    "sex": e,
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

    // 启动模态框
    $("#newBtn").click(function () {
        $("#saveModal").modal({
            show: true,
            backdrop: 'static'
        });

        $("#sjob").select2();
        $("#ssex").select2();

        // 增加教师
        $("#ssaveBtn").click(function () {
            let b = $("#sname").val();
            let c = $("#sworkNumber").val();
            let d = $("#sjob").val();
            let e = $("#ssex").val();
            let f = $("#spass").val();
            layer.confirm("确定增加学教师吗?", function () {
                $.post("/admin/teacher/save/", {
                    "name": b,
                    "workNumber": c,
                    "job": d,
                    "sex": e,
                    "password": f,
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
            });
        });
    });
</script>
</body>
</html>


