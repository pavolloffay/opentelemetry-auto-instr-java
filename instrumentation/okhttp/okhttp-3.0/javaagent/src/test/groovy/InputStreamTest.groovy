import foo.TestByteArrayInputStream
import foo.TestInputStream
import io.opentelemetry.instrumentation.test.AgentTestRunner
import io.opentelemetry.javaagent.instrumentation.api.InputStreamObjectRegistry;

class InputStreamTest extends AgentTestRunner {

  def "basic input stream test"() {
//    when:

    given:
    InputStream inputStream = new TestByteArrayInputStream("hello".getBytes());
    InputStreamObjectRegistry.objects.put(inputStream, new String(""))
    System.out.println(InputStreamObjectRegistry.objects)

    System.out.println(inputStream.getClass().getClassLoader())

//    inputStream.read(new byte[3]);
    while (inputStream.read() != -1) {
      System.out.println("reading")
    }

    println("Spock test")
//    then:
//    status == 200
//    assertTraces(1) {
//      trace(0, 2 + extraClientSpans()) {
//        clientSpan(it, 0, null, method, url)
//        serverSpan(it, 1 + extraClientSpans(), span(extraClientSpans()))
//      }
//    }

//    where:
//    path << ["/success", "/success?with=params"]

//    method = "GET"
//    url = server.address.resolve(path)
  }
}
