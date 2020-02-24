<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>OES | 考试列表</title>
    <!-- Tell the browser to be responsive to screen width -->
    <%@include file="../include/css.jsp" %>
    <link rel="stylesheet"
          href="/static/plugins/datetimepicker/css/bootstrap-datetimepicker.min.css">
    <link rel="stylesheet" href="/webjars/adminlte/2.3.11/plugins/datepicker/datepicker3.css">

    <!-- 样式 -->
    <style>
        .table > tbody > tr:hover {
            cursor: pointer;
        }

        .table > tbody > tr > td {
            vertical-align: middle;
        }

        th {
            font-size: 15px;
            align: "center"
        }

        td {
            font-size: 16px;
        }
    </style>


</head>
<body class="hold-transition skin-blue sidebar-mini">
<!-- Site wrapper -->
<div class="wrapper">
    <%@include file="../teacher/include/header.jsp" %>
    <!-- 左侧菜单栏 -->
    <jsp:include page="../teacher/include/sider.jsp">
        <jsp:param name="menu" value="exam"/>
    </jsp:include>
    <!-- 右侧内容部分 -->
    <div class="content-wrapper">
        <!-- Main content -->
        <section class="content">

            <!-- Default box -->
            <div class="box">
                <div class="box-header with-border">
                    <h3 class="box-title">正式考试</h3>
                    <div class="box-tools pull-right">
                    </div>
                </div>
                <div class="box-body no-padding">
                    <table class="table table-hover">
                        <tbody>
                        <c:if test="${empty paperList}">
                            <tr>
                                <td colspan="6">暂无需要管理的考试</td>
                            </tr>
                        </c:if>
                        <c:if test="${not empty paperList}">
                            <tr>
                                <th style="width: 10px"></th>
                                <th>试卷名称</th>
                                <th>开考时间</th>
                                <th>结束时间</th>
                                <th>考试时长</th>
                                <th>操作</th>
                            </tr>
                        </c:if>
                        <c:forEach items="${paperList}" var="paper" varStatus="state">
                            <tr class="rowDetail" rel="${paper.id}">
                                <td>${state.count}</td>
                                <td>${paper.paperName}</td>
                                <td>${paper.beginTime}</td>
                                <td>${paper.endTime}</td>
                                <td>${paper.allowTime}</td>
                                <td><a class="rowDel" rel="${paper.id}">修改时间</a></td>
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
    <!-- 修改时间模态框 -->
    <div class="modal fade" id="modifyModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">修改时间</h4>
                </div>
                <div class="modal-body">
                    <div class="form-group form_datetime">
                        <label>开始时间</label>
                        <input type="text" name="beginTime" class="form-control"
                               id="datepicker">
                    </div>
                    <div class="form-group form_datetime">
                        <label>结束时间</label>
                        <input type="text" name="endTime" class="form-control" id="datepicker2">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                    <button type="button" class="btn btn-primary" id="saveBtn">确定</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
    </div><!-- /.modal -->

</div>
<!-- ./wrapper -->

<%@include file="../include/js.jsp" %>
<script src="<c:url value="/static/plugins/layer/layer.js"/>"></script>
<script src="<c:url value="/static/plugins/jquery-countdown/jquery.countdown.min.js"/>"></script>
<script src="<c:url value="/static/plugins/datetimepicker/js/bootstrap-datetimepicker.min.js"/>"></script>
<script src="<c:url value="/static/plugins/datetimepicker/js/locales/bootstrap-datetimepicker.zh-CN.js"/>"></script>
<script src="<c:url value="/static/plugins/moment/moment.js"/>"></script>
<script src="<c:url value="/webjars/adminlte/2.3.11/plugins/datepicker/bootstrap-datepicker.js"/>"></script>
<script src="<c:url value="/webjars/adminlte/2.3.11/plugins/datepicker/locales/bootstrap-datepicker.zh-CN.js"/>"></script>
<script src="<c:url value="/static/plugins/jquery-validation/jquery.validate.js"/>"></script>
<script>
    $(function () {

        const date = new Date();
        const picker = $("#datepicker").datetimepicker({
            format: "yyyy-mm-dd hh:ii",
            language: "zh-CN",
            autoclose: true,
            todayHighlight: true,
            startDate: date
        });
        picker.on("changeDate", function (e) {
            const time = $("#datepicker").val();
            const day = time.split(" ")[0];
            $('#datepicker2').datetimepicker('setStartDate', day);
        });

        const timepicker = $('#datepicker2').datetimepicker({
            format: "yyyy-mm-dd hh:ii",
            language: "zh-CN",
            autoclose: true,
            todayHighlight: true
        });


        //修改考试时间
        $(".rowDel").click(function () {
            const beginTime = $(this).parents('tr').find('td').eq(2).text();
            const endTime = $(this).parents('tr').find('td').eq(3).text();
            // 模态框赋值
            $('#datepicker').val(beginTime);
            $('#datepicker2').val(endTime);
            $("#modifyModal").modal({
                show: true,
                backdrop: 'static'
            });

            const id = $(this).attr("rel");


            $("#saveBtn").click(function () {
                layer.confirm("确定修改考试时间么?", function () {
                    let begin = $("#datepicker").val();
                    let end = $("#datepicker2").val();
                    $.post("/teacher/editPaper/" + id, {
                        "beginTime": begin,
                        "endTime": end,
                    }).done(function (data) {
                        if (data.state === "success") {
                            layer.msg("修改成功!");
                            window.location.href = "/teacher/exam";
                        }else{
                            layer.msg(data.message);
                        }
                    }).error(function () {
                        layer.msg("服务器异常");
                    });
                });
            });
        });
    });
</script>
</body>
</html>


