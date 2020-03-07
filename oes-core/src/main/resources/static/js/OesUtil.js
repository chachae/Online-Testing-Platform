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

/**
 * 日历
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