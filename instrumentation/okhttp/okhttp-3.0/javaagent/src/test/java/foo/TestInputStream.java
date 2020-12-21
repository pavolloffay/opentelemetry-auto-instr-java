package foo;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Pavol Loffay
 */
public class TestInputStream extends InputStream {

  public TestInputStream(byte[] bytes) {
  }

  @Override
  public int read() throws IOException {
    return -1;
  }

}
