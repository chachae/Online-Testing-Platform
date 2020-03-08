// 静态方法
function OesUtil() {
}

/**
 * 封装 confirm 弹层
 * @param content 弹层内容
 * @param fnc 方法体
 */
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

/**
 * 封装日历选择器
 * @param e 日期输入框
 * @param doubleTime  是否为双日期（true）
 * @param second 有无时分秒（有则true）
 */
OesUtil.dateTimePick = function calenders(e, doubleTime, second) {
    let singleNot, formatDate;
    singleNot = doubleTime !== true;
    if (second === true) {
        formatDate = "YYYY-MM-DD HH:mm:ss";
    } else {
        formatDate = "YYYY-MM-DD HH:mm";
    }

    $(e).daterangepicker({
        "singleDatePicker": singleNot,
        "timePicker": true,
        "timePicker24Hour": true,
        "showDropdowns": true,
        "timePickerIncrement": 1,
        "linkedCalendars": false,
        "autoApply": true,
        "autoUpdateInput": false,
        "locale": {
            "format": 'YYYY-MM-DD',
            "separator": " -222 ",
            "applyLabel": "确定",
            "cancelLabel": "取消",
            "fromLabel": "起始时间",
            "toLabel": "结束时间'",
            "customRangeLabel": "自定义",
            "weekLabel": "W",
            "daysOfWeek": ["日", "一", "二", "三", "四", "五", "六"],
            "monthNames": ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
            "firstDay": 1
        }
    }, function (start, end, label) {
        if (second === true && doubleTime === true) {
            $(e).val($.trim(start.format('YYYY-MM-DD HH:mm:ss') + '~' + end.format('YYYY-MM-DD HH:mm:ss')));
        } else if (second === false && doubleTime === true) {
            $(e).val($.trim(start.format('YYYY-MM-DD HH:mm') + '~' + end.format('YYYY-MM-DD HH:mm')));
        } else if (second === false && doubleTime === false) {
            $(e).val(start.format('YYYY-MM-DD HH:mm'));
        } else if (second === true && doubleTime === false) {
            $(e).val(start.format('YYYY-MM-DD HH:mm:ss'));
        }
    });
    //清空
    $(e).siblings().click(function () {
        $(e).val('');
    })
};

/**
 * 刷新表单
 * @param e 表单元素 ID / class
 */
OesUtil.refreshTable = function refreshTable(e) {
    $(e).bootstrapTable("refresh");
};

/**
 * 关闭模态框
 * @param e 元素 ID / class
 */
OesUtil.closeModal = function closeModal(e) {
    $(e).modal('hide');
};

/**
 * 封装简单的表单
 * @param e ID / class
 * @param api 请求数据接口
 * @param method 请求方式
 * @param columns 表单数据
 */
OesUtil.initNormalTable = function normalTable(e, api, method, columns) {
    $(e).bootstrapTable({
        method: method,
        url: api,//请求路径
        queryParamsType: '',
        queryParams: function (params) {
            return {
                current: params.pageNumber,
                size: params.pageSize
            }
        },
        responseHandler: function (res) {
            //在ajax获取到数据，渲染表格之前，修改数据源
            const nres = [];
            nres.push({total: res.total, rows: res.list});
            return nres[0];
        },
        buttonsClass: 'primary',
        pageNumber: 1, //初始化加载第一页
        pageSize: 10,//单页记录数
        pageList: [5, 10, 20, 30, 50],//可选择单页记录数
        pagination: true,//是否分页
        sidePagination: 'server',//server:服务器端分页|client：前端分页
        columns: columns
    })
};