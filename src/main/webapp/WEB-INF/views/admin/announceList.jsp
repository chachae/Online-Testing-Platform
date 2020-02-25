<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>OES | 公告列表</title>
    <%@include file="../include/css.jsp" %>
    <style>
        .table > tbody > tr:hover {
            cursor: pointer;
        }

        .table > tbody > tr > td {
            vertical-align: middle;
        }

        th {
            font-size: 14px;
            text-align: center;
        }

        td {
            font-size: 15px;
            text-align: center;
        }
    </style>
</head>
<body class="hold-transition skin-purple">
<!-- Site wrapper -->
<div class="wrapper">
    <%@include file="../admin/include/header.jsp" %>
    <!-- 左侧菜单栏 -->
    <jsp:include page="../admin/include/sider.jsp">
        <jsp:param name="announce" value="announce"/>
    </jsp:include>
    <!-- 右侧内容部分 -->
    <div class="content-wrapper">
        <!-- Main content -->
        <section class="content">


            <!-- 编辑模态框 -->
            <!------------------------------------------------------------------------------------------->
            <div class="modal fade" id="updateModal">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal"><span
                                    aria-hidden="true">&times;</span>
                            </button>
                            <h4 class="modal-title">更新公告</h4>
                        </div>
                        <div class="modal-body">
                            <div class="form-group form_datetime">
                                <label for="id">ID</label>
                                <input type="text" name="id" class="form-control"
                                       id="id" readonly="readonly">
                            </div>
                            <div class="form-group form_datetime">
                                <label for="title">公告标题</label>
                                <input type="text" name="title" class="form-control"
                                       id="title">
                            </div>
                            <div class="form-group form_datetime">
                                <label for="content">公告内容</label>
                                <textarea type="text" name="content" class="form-control"
                                          id="content" rows="8"></textarea>
                            </div>
                            <div class="form-group form_datetime">
                                <label for="author">发布人</label>
                                <input type="text" name="author" class="form-control"
                                       id="author" readonly="readonly">
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                            <button type="button" class="btn btn-primary" id="updateBtn">确定</button>
                        </div>
                    </div><!-- /.modal-content -->
                </div><!-- /.modal-dialog -->
            </div><!-- /.modal -->

            <!-- 编辑模态框 -->
            <!------------------------------------------------------------------------------------------->
            <div class="modal fade" id="viewModal">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal"><span
                                    aria-hidden="true">&times;</span>
                            </button>
                            <h4 class="modal-title">公告内容</h4>
                        </div>
                        <div class="modal-body">
                            <div class="form-group form_datetime">
                                <label for="vid">ID</label>
                                <input type="text" name="vid" class="form-control"
                                       id="vid" readonly="readonly">
                            </div>
                            <div class="form-group form_datetime">
                                <label for="vtitle">公告标题</label>
                                <input type="text" name="vtitle" class="form-control"
                                       id="vtitle" readonly="readonly">
                            </div>
                            <div class="form-group form_datetime">
                                <label for="vcontent">公告内容</label>
                                <textarea type="text" name="vcontent" class="form-control"
                                          id="vcontent" rows="8" readonly="readonly"></textarea>
                            </div>
                            <div class="form-group form_datetime">
                                <label for="vauthor">发布人</label>
                                <input type="text" name="vauthor" class="form-control"
                                       id="vauthor" readonly="readonly">
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        </div>
                    </div><!-- /.modal-content -->
                </div><!-- /.modal-dialog -->
            </div><!-- /.modal -->


            <!-- Default box -->
            <div class="box">
                <div class="box-header with-border">
                    <h3 class="box-title">公告管理</h3>
                    <div class="box-tools pull-right">
                        <a href="<c:url value="/admin/announce/new"/>" class="btn btn-success btn-sm"><i
                                class="fa fa-plus"></i> 发布公告</a>
                        <button id="delBtn" class="btn btn-danger btn-sm" disabled><i class="fa fa-dustbin"></i> 批量删除
                        </button>
                    </div>
                </div>
                <div class="box-body no-padding">
                    <table class="table table-hover">
                        <thead>
                        <tr>
                            <th width="60"><input type="checkbox" id="ckFather"></th>
                            <th style="text-align: left">公告标题</th>
                            <th>发布人</th>
                            <th>身份</th>
                            <th>发布日期</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:if test="${empty announceList}">
                            <tr>
                                <td colspan="6">暂无公告信息</td>
                            </tr>
                        </c:if>
                        <c:forEach items="${announceList}" var="announce">
                            <tr class="rowDetail" rel="${announce.id}">
                                <td><input value="${announce.id}" type="checkbox" class="ckSon"></td>
                                <td style="text-align: left">${announce.title}</td>
                                <td>${announce.authorName}</td>
                                <td hidden="hidden">${announce.content}</td>
                                <td>
                                        ${announce.roleId == '1' ? '管理员' : ''}
                                        ${announce.roleId == '3' ? '教师' : ''}
                                </td>
                                <td><fmt:formatDate value="${announce.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                                <td>
                                    <button class="view btn btn-sm btn-vk" rel="${announce.id}">查看公告</button>
                                    <button class="edit btn btn-sm btn-dropbox" rel="${announce.id}">编辑公告</button>
                                    <a href="javascript:;" class="del btn btn-sm btn-danger"
                                       rel="${announce.id}">删除公告</a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
                <!-- /.box-body -->
            </div>
            <!-- /.box -->
            <ul id="pagination-demo" class="pagination-sm pull-right"></ul>
        </section>
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->
    <%@include file="../include/footer.jsp" %>
