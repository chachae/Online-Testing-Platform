<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<aside class="main-sidebar">
    <!-- sidebar: style can be found in sidebar.less -->
    <section class="sidebar">
        <!-- Sidebar user panel -->
        <div class="user-panel">
            <div class="pull-left image">
                <img src="<c:url value="/webjars/adminlte/2.3.11/dist/img/avatar5.png"/>" class="img-circle" alt="User Image">
            </div>
            <div class="pull-left info">
                <p>${sessionScope.get("admin").name}</p>
                <a href="#"><i class="fa fa-circle text-success"></i> Online</a>
                <a href="/admin/home/${sessionScope.get("admin").id}" class="pull-right"><i class="fa fa-home"></i>
                    <span>首页</span></a>
            </div>
        </div>
        <ul class="sidebar-menu" data-widget="tree">
            <li class="header">系统功能</li>
            <li class="treeview ${param.menu == 'announce'?'active':''}">
                <a href="<c:url value="/admin/announce"/>">
                    <i class="fa fa-dashboard"></i> <span>公告管理</span>
                    <span class="pull-right-container">
                      <i class="fa fa-angle-left pull-right"></i>
                    </span>
                </a>
            </li>
            <li class="treeview ${param.menu == 'academy'?'active':''}">
                <a href="<c:url value="/admin/academy"/>">
                    <i class="fa fa-university"></i> <span>学院管理</span>
                    <span class="pull-right-container">
                      <i class="fa fa-angle-left pull-right"></i>
                    </span>
                </a>
            </li>
            <li class="treeview ${param.menu == 'teacher'?'active':''}">
                <a href="<c:url value="/admin/teacher"/>">
                    <i class="fa fa-dashboard"></i> <span>教师管理</span>
                    <span class="pull-right-container">
                      <i class="fa fa-angle-left pull-right"></i>
                    </span>
                </a>
            </li>
            <li class="treeview ${param.menu == 'admin'?'active':''}">
                <a href="<c:url value="/admin"/>">
                    <i class="fa fa-dashboard"></i> <span>管理员管理</span>
                    <span class="pull-right-container">
                      <i class="fa fa-angle-left pull-right"></i>
                    </span>
                </a>
            </li>
        </ul>
    </section>
    <!-- /.sidebar -->
</aside>