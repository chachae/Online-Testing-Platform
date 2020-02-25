<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- 左侧菜单栏 -->
<aside class="main-sidebar">
    <!-- sidebar: style can be found in sidebar.less -->
    <section class="sidebar">

        <!-- 搜索表单，不需要删除即可 -->
        <form action="<c:url value="/search"/>" id="search" method="get" class="sidebar-form">
            <div class="input-group">
                <input type="text" name="questionId" class="form-control" placeholder="Search...">
                <span class="input-group-btn">
                  <button type="submit" name="search" id="search-btn" class="btn btn-flat"><i class="fa fa-search"></i>
                  </button>
                </span>
            </div>
        </form>
        <!-- 菜单 -->
        <ul class="sidebar-menu">
            <li class="${param.menu == 'home' ? 'active' : ''}"><a
                    href="/student/home/${sessionScope.get("studentId")}"><i class="fa fa-home"></i> <span>首页</span></a>
            </li>
            <li class="header">系统功能</li>
            <!-- 消息公告 -->
            <li class="treeview ${fn:startsWith(param.menu,'announce')?'active':''}">
                <a href="<c:url value="/student/announce/system"/>">
                    <i class="fa fa-address-book-o"></i> <span>消息公告</span>
                    <span class="pull-right-container"><i class="fa fa-angle-left pull-right"></i></span>
                </a>
            </li>
            <!-- 我的考试 -->
            <li class="treeview ${param.menu == 'exam'?'active':''}">
                <a href="<c:url value="/student/exam"/>">
                    <i class="fa fa-file-text"></i> <span>我的考试</span>
                    <span class="pull-right-container">
                        <i class="fa fa-angle-left pull-right"></i>
                    </span>
                </a>
            </li>
            <!-- 我的成绩 -->
            <li class="treeview ${fn:startsWith(param.menu,'score')?'active':''}">
                <a href="/student/score/${sessionScope.get("studentId")}">
                    <i class="fa fa-print"></i> <span>我的成绩</span>
                    <span class="pull-right-container">
                    <i class="fa fa-angle-left pull-right"></i></span>
                </a>
            </li>
            <!-- 成绩分析 -->
            <li class="treeview ${fn:startsWith(param.menu,'chart_')?'active':''}">
                <a href="">
                    <i class="fa fa-pie-chart"></i> <span>成绩分析</span>
                    <span class="pull-right-container">
                    <i class="fa fa-angle-left pull-right"></i></span>
                </a>
                <ul class="treeview-menu">
                    <li class="${param.menu == 'chart_my'?'active':''}"><a
                            href="${pageContext.request.contextPath}/score/student/chart"><i
                            class="fa fa-circle-o"></i> 我的分析</a></li>
                </ul>
            </li>
            <li class=" ${fn:startsWith(param.menu,'help')?'active':''}">
                <a href="<c:url value="/student/help"/>"><i class="fa fa-share-alt"></i> <span>帮助中心</span></a>
            </li>

        </ul>
    </section>
    <!-- /.sidebar -->
</aside>

