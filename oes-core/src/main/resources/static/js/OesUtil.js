// 静态方法
function OesUtil() {
}

// 封装 confirm 弹层
OesUtil.confirm = function oesConfirm(content, fnc) {
    let s = "function" == typeof fnc;
    if (s) {
        $.confirm({
            title: '确认框',
            content: content,
            buttons: {
                confirm: {
                    text: '确定',
                    btnClass: 'btn btn-primary',
                    action: fnc
                },
                cancel: {
                    text: '取消',
                    btnClass: 'btn btn-danger',
                    action: function () {
                        toastr.success("取消操作");
                    }
                }
            }
        })
    } else {
        toastr.error("参数异常");
    }
};