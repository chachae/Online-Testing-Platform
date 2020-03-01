package com.chachae.exam.common.util;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.digest.BCrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 密码工具
 *
 * @author yzn
 * @since 2020/1/18 12:11
 */
@Component
public class RsaCipherUtil {

  private RsaCipherUtil() {}

  /** 后端解密私钥 [merbac.properties] */
  private static String rsaPrivateKey;

  @Value("${rsa.private.key}")
  private void setRsaPrivateKey(String rsaPrivateKey) {
    RsaCipherUtil.rsaPrivateKey = rsaPrivateKey;
  }

  /** 密码加密 */
  public static String hash(String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt());
  }

  /** 密码解密 */
  public static String decrypt(String crypt) {
    RSA rsa = new RSA(rsaPrivateKey, null);
    return new String(rsa.decrypt(crypt, KeyType.PrivateKey));
  }

  /** 验证密码 */
  public static boolean verify(String password, String crypt) {
    return BCrypt.checkpw(password, crypt);
  }
}
