<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>OES | 学生列表</title>
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
<body class="hold-transition skin-blue sidebar-mini">
<!-- Site wrapper -->
<div class="wrapper">
    <%@include file="../teacher/include/header.jsp" %>
    <!-- 左侧菜单栏 -->
    <jsp:include page="../teacher/include/sider.jsp">
        <jsp:param name="menu" value="student"/>
    </jsp:include>
    <!-- 右侧内容部分 -->
    <div class="content-wrapper">
        <!-- Main content -->
        <section class="content">
            <!-- Default box -->
            <div class="box">
                <div class="box-header with-border">
                    <h3 class="box-title">学生查询</h3>
                    <div class="box-tools pull-right">
                        <a href="<c:url value="/teacher/student"/>" class="btn btn-success btn-sm"><i
                                class="fa  fa-refresh"></i> 刷新</a>
                    </div>
                </div>
                <div class="box-body no-padding">
                    <form type="get" action="<c:url value="/teacher/student"/>">
                        <table class="table table-hover" width="100%" style="table-layout:fixed">
                            <tr class="rowDetail">
                                <td width="100">
                                    <label for="stuName"> 姓名：</label>
                                    <input width="100px" class="form-control find" id="stuName" name="name" type="text">
                                </td>
                                <td width="100">
                                    <label for="findAcademy">学院</label>
                                    <select id="findAcademy" name="academyId" class="form-control academy find"
                                            onchange="getAcademy(this.value)">
                                        <option value="">请选择</option>
                                        <c:forEach items="${academyList}" var="academy">
                                            <option value="${academy.id}">${academy.name}</option>
                                        </c:forEach>
                                    </select>
                                </td>
                                <td width="100">
                                    <label for="findMajor">专业</label>
                                    <select id="findMajor" name="majorId" class="form-control major find"
                                            aria-selected="">
                                        <option value="">请选择</option>
                                    </select>
                                </td>
                                <td width="100">
                                    <label for="goBtn"></label>
                                    <button type="submit" class="btn btn-block btn-primary" id="goBtn" name="goBtn">查询
                                    </button>
                                </td>
                            </tr>
                        </table>
                    </form>
                </div>
                <!-- /.box-body -->
            </div>
            <!-- Default box -->
            <div class="box">
                <div class="box-header with-border">
                    <h3 class="box-title">学生管理</h3>
                    <div class="box-tools pull-right">
                        <a id="newBtn" class="btn btn-success btn-sm"><i class="fa fa-plus"></i> 新增学生</a>
                    </div>
                </div>
                <div class="box-body no-padding">
                    <table class="table table-hover">
                        <tbody>
                        <tr>
                            <th width="10"></th>
                            <th>学生ID</th>
                            <th>姓名</th>
                            <th>学号</th>
                            <th>学院</th>
                            <th>专业</th>
                            <th>年级</th>
                            <th>性别</th>
                            <th>操作</th>
                        </tr>
                        <c:if test="${empty page.list}">
                            <tr>
                                <td colspan="6">没有学生</td>
                            </tr>
                        </c:if>
                        <c:forEach items="${page.list}" var="student">
                            <tr class="rowDetail" rel="${student.stuNumber}">
                                <td></td>
                                <td>${student.id}</td>
                                <td>${student.name}</td>
                                <td>${student.stuNumber}</td>
                                <td rel="${student.academy.id}">${student.academy.name}</td>
                                <td rel="${student.majorId}">${student.major.major}</td>
                                <td>${student.level}</td>
                                <td>${student.sex}</td>
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
    <!-- 增加学生信息模态框 -->
    <!------------------------------------------------------------------------------------------->
    <div class="modal fade" id="saveModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span
                            aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">增加学生</h4>
                </div>
                <div class="modal-body">
                    <div class="form-group form_datetime">
                        <label for="sname">姓名</label>
                        <input type="text" name="name" class="form-control"
                               id="sname">
                    </div>
                    <div class="form-group form_datetime">
                        <label for="sstuNumber">学号</label>
                        <input type="text" name="stuNumber" class="form-control"
                               id="sstuNumber">
                    </div>
                    <div class="form-group form_datetime">
                        <label for="sacademy">学院</label>
                        <select id="sacademy" name="sacademy" class="form-control academy" style="width: 100%"
                                onchange="getAcademy(this.value)">
                            <option value="">请选择</option>
                            <c:forEach items="${academyList}" var="academy">
                                <option value="${academy.id}">${academy.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group form_datetime">
                        <label for="smajor">专业</label>
                        <select id="smajor" name="smajor" class="form-control major" style="width: 100%">
                            <option value="">请选择</option>
                            <c:forEach items="${majorList}" var="major">
                                <option value="${major.id}">${major.major}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group form_datetime">
                        <label for="slevel">年级</label>
                        <select id="slevel" name="slevel" class="form-control level" style="width: 100%">
                            <option value="">请选择</option>
                            <option value="16">16级</option>
                            <option value="17">17级</option>
                            <option value="18">18级</option>
                            <option value="19">19级</option>
                            <option value="20">20级</option>
                            <option value="21">21级</option>
                        </select>
                    </div>
                    <div class="form-group form_datetime">
                        <label for="spass">密码</label>
                        <input type="password" name="spass" class="form-control"
                               id="spass">
                    </div>
                    <div class="form-group form_datetime">
                        <label for="ssex">性别</label>
                        <select id="ssex" name="ssex" class="form-control" style="width: 100%">
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
    <!-- 修改学生信息模态框 -->
    <!------------------------------------------------------------------------------------------->
    <div class="modal fade" id="modifyModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span
                            aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">修改学生信息</h4>
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
                        <label for="stuNumber">学号</label>
                        <input type="text" name="stuNumber" class="form-control"
                               id="stuNumber" readonly="readonly">
                    </div>
                    <div class="form-group form_datetime">
                        <label for="academy">学院</label>
                        <select id="academy" name="academy" class="form-control" style="width: 100%"
                                onchange="getAcademy2(this.value)">
                            <option value="">请选择</option>
                            <c:forEach items="${academyList}" var="academy">
                                <option value="${academy.id}">${academy.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group form_datetime">
                        <label for="major">专业</label>
                        <select id="major" class="form-control" style="width: 100%" aria-selected="">
                            <option value="">请选择</option>
                        </select>
                    </div>
                    <div class="form-group form_datetime">
                        <label for="level">年级</label>
                        <select id="level" name="level" class="form-control level" style="width: 100%">
                            <option value="">请选择</option>
                            <option value="16">16级</option>
                            <option value="17">17级</option>
                            <option value="18">18级</option>
                            <option value="19">19级</option>
                            <option value="20">20级</option>
                            <option value="21">21级</option>
                        </select>
                    </div>
                    <div class="form-group form_datetime">
                        <label for="sex">性别</label>
                        <select id="sex" class="form-control" style="width: 100%">
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

    $('#slevel').select2();
    $('#findAcademy').select2();
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

    //回填的二级类别值
    function getAcademy2(id) {
        let options = "";
        //回填的二级类别值
        $('#major').empty();
        options += "<option value=''>请选择</option>";
        <c:forEach items="${majorList}" var="major">
        var academyId = "${major.academyId}";
        if (academyId === id) {
            const value = "${major.id}";
            const name = "${major.major}";
            options += "<option selected='true' value=" + value + ">" + name + "</option>";
        }
        </c:forEach>
        $("#major").append(options).select2();
    }

    // 模态框
    // 启动修改模态框
    $(".editStudent").click(function () {
        $("#modifyModal").modal({
            show: true,
            backdrop: 'static'
        });

        // 模态框赋值
        const stuId = $(this).parents('tr').find('td').eq(1).text();
        const name = $(this).parents('tr').find('td').eq(2).text();
        const stuNumber = $(this).parents('tr').find('td').eq(3).text();
        const academyId = $(this).parents('tr').find('td').eq(4).attr("rel");
        const majorId = $(this).parents('tr').find('td').eq(5).attr("rel");
        const major = $(this).parents('tr').find('td').eq(5).text();
        const level = $(this).parents('tr').find('td').eq(6).text();
        const sex = $(this).parents('tr').find('td').eq(7).text();
        $('#id').val(stuId);
        $('#name').val(name);
        $('#stuNumber').val(stuNumber);
        $('.level').val(level).select2();
        $('#sex').val(sex).select2();
        $('#academy').val(academyId).select2();

        var options = "<option selected='true' value=" + majorId + ">" + major + "</option>";
        $('#major').append(options).select2();

        $("#saveBtn").click(function () {
            let a = $("#id").val();
            let b = $("#name").val();
            let c = $("#stuNumber").val();
            let d = $("#major").val();
            let e = $("#sex").val();
            let f = $('#level').val();
            layer.confirm("确定修改信息吗?", function () {
                $.post("/teacher/student/update/", {
                    "id": a,
                    "name": b,
                    "stuNumber": c,
                    "majorId": d,
                    "sex": e,
                    "level": f
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

    // select2
    $("#ssex").select2({width: "100%"});
    $("#sacademy").select2({width: "100%"});

    //回填的二级类别值
    function getAcademy(id) {
        let options = "";
        //回填的二级类别值
        $('.major').empty();
        options += "<option value=''>请选择</option>";
        <c:forEach items="${majorList}" var="major">
        var academyId = "${major.academyId}";
        if (academyId === id) {
            const value = "${major.id}";
            const name = "${major.major}";
            options += "<option selected='true' value=" + value + ">" + name + "</option>";
        }
        </c:forEach>
        $(".major").append(options).select2();
    }


    // 启动模态框
    $("#newBtn").click(function () {
        $("#saveModal").modal({
            show: true,
            backdrop: 'static'
        });

        $("#ssaveBtn").click(function () {
            let b = $("#sname").val();
            let c = $("#sstuNumber").val();
            let d = $("#smajor").val();
            let e = $("#ssex").val();
            let f = $("#spass").val();
            let g = $("#slevel").val();
            layer.confirm("确定增加学生吗?", function () {
                $.post("/teacher/student/save/", {
                    "name": b,
                    "stuNumber": c,
                    "majorId": d,
                    "sex": e,
                    "password": f,
                    "level": g
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


