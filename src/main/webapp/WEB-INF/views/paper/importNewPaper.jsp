<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>OES | 新增试卷</title>
    <!-- css style -->
    <%@ include file="../include/css.jsp" %>
    <link rel="stylesheet" href="<c:url value="/static/plugins/datetimepicker/css/bootstrap-datetimepicker.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/webjars/adminlte/2.3.11/plugins/datepicker/datepicker3.css"/>">
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
            <!-- Default box -->
            <div class="box">
                <div class="box-header with-border">
                    <h3 class="box-title">新增试卷</h3>
                    <div class="box-tools pull-right">
                        <a href="<c:url value="/teacher/paper"/>" class="btn btn-primary btn-sm"><i
                                class="fa fa-arrow-left"></i> 返回列表</a>
                    </div>
                </div>
                <div class="box-body">
                    <div class="form-group">
                        <label style="color: #af0000">注意：题目类型代码，[ 单选题-1，多选题-2，判断题-3，填空题-4，主观题-5，编程题-6 ]</label><br>
                        <label style="color: #af0000">注意：题目难度，[ 简单-1，中等-2，困难-3 ]</label>
                        <form method="post" id="importPaper">
                            <div class="form-group">
                                <label for="upload">导入试卷 </label>
                                <input class="form-control btn btn-flat btn-warning btn-sm" style="border: none"
                                       type="file" name="upload"
                                       id="upload"/>
                            </div>
                        </form>
                        <form method="post" id="addForm">
                            <div class="form-group">
                                <label for="teacherId">试卷名称</label>
                                <input type="hidden" class="form-control" id="teacherId" name="teacherId"
                                       value="${sessionScope.get('teacherId')}">
                                <input type="hidden" class="form-control" name="paperState" value="未开始">
                                <input type="text" class="form-control" id="paperName" name="paperName">
                                <input type="text" hidden="hidden" name="questionId" id="questionId">
                                <input type="text" hidden="hidden" name="paperFormId" id="paperFormId">
                            </div>
                            <div class="form-group">
                                <label for="teacherId">目前试卷分值</label>
                                <input type="text" class="form-control" id="curScore" disabled>
                            </div>
                            <div class="form-group">
                                <label for="courseId">所属课程</label>
                                <select class="form-control" id="courseId" name="courseId">
                                    <option value="">请选择</option>
                                    <c:forEach items="${courseList}" var="course">
                                        <option value="${course.id}">${course.courseName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="paperType">试卷类型 </label>
                                <select class="form-control" name="paperType" id="paperType" required="required">
                                    <option value="">请选择</option>
                                    <option value="正式">正式</option>
                                    <option value="模拟">模拟</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="academy">学院</label>
                                <select id="academy" onchange="getAcademy(this.value)" name="academy"
                                        class="form-control" required>
                                    <option value="">请选择</option>
                                    <c:forEach items="${academyList}" var="academy">
                                        <option value="${academy.id}">${academy.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="majorId">考试专业</label>
                                <select id="majorId" name="majorId" class="form-control" required="required">
                                    <option value="">请选择</option>
                                    <c:forEach items="${majorList}" var="major">
                                        <option value="${major.id}">${major.major}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="score">试卷总分 (默认模版为100分)</label>
                                <input type="text" id="score" name="score" value="100" class="form-control">
                            </div>
                            <hr/>
                            <div class="form-group">
                                <label style="color: #af0000">局部题型随机【请根据当前卷面分值自行判断】</label><br>
                                <label style="color: #af0000">按需选择，对于试卷已存在的题型进行随机抽题将驳回试卷提交请求！</label><br>
                            </div>
                            <div class="form-group row">
                                <div class="col-sm-3">
                                    <label for="a" class="control-label">单项选择题</label>
                                    <select id="a" name="a" class="form-control">
                                        <option value="1">是</option>
                                        <option value="0" selected>否</option>
                                    </select>
                                </div>
                                <div class="col-sm-3">
                                    <label for="aNum" class="control-label">数量</label>
                                    <input id="aNum" name="aNum" class="form-control"/>
                                </div>
                                <div class="col-sm-3">
                                    <label for="aScore" class="control-label">每题分值</label>
                                    <input id="aScore" name="aScore" class="form-control"/>
                                </div>
                                <div class="col-sm-3">
                                    <label for="aDif" class="control-label">难度</label>
                                    <select id="aDif" name="aDif" class="form-control">
                                        <option value="0" selected>平均</option>
                                        <option value="1">简答</option>
                                        <option value="2">一般</option>
                                        <option value="3">困难</option>
                                    </select>
                                </div>
                            </div>
                            <div class="form-group row">
                                <div class="col-sm-3">
                                    <label for="b" class="control-label">多项选择题</label>
                                    <select id="b" name="b" class="form-control">
                                        <option value="1">是</option>
                                        <option value="0" selected>否</option>
                                    </select>
                                </div>
                                <div class="col-sm-3">
                                    <label for="bNum" class="control-label">数量</label>
                                    <input id="bNum" name="bNum" class="form-control"/>
                                </div>
                                <div class="col-sm-3">
                                    <label for="bScore" class="control-label">每题分值</label>
                                    <input id="bScore" name="bScore" class="form-control"/>
                                </div>
                                <div class="col-sm-3">
                                    <label for="bDif" class="control-label">难度</label>
                                    <select id="bDif" name="bDif" class="form-control">
                                        <option value="0" selected>平均</option>
                                        <option value="1">简答</option>
                                        <option value="2">一般</option>
                                        <option value="3">困难</option>
                                    </select>
                                </div>
                            </div>
                            <div class="form-group row">
                                <div class="col-sm-3">
                                    <label for="c" class="control-label">判断题</label>
                                    <select id="c" name="c" class="form-control">
                                        <option value="1">是</option>
                                        <option value="0" selected>否</option>
                                    </select>
                                </div>
                                <div class="col-sm-3">
                                    <label for="cNum" class="control-label">数量</label>
                                    <input id="cNum" name="cNum" class="form-control"/>
                                </div>
                                <div class="col-sm-3">
                                    <label for="cScore" class="control-label">每题分值</label>
                                    <input id="cScore" name="cScore" class="form-control"/>
                                </div>
                                <div class="col-sm-3">
                                    <label for="cDif" class="control-label">难度</label>
                                    <select id="cDif" name="cDif" class="form-control">
                                        <option value="0" selected>平均</option>
                                        <option value="1">简答</option>
                                        <option value="2">一般</option>
                                        <option value="3">困难</option>
                                    </select>
                                </div>
                            </div>
                            <div class="form-group row">
                                <div class="col-sm-3">
                                    <label for="d" class="control-label">填空题</label>
                                    <select id="d" name="d" class="form-control">
                                        <option value="1">是</option>
                                        <option value="0" selected>否</option>
                                    </select>
                                </div>
                                <div class="col-sm-3">
                                    <label for="dNum" class="control-label">数量</label>
                                    <input id="dNum" name="dNum" class="form-control"/>
                                </div>
                                <div class="col-sm-3">
                                    <label for="dScore" class="control-label">每题分值</label>
                                    <input id="dScore" name="dScore" class="form-control"/>
                                </div>
                                <div class="col-sm-3">
                                    <label for="dDif" class="control-label">难度</label>
                                    <select id="dDif" name="dDif" class="form-control">
                                        <option value="0" selected>平均</option>
                                        <option value="1">简答</option>
                                        <option value="2">一般</option>
                                        <option value="3">困难</option>
                                    </select>
                                </div>
                            </div>
                            <div class="form-group row">
                                <div class="col-sm-3">
                                    <label for="e" class="control-label">主观题</label>
                                    <select id="e" name="e" class="form-control">
                                        <option value="1">是</option>
                                        <option value="0" selected>否</option>
                                    </select>
                                </div>
                                <div class="col-sm-3">
                                    <label for="eNum" class="control-label">数量</label>
                                    <input id="eNum" name="eNum" class="form-control"/>
                                </div>
                                <div class="col-sm-3">
                                    <label for="eScore" class="control-label">每题分值</label>
                                    <input id="eScore" name="eScore" class="form-control"/>
                                </div>
                                <div class="col-sm-3">
                                    <label for="eDif" class="control-label">难度</label>
                                    <select id="eDif" name="eDif" class="form-control">
                                        <option value="0" selected>平均</option>
                                        <option value="1">简答</option>
                                        <option value="2">一般</option>
                                        <option value="3">困难</option>
                                    </select>
                                </div>
                            </div>
                            <div class="form-group row">
                                <div class="col-sm-3">
                                    <label for="f" class="control-label">编程题</label>
                                    <select id="f" name="f" class="form-control">
                                        <option value="1">是</option>
                                        <option value="0" selected>否</option>
                                    </select>
                                </div>
                                <div class="col-sm-3">
                                    <label for="fNum" class="control-label">数量</label>
                                    <input id="fNum" name="fNum" class="form-control"/>
                                </div>
                                <div class="col-sm-3">
                                    <label for="fScore" class="control-label">每题分值</label>
                                    <input id="fScore" name="fScore" class="form-control"/>
                                </div>
                                <div class="col-sm-3">
                                    <label for="fDif" class="control-label">难度</label>
                                    <select id="fDif" name="fDif" class="form-control">
                                        <option value="0" selected>平均</option>
                                        <option value="1">简答</option>
                                        <option value="2">一般</option>
                                        <option value="3">困难</option>
                                    </select>
                                </div>
                            </div>

                            <hr/>
                            <div class="form-group">
                                <label style="color: #af0000">注意: 正式试卷必选，模拟试卷无需选择时间</label>
                            </div>
                            <div class="form-group form_datetime">
                                <label for="datepicker">开始时间</label>
                                <input type="text" name="beginTime" class="form-control" id="datepicker">
                            </div>
                            <div class="form-group form_datetime">
                                <label for="datepicker2">结束时间</label>
                                <input type="text" name="endTime" class="form-control" id="datepicker2">
                            </div>
                            <hr/>
                        </form>
                    </div>
                    <div class="box-footer">
                        <button class="btn btn-primary" id="addBtn">保存</button>
                        <a href="javascript:history.go(0)" class="btn btn-danger" id="reset">重置</a>
                    </div>
                    <!-- /.box-body -->
                </div>
                <!-- /.box -->
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
<script src="<c:url value="/static/plugins/datetimepicker/js/bootstrap-datetimepicker.min.js"/>"></script>
<script src="<c:url value="/static/plugins/datetimepicker/js/locales/bootstrap-datetimepicker.zh-CN.js"/>"></script>
<script src="<c:url value="/static/plugins/moment/moment.js"/>"></script>
<script src="<c:url value="/webjars/adminlte/2.3.11/plugins/datepicker/bootstrap-datepicker.js"/>"></script>
<script src="<c:url value="/webjars/adminlte/2.3.11/plugins/datepicker/locales/bootstrap-datepicker.zh-CN.js"/>"></script>
<script src="<c:url value="/static/plugins/jquery-validation/jquery.validate.js"/>"></script>
<script src="<c:url value="/static/plugins/jquery-form/jquery.form.min.js"/>"></script>
<script src="<c:url value="/static/plugins/layer/layer.js"/>"></script>
<script>

    // select2
    $('#academy').select2({width: "100%"});


    // 自动上传
    $('#upload').change(function () {
        var formData = new FormData();
        var files = $($(this))[0].files[0];
        formData.append("file", files);
        layer.msg('正在提交中....');
        $.ajax({
            url: '/teacher/paper/import/excel',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            dataType: 'json',
            success: function (res) {
                if (res.state === 'success') {
                    $('#paperName').val(res.data.paperName);
                    $('#questionId').val(res.data.questionIdList);
                    $('#paperFormId').val(res.data.paperFormId);
                    $('#curScore').val(res.data.score);
                    layer.msg('上传成功');
                } else {
                    layer.msg(res.message);
                }
            }
            , error: function (res) {
                layer.msg(res.message);
            }
        });
    });

    $('#majorId').select2().empty();

    //回填的二级类别值
    function getAcademy(id) {
        let options = "";
        //回填的二级类别值
        options += "<option value=''>请选择</option>";
        <c:forEach items="${majorList}" var="major">
        var academyId = "${major.academyId}";
        if (academyId === id) {
            const value = "${major.id}";
            const name = "${major.major}";
            options += "<option selected='true' value=" + value + ">" + name + "</option>";
        }
        </c:forEach>
        $("#majorId").append(options).select2();
    };

    $(function () {
        // select2
        $('#courseId').select2({width: "100%"});
        $('#paperType').select2({width: "100%"});

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

        let timepicker = $('#datepicker2').datetimepicker({
            format: "yyyy-mm-dd hh:ii",
            language: "zh-CN",
            autoclose: true,
            todayHighlight: true
        });

        // 提交组卷
        $('#addBtn').click(function () {
            if ($('#paperName').val() == '') {
                layer.alert("请填写试卷名称");
                return false;
            } else if ($('#courseId').val() == '') {
                layer.alert("请选择课程");
                return false;
            } else if ($('#academy').val() == '') {
                layer.alert("请选择专业");
                return false;
            } else {
                $.ajax({
                    type: "POST",
                    data: $('#addForm').serialize(),
                    success: function (res) {
                        if (res.state === "success") {
                            layer.alert("添加成功");
                            window.location.href = "/teacher/paper/show/" + res.data;
                        } else {
                            layer.alert(res.message);
                        }
                    }
                });
            }
        });

        $("#addForm").validate({
            errorClass: 'text-danger',
            errorElement: 'span',
            rules: {
                paperType: {
                    required: true
                },
                academy: {
                    required: true
                },
                majorId: {
                    required: true
                },
                beginTime: {
                    required: true
                },
                endTime: {
                    required: true
                }
            },
            messages: {
                paperType: {
                    required: "请输入试卷类型"
                },
                beginTime: {
                    required: "请选择开始时间"
                },
                endTime: {
                    required: "请选择结束时间"
                },
                academy: {
                    required: "请选择学院"
                },
                majorId: {
                    required: "请选择专业"
                }
            }
        });
    });

    // 随机部分
    $("#a").select2().val(0);
    $("#b").select2().val(0);
    $("#c").select2().val(0);
    $("#d").select2().val(0);
    $("#e").select2().val(0);
    $("#f").select2().val(0);
    $("#aDif").select2().val("0");
    $("#bDif").select2().val("0");
    $("#cDif").select2().val("0");
    $("#dDif").select2().val("0");
    $("#eDif").select2().val("0");
    $("#fDif").select2().val("0");

    // 监控变化

</script>
</body>
</html>

