package foo;

import java.io.ByteArrayInputStream;

/**
 * @author Pavol Loffay
 */
public class TestByteArrayInputStream extends ByteArrayInputStream {
  public TestByteArrayInputStream(byte[] buf) {
    super(buf);
  }
}
