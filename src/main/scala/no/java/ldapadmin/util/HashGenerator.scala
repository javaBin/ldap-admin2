package no.java.ldapadmin.util

import java.security.MessageDigest
import java.nio.charset.Charset
import org.apache.commons.codec.binary.Base64


object HashGenerator {
  private val algorithm = "md5"
  private val digest: MessageDigest = MessageDigest.getInstance(algorithm);
  private val charset: Charset = Charset.forName("UTF-8")

  def createHash(password: String): String = {
    val data: Array[Byte] = charset.encode(password).array
    val fingerPrint: Array[Byte] = digest.digest(data);

    val result = new StringBuffer();
    result.append('{');
    result.append(algorithm);
    result.append('}');
    result.append(new String(new Base64().encode(fingerPrint)));
    return result.toString();
  }
}