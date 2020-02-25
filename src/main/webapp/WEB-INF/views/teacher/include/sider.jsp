<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- 左侧菜单栏 -->
<aside class="main-sidebar">
    <!-- sidebar: style can be found in sidebar.less -->
    <section class="sidebar">
        <!-- 搜索表单，不需要删除即可 -->
        <form action="#" method="get" class="sidebar-form">
            <div class="input-group">
                <input type="text" name="q" class="form-control" placeholder="Search...">
                <span class="input-group-btn">
                  <button type="submit" name="search" id="search-btn" class="btn btn-flat"><i class="fa fa-search"></i>
                  </button>
                </span>
            </div>
        </form>
        <!-- /.search form -->
        <!-- 菜单 -->
        <ul class="sidebar-menu">
            <li class="${param.menu == 'home' ? 'active' : ''}"><a
                    href="/teacher/home/${sessionScope.get("teacher").id}"><i class="fa fa-home"></i>
                <span>首页</span></a></li>
            <li class="header">系统功能</li>
            <!-- 消息公告 -->
            <li class="treeview ${param.menu == 'announce' ? 'active' : ''}">
                <a href="${pageContext.request.contextPath}/teacher/announce/system">
                    <i class="fa fa-commenting"></i> <span>消息公告</span>
                    <span class="pull-right-container"><i class="fa fa-angle-left pull-right"></i></span>
                </a>
            </li>
            <!-- 课程管理 -->
            <li class="treeview ${param.menu == 'course'?'active':''}">
                <a href="${pageContext.request.contextPath}/teacher/course/list">
                    <i class="fa fa-book"></i> <span>课程管理</span>
                    <span class="pull-right-container">
                    <i class="fa fa-angle-left pull-right"></i></span>
                </a>
            </li>
            <!-- 专业管理 -->
            <li class="treeview ${param.menu == 'major'?'active':''}">
                <a href="${pageContext.request.contextPath}/teacher/major">
                    <i class="fa fa-mortar-board"></i> <span>专业管理</span>
                    <span class="pull-right-container">
                    <i class="fa fa-angle-left pull-right"></i></span>
                </a>
            </li>
            <!-- 学生管理 -->
            <li class="treeview ${param.menu == 'student'?'active':''}">
                <a href="<c:url value="/teacher/student"/>">
                    <i class="fa fa-user-circle"></i><span>学生管理</span>
                    <span class="pull-right-container">
                    <i class="fa fa-angle-left pull-right"></i></span>
                </a>
            </li>
            <!-- 试题管理 -->
            <li class="treeview ${param.menu == 'question'?'active':''}">
                <a href="<c:url value="/teacher/question"/>">
                    <i class="fa fa-file-pdf-o"></i> <span>试题管理</span>
                    <span class="pull-right-container">
                        <i class="fa fa-angle-left pull-right"></i>
                    </span>
                </a>
            </li>
            <!-- 试卷管理 -->
            <li class="treeview ${param.menu == 'paper'?'active':''}">
                <a href="<c:url value="/teacher/paper"/>">
                    <i class="fa fa-book"></i> <span>试卷管理</span>
                    <span class="pull-right-container">
                    <i class="fa fa-angle-left pull-right"></i></span>
                </a>
            </li>
            <!-- 考试管理 -->
            <li class="treeview ${param.menu == 'exam'?'active':''}">
                <a href="<c:url value="/teacher/exam"/>">
                    <i class="fa fa-paper-plane"></i> <span>考试管理</span>
                    <span class="pull-right-container">
                    <i class="fa fa-angle-left pull-right"></i></span>
                </a>
            </li>
            <!-- 试卷复查 -->
            <li class="treeview ${param.menu == 'review'?'active':''}">
                <a href="<c:url value="/teacher/reviewPaper"/>">
                    <i class="fa fa-retweet"></i> <span>试卷复查</span>
                    <span class="pull-right-container">
                    <i class="fa fa-angle-left pull-right"></i></span>
                </a>
            </li>
            <!-- 帮助中心 -->
            <li class=" ${fn:startsWith(param.menu,'help')?'active':''}">
                <a href="${pageContext.request.contextPath}/teacher/help"><i class="fa fa-share-alt"></i>
                    <span>帮助中心</span></a>
            </li>
        </ul>
    </section>
    <!-- /.sidebar -->
</aside>
