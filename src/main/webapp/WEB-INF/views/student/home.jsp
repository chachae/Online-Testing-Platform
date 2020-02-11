<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>OES | 学生首页</title>
    <!-- 引入全局 css 文件 -->
    <%@include file="../include/css.jsp" %>
    <!-- 样式 -->
    <style>
        p {
            text-indent: 30px;
        }
    </style>
</head>
<body class="hold-transition skin-green sidebar-mini">
<div class="wrapper">
    <!-- 引入头部 -->
    <%@include file="include/header.jsp" %>
    <!-- 左侧菜单栏 -->
    <jsp:include page="include/sider.jsp">
        <jsp:param name="menu" value="home"/>
    </jsp:include>
    <!-- 右侧内容部分 -->
    <div class="content-wrapper">
        <section class="content-header">
            <h1 style="font-family: 'Microsoft Yahei', Arial, Helvetica, sans-serif;text-align:center; font-size: 30px">
                欢迎使用 On-line Examination System 在线考试系统
            </h1>
            <ol class="breadcrumb">
                <li><a href="#"><i class="fa fa-dashboard"></i> Home</a></li>
            </ol>
        </section>
        <!-- table 部分 -->
        <section class="content">
            <div class="box">
                <div class="box-header with-border">
                    <h3 class="box-title lead" style="font-size: 22px;font-weight: 800">诚信考试倡议书</h3>
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
                    　　<p>为了加强全校考风考纪建设，我们作出如下倡议：</p>
                    　　<p>一、全面复习，认真备考，投身“提倡文明修身，严肃考风考纪”的行动中;</p>
                    　　<p>二、各班加大考风考纪教育力度，严肃考风考纪，严格执行学校的有关文件规定，明确作弊危害和后果，树立良好的考试心态;</p>
                    　　<p>三、党员、学生干部为诚信考试树立模范带头作用，端正考风、严肃考纪;</p>
                    　　<p>四、提倡对作弊的同学如实上报，绝不包庇。</p>
                    　　<p>五、各班级、各支部携手共进，为严肃考风考纪，树立良好的学风，杜绝作弊现象进到一份义务。</p>
                    　　<p>最后，衷心预祝全体同学在期末考试中取得优异的成绩!</p>
                </div>
                <div class="box-footer ">
                    <blockquote class="blockquote-reverse">
                        <p style="color: green">祝您开启愉快的一天！</p>
                        <a class="close" aria-hidden="true">
                            ${message}
                        </a>
                    </blockquote>
                </div>
            </div>
        </section>
    </div>
    <!-- 引入footer -->
    <%@include file="../include/footer.jsp" %>
</div>
<!-- 引入全局 js 脚本文件 -->
<%@include file="../include/js.jsp" %>
</body>
</html>

