// 密码加密
function encryptPass(password) {
  // 创建 JSEncrypt 对象
  let enc = new JSEncrypt();
  // rsa 加密传输公钥（私钥在后端进行解密）
  let pk = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANL378k3RiZHWx5AfJqdH9xRNBmD9wGD2" +
      "iRe41HdTNF8RUhNnHit5NpMNtGL0NPTSSpPjjI1kJfVorRvaQerUgkCAwEAAQ==";
  // 设置公钥
  enc.setPublicKey(pk);
  // 加密
  return enc.encrypt(password);
}
