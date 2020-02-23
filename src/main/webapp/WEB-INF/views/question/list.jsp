<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>OES | 题目列表</title>
    <!-- Tell the browser to be responsive to screen width -->
    <%@include file="../include/css.jsp" %>
    <style>
        .table > tbody > tr:hover {
            cursor: pointer;
        }

        .table > tbody > tr > td {
            vertical-align: middle;
        }

        .star {
            font-size: 20px;
            color: #ff7400;
        }

        th {
            font-size: 14px;
            text-align: center;
        }

        td {
            font-size: 14px;
            text-align: center;
        }

    </style>
</head>
<body class="hold-transition skin-blue sidebar-mini">


<!-- 增加学生信息模态框 -->
<!------------------------------------------------------------------------------------------->
<div class="modal fade" id="importModal">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span
                        aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title">导入题目</h4>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <form method="post" id="importPaper">
                        <div class="form-group">
                            <label for="upload">导入试卷 </label>
                            <input class="form-control btn-file" type="file" name="upload"
                                   id="upload"/>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
    </div><!-- /.modal -->
</div>


<!-- Site wrapper -->
<div class="wrapper">

    <%@include file="../teacher/include/header.jsp" %>
    <!-- 左侧菜单栏 -->
    <jsp:include page="../teacher/include/sider.jsp">
        <jsp:param name="menu" value="question"/>
    </jsp:include>
    <!-- 右侧内容部分 -->
    <div class="content-wrapper">
        <!-- Main content -->
        <section class="content">
            <!-- Default box -->
            <div class="box">
                <div class="box-header with-border">
                    <h3 class="box-title">试题管理</h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-warning btn-sm" id="importBtn">
                            <i class="fa fa-plus-circle"></i>
                            导入试题
                        </button>
                        <a href="<c:url value="/teacher/question/new"/>" class="btn btn-success btn-sm"><i
                                class="fa fa-plus-circle"></i>
                            新增试题</a>
                    </div>
                </div>
                <div class="box-tools">
                    <div class="box-tools pull-right">
                        <label for="typeId">类型：</label>
                        <select class="form-control" name="typeId" id="typeId">
                            <option value="" selected>全部</option>
                            <c:forEach items="${typeList}" var="type">
                                <c:if test="${curTypeId == null}">
                                    <option value="${type.id}">${type.typeName}</option>
                                </c:if>
                                <c:if test="${curTypeId != null}">
                                    <c:choose>
                                        <c:when test="${curTypeId == type.id}">
                                            <option value="${type.id}" selected>${type.typeName}</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="${type.id}">${type.typeName}</option>
                                        </c:otherwise>
                                    </c:choose>
                                </c:if>
                            </c:forEach>
                        </select>
                        <label for="courseId">科目：</label>
                        <select class="form-control" name="courseId" id="courseId">
                            <option value="" selected>全部</option>
                            <c:forEach items="${courseList}" var="course">
                                <c:if test="${curCourseId == null}">
                                    <option value="${course.id}">${course.courseName}</option>
                                </c:if>
                                <c:if test="${curCourseId != null}">
                                    <c:choose>
                                        <c:when test="${curCourseId == course.id}">
                                            <option value="${course.id}" selected>${course.courseName}</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="${course.id}">${course.courseName}</option>
                                        </c:otherwise>
                                    </c:choose>
                                </c:if>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="box-body no-padding">
                    <table class="table table-hover">
                        <tbody>
                        <tr>
                            <th>题目编号</th>
                            <th style="text-align: left">题目名称</th>
                            <th>题目类型</th>
                            <th>难易程度</th>
                        </tr>
                        <c:if test="${empty page.list}">
                            <tr>
                                <td colspan="6">题库中还未添加题目</td>
                            </tr>
                        </c:if>
                        <c:forEach items="${page.list}" var="question">
                            <tr class="rowDetail" rel="${question.id}">
                                <td>${question.id}</td>
                                <td style="text-align: left">${question.questionName}</td>
                                <td>${question.typeId == '1' ? '单选题' : ''}
                                        ${question.typeId == '2' ? '多选题' : ''}
                                        ${question.typeId == '3' ? '判断题' : ''}
                                        ${question.typeId == '4' ? '填空题' : ''}
                                        ${question.typeId == '5' ? '简答题' : ''}
                                        ${question.typeId == '6' ? '编程题' : ''}
                                </td>
                                <td class="star">
                                        ${question.difficulty == '1'?'★':''}
                                        ${question.difficulty == '2'?'★ ★':''}
                                        ${question.difficulty == '3'?'★ ★ ★':''}
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
                <!-- /.box-body -->
            </div>
            <!-- /.box -->
            <ul id="pagination-demo" class="pagination pull-right"></ul>
        </section>
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->
    <%@include file="../include/footer.jsp" %>
</div>
<!-- ./wrapper -->
<%@include file="../include/js.jsp" %>
<script src="<c:url value="/static/plugins/jquery-pagination/jquery.twbsPagination.min.js"/>"></script>
<script src="<c:url value="/static/plugins/layer/layer.js"/>"></script>
<script>

    // 模态框
    // 启动修改模态框
    $("#importBtn").click(function () {
        $("#importModal").modal({
            show: true,
            backdrop: 'static'
        });
    });

    // 自动上传
    $('body').on('change', '#upload', function () {
        var formData = new FormData();
        var files = $($(this))[0].files[0];
        formData.append("file", files);
        layer.msg('正在提交中....');
        $.ajax({
            url: '/teacher/question/import',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function (res) {
                if (res.state === "success") {
                    layer.msg('题目解析成功，完成导入');
                    window.location.reload();
                } else {
                    layer.msg(res.message);
                    window.location.reload();
                }
            }
            , error: function (res) {
                layer.msg(res.message);
            }
        });
    });

    $(function () {
        $(".rowDetail").click(function () {
            let id = $(this).attr("rel");
            window.location.href = "/teacher/question/show/" + id;
        });

        var typeId = "";
        var courseId = "";


        $("#typeId").select2({width: "200px"}).change(function () {
            typeId = $(this).val();
            console.log(typeId);
            window.location.href = "/teacher/question?p=1&courseId=" + "${curCourseId}" + "&typeId=" + typeId;
        })

        // 课程筛选
        $("#courseId").select2({width: "200px"}).change(function () {
            courseId = $(this).val();
            console.log(courseId);
            window.location.href = "/teacher/question?p=1&courseId=" + courseId + "&typeId=" + "${curTypeId}";
        });

        //分页
        $('#pagination-demo').twbsPagination({
            totalPages: "${page.pages}",
            visiblePages: 5,
            first: '首页',
            last: '末页',
            prev: '上一页',
            next: '下一页',
            href: "?p={{number}}&courseId=" + "${curCourseId}" + "&typeId=" + "${curTypeId}",
        });
    });
</script>
</body>
</html>


