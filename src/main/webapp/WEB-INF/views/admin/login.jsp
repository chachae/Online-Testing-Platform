<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>OES在线考试系统|管理员登录</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/static/css/login.css"/>"/>
    <link href="<c:url value="/static/css/login.css"/>" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<c:url value="/webjars/adminlte/2.3.11/plugins/jQuery/jquery-2.2.3.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/static/plugins/jsencrypt/jsencrypt.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/static/js/login.js"/>" charset="utf-8"></script>
    <script type="text/javascript" src="<c:url value="/static/js/check.js"/>" charset="UTF-8"></script>
</head>

<body>
<div class="b3"></div>
<div class="admin_login_wrap">
    <h1>OES在线考试系统</h1>
    <h1 style="margin-top:-30px;margin-bottom: 20px">后台管理</h1>
    <div class="login">
        <div class="header" style="text-align: center;">
            <label style="font-size:20px;line-height:51px">管理员登录</label>
        </div>
        <div class="web_login" style="margin-top:20px">
            <form method="post" onsubmit="return login_admin()">
                <ul class="reg_form">
                    <li>
                        <label for="adminId" class="input-tips2">用户名：</label>
                        <input type="text" name="adminId" value="456" id="adminId" size="35" class="inputstyle2"/>
                    </li>
                    <li>
                        <label for="adminPassword" class="input-tips2">密码：</label>
                        <input type="password" name="adminPassword" id="adminPassword" size="35"
                               class="inputstyle2 realPass" hidden="hidden"/>
                        <input type="password" id="encPass" value="123" size="35"
                               class="inputstyle2 encPass"/>
                    </li>
                    <li>
                        <div class="inputArea">
                            <input type="submit" id="reg" style="margin-top:10px;margin-left:90px;" class="button_blue"
                                   value="立即登录"/>
                        </div>
                    </li>
                </ul>
            </form>
        </div>
    </div>
</div>
<jsp:include page="../include/login_footer.jsp"/>
</body>
</html>