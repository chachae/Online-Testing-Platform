<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>OES | 教师主页</title>
    <!-- Tell the browser to be responsive to screen width -->
    <%@include file="../include/css.jsp" %>
    <style>
        p {
            text-indent: 30px;
        }
    </style>


</head>
<body class="hold-transition skin-blue sidebar-mini">
<!-- Site wrapper -->
<div class="wrapper">

    <%@include file="include/header.jsp" %>
    <!-- 左侧菜单栏 -->
    <jsp:include page="include/sider.jsp">
        <jsp:param name="menu" value="home"/>
    </jsp:include>
    <!-- 右侧内容部分 -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1 style="font-family: 'Microsoft Yahei', Arial, Helvetica, sans-serif;text-align:center; font-size: 30px">
                欢迎使用 On-line Examination System 在线考试系统！
            </h1>
            <ol class="breadcrumb">
                <li><a href="#"><i class="fa fa-dashboard"></i> Home</a></li>
            </ol>
        </section>

        <!-- Main content -->
        <section class="content">
            <!-- Default box -->
            <div class="box">
                <div class="box-header with-border">
                    <h3 class="box-title lead" style="font-size: 22px;font-weight: 800">系统说明</h3>
                    <div class="box-tools pull-right">
                        <button type="button" class="btn btn-box-tool" data-widget="collapse" data-toggle="tooltip"
                                title="Collapse">
                            <i class="fa fa-minus"></i></button>
                        <button type="button" class="btn btn-box-tool" data-widget="remove" data-toggle="tooltip"
                                title="Remove">
                            <i class="fa fa-times"></i></button>
                    </div>
                </div>
                <div class="box-body" style="font-size: 20px">
                    <p>On-line Examination System 在线考试系统是一款构建在 SpringBoot 和 MyBatis 上基于 JSP 开发的线考试系统。</p>
                    <p>教师端包含如下功能：</p>
                    <p>一、查询系统公告</p>
                    <p>二、课程统一管理</p>
                    <p>三、全校专业统一管理</p>
                    <p>四、学生信息统一管理</p>
                    <p>五、发布、Excel 导入、修改、删除试题</p>
                    <p>六、发布、Excel 导入、修改、删除考试</p>
                    <p>七、学生成绩复查、修改分数</p>
                    <p>八、未发布考试时间修改、删除</p>
                </div>
                <!-- /.box-body -->
                <div class="box-footer ">
                    <blockquote class="blockquote-reverse">
                        <p style="color: #2D93CA">祝您开启愉快的一天！</p>
                        <a class="close" aria-hidden="true">
                            ${message}
                        </a>
                    </blockquote>
                </div>
                <!-- /.box-footer-->
            </div>
            <!-- /.box -->
        </section>
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->
    <%@include file="../include/footer.jsp" %>
</div>
<!-- ./wrapper -->
<%@include file="../include/js.jsp" %>
</body>
</html>

