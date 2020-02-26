//加密
function encrypt(pass) {
    // 创建 JSEncrypt 对象
    let enc = new JSEncrypt();
    // rsa 加密传输公钥（私钥在后端进行解密）
    let pulicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANL378k3RiZHWx5AfJqdH9xRNBmD9wGD2" +
        "iRe41HdTNF8RUhNnHit5NpMNtGL0NPTSSpPjjI1kJfVorRvaQerUgkCAwEAAQ==";
    // 设置公钥
    enc.setPublicKey(pulicKey);
    // 加密
    pass = enc.encrypt(pass);
    $('.realPass').val(pass);
}

//登录界面学号验证
function login_studentID_username() {
    var stuIDval = document.getElementById("studentId").value;
    var patt1 = new RegExp(/^[0-9]+$/);
    var studentCue = document.getElementById("studentCue");
    if (!patt1.test(stuIDval)) {
        studentCue.innerHTML = "<span><b>学号只能为纯数字</b></span>";
    }
    // 明文
    let psw = $(".encPass").val();
    // 调用加密方法
    encrypt(psw);
    // 返回校验结果
    return patt1.test(stuIDval);
}

//登录界面职工号验证
function login_teacherID_username() {
    var stuIDval = document.getElementById("teacherId").value;
    var patt1 = new RegExp(/^[0-9]+$/);
    var teacherCue = document.getElementById("teacherCue");
    if (!patt1.test(stuIDval)) {
        teacherCue.innerHTML = "<span><b>职工号只能为纯数字</b></span>";
    }
    // 明文
    let psw = $(".encPass").val();
    // 调用加密方法
    encrypt(psw);
    return patt1.test(stuIDval);
}

function login_admin() {
    // 明文
    let psw = $(".encPass").val();
    // 调用加密方法
    encrypt(psw);
}
