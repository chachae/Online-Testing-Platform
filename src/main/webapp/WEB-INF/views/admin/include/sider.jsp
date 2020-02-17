<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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