</div>
<!-- ./wrapper -->
<%@include file="../include/js.jsp" %>
<script src="<c:url value="/static/plugins/layer/layer.js"/>"></script>
<script>
    // 按钮状态
    $(function () {
        $("#ckFather").click(function () {
            var sons = $(".ckSon");
            for (var i = 0; i < sons.length; i++) {
                sons[i].checked = $(this)[0].checked;
            }
            if ($(this)[0].checked === true) {
                $("#markBtn").removeAttr("disabled");
            } else {
                $("#markBtn").attr("disabled", "disabled");
            }
        });

        $(".ckSon").click(function () {
            var sons = $(".ckSon");
            var num = 0;
            for (var i = 0; i < sons.length; i++) {
                if (sons[i].checked === true) {
                    num++;
                }
            }
            if (num === sons.length) {
                $("#ckFather")[0].checked = true;
            } else {
                $("#ckFather")[0].checked = false;
            }
            if (num > 0) {
                $("#markBtn").removeAttr("disabled");
                $("#delBtn").removeAttr("disabled");
            } else {
                $("#markBtn").attr("disabled", "disabled");
                $("#delBtn").attr("disabled", "disabled");
            }
        });

        //删除一个公告
        $(".del").click(function () {
            var id = $(this).attr("rel");
            layer.confirm("确定要删除么？", function () {
                $.get("/admin/announce/del", {"id": id}).done(function (json) {
                    if (json.state === "success") {
                        layer.alert("删除成功", function () {
                            history.go(0);
                        });
                    } else {
                        layer.msg(json.message);
                    }
                }).error(function () {
                    layer.msg("系统异常...");
                });
            });
        });

        //批量删除公告
        $("#delBtn").click(function () {
            var sons = $(".ckSon");
            var num = 0;
            var ids = '';
            for (var i = 0; i < sons.length; i++) {
                if (sons[i].checked === true) {
                    ids += sons[i].value + ',';
                    num++;
                    console.log(ids);
                }
            }

            // 删除
            layer.confirm("确定要删除么？", function () {
                $.post("/admin/announce/del", {"ids": ids}).done(function (json) {
                    if (json.state === "success") {
                        layer.alert("删除成功", function () {
                            history.go(0);
                        });
                    } else {
                        layer.msg(json.message);
                    }
                }).error(function () {
                    layer.msg("系统异常...");
                });
            });
        });
    });

    // 模态框
    // 启动修改模态框
    $(".edit").click(function () {
        $("#updateModal").modal({
            show: true,
            backdrop: 'static'
        });
        // 模态框赋值
        const id = $(this).attr('rel');
        const title = $(this).parents('tr').find('td').eq(1).text();
        const author = $(this).parents('tr').find('td').eq(2).text();
        const content = $(this).parents('tr').find('td').eq(3).text();
        $('#id').val(id);
        $('#title').val(title);
        $('#author').val(author);
        $('#content').text(content);
    });

    // 模态框
    // 启动修改模态框
    $(".view").click(function () {
        $("#viewModal").modal({
            show: true,
            backdrop: 'static'
        });
        // 模态框赋值
        const id = $(this).attr('rel');
        const title = $(this).parents('tr').find('td').eq(1).text();
        const author = $(this).parents('tr').find('td').eq(2).text();
        const content = $(this).parents('tr').find('td').eq(3).text();
        $('#vid').val(id);
        $('#vtitle').val(title);
        $('#vauthor').val(author);
        $('#vcontent').text(content);
    });

    // 更新
    $('#updateBtn').click(function () {
        const id = $('#id').val();
        const title = $('#title').val();
        const author = $('#author').val();
        const content = $('#content').val();

        layer.confirm("确定修改公告吗?", function () {
            $.post("/admin/announce/update", {
                "id": id,
                "title": title,
                "author": author,
                "content": content
            }).done(function (data) {
                if (data.state === "success") {
                    layer.msg("修改成功!");
                    window.location.reload();
                }
            }).error(function () {
                layer.msg("服务器异常");
            });
        });
    })
</script>
</body>
</html>


