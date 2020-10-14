package io.opentelemetry.javaagent.instrumentation.sparkjava;

import static java.util.Collections.singletonMap;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.tooling.Instrumenter;
import java.lang.reflect.Method;
import java.util.Map;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Spark;

@AutoService(Instrumenter.class)
public class SparkJavaBodyInstrumentation extends Instrumenter.Default {

  public SparkJavaBodyInstrumentation() {
    super("sparkjava", "sparkjava-2.4", "body", "sparkjava-body", "sparkjava-2.4-body");
  }

  @Override
  public String[] helperClassNames() {
    return new String[] {
        packageName + ".SparkJavaBodyInstrumentation$SparkFilter",
    };
  }

  @Override
  public ElementMatcher<? super TypeDescription> typeMatcher() {
    return named("spark.Spark");
  }

  @Override
  public Map<? extends ElementMatcher<? super MethodDescription>, String> transformers() {
    return singletonMap(
        named("awaitInitialization")
            .and(isPublic()),
        SparkJavaBodyInstrumentation.class.getName() + "$StartAdvice");
  }

  public static class SparkFilter implements Filter{
    @Override
    public void handle(Request request, Response response) throws Exception {
      Thread.dumpStack();
      System.out.println("Spark filter");
    }
  }

  public static class StartAdvice {
    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void awaitInitialization(@Advice.Origin Class<Spark> clazz) {
      System.out.println("---> \n\n\n\n Sparkjava start");
      System.out.println(clazz);
      try {
        // Spark 2.3 has this method, Spark 2.9 accepts Filter[].class
        // We could either catch java.lang.NoSuchMethodException or intercept spark.webserver.MatcherFilter.doFilter
        /*
        {}
java.lang.Exception: Stack trace
	at java.base/java.lang.Thread.dumpStack(Thread.java:1388)
	at org.hypertrace.smoketest.sparkjava.App.lambda$initSpark$1(App.java:26)
	at spark.RouteImpl$1.handle(RouteImpl.java:58)
	at spark.webserver.MatcherFilter.doFilter(MatcherFilter.java:162)
	at spark.webserver.JettyHandler.doHandle(JettyHandler.java:61)
	at org.eclipse.jetty.server.session.SessionHandler.doScope(SessionHandler.java:189)
	at org.eclipse.jetty.server.handler.ScopedHandler.handle(ScopedHandler.java:141)
	at org.eclipse.jetty.server.handler.HandlerWrapper.handle(HandlerWrapper.java:119)
	at org.eclipse.jetty.server.Server.handle(Server.java:517)
	at org.eclipse.jetty.server.HttpChannel.handle(HttpChannel.java:302)
	at org.eclipse.jetty.server.HttpConnection.onFillable(HttpConnection.java:242)
	at org.eclipse.jetty.io.AbstractConnection$ReadCallback.succeeded(AbstractConnection.java:245)
	at org.eclipse.jetty.io.FillInterest.fillable(FillInterest.java:95)
	at org.eclipse.jetty.io.SelectChannelEndPoint$2.run(SelectChannelEndPoint.java:75)
	at org.eclipse.jetty.util.thread.strategy.ExecuteProduceConsume.produceAndRun(ExecuteProduceConsume.java:213)
	at org.eclipse.jetty.util.thread.strategy.ExecuteProduceConsume.run(ExecuteProduceConsume.java:147)
	at org.eclipse.jetty.util.thread.QueuedThreadPool.runJob(QueuedThreadPool.java:654)
	at org.eclipse.jetty.util.thread.QueuedThreadPool$3.run(QueuedThreadPool.java:572)
	at java.base/java.lang.Thread.run(Thread.java:834)
java.lang.Exception: Stack trace
	at java.base/java.lang.Thread.dumpStack(Thread.java:1388)
	at io.opentelemetry.javaagent.instrumentation.sparkjava.SparkJavaBodyInstrumentation$SparkFilter.handle(SparkJavaBodyInstrumentation.java:51)
	at spark.FilterImpl$1.handle(FilterImpl.java:59)
	at spark.webserver.MatcherFilter.doFilter(MatcherFilter.java:192)
	at spark.webserver.JettyHandler.doHandle(JettyHandler.java:61)
	at org.eclipse.jetty.server.session.SessionHandler.doScope(SessionHandler.java:189)
	at org.eclipse.jetty.server.handler.ScopedHandler.handle(ScopedHandler.java:141)
	at org.eclipse.jetty.server.handler.HandlerWrapper.handle(HandlerWrapper.java:119)
	at org.eclipse.jetty.server.Server.handle(Server.java:517)
	at org.eclipse.jetty.server.HttpChannel.handle(HttpChannel.java:302)
	at org.eclipse.jetty.server.HttpConnection.onFillable(HttpConnection.java:242)
	at org.eclipse.jetty.io.AbstractConnection$ReadCallback.succeeded(AbstractConnection.java:245)
	at org.eclipse.jetty.io.FillInterest.fillable(FillInterest.java:95)
	at org.eclipse.jetty.io.SelectChannelEndPoint$2.run(SelectChannelEndPoint.java:75)
	at org.eclipse.jetty.util.thread.strategy.ExecuteProduceConsume.produceAndRun(ExecuteProduceConsume.java:213)
	at org.eclipse.jetty.util.thread.strategy.ExecuteProduceConsume.run(ExecuteProduceConsume.java:147)
	at org.eclipse.jetty.util.thread.QueuedThreadPool.runJob(QueuedThreadPool.java:654)
	at org.eclipse.jetty.util.thread.QueuedThreadPool$3.run(QueuedThreadPool.java:572)
	at java.base/java.lang.Thread.run(Thread.java:834)
Spark filter

         */
        Method after = clazz.getMethod("after", Filter.class);
        System.out.println(after.getParameters());
        after.setAccessible(true);
        System.out.println(after);
        after.invoke(null, new SparkFilter());
        System.out.println("after invoke");
      } catch (Throwable e) {
        e.printStackTrace();
      }
      System.out.println("---> \n\n\n\n Sparkjava start end advice");
    }
  }
}
