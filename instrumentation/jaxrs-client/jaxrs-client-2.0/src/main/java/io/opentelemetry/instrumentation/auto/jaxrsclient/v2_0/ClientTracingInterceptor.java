package io.opentelemetry.instrumentation.auto.jaxrsclient.v2_0;

import io.opentelemetry.trace.Span;
import io.opentelemetry.trace.Span.Kind;
import java.io.IOException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

public class ClientTracingInterceptor implements WriterInterceptor, ReaderInterceptor {

  private static final JaxRsClientTracer TRACER = JaxRsClientTracer.TRACER;

  @Override
  public void aroundWriteTo(WriterInterceptorContext interceptorContext) throws WebApplicationException, IOException {
    Object spanObj = interceptorContext.getProperty(ClientTracingFilter.SPAN_PROPERTY_NAME);
    if (spanObj instanceof Span) {
      Span span = (Span) spanObj;
      span.addEvent("write_start");
      try {
        interceptorContext.proceed();
      } catch (Exception e) {
        span.recordException(e);
        throw e;
      } finally {
        span.addEvent("write_end");
      }
    } else {
      interceptorContext.proceed();
    }
  }

  @Override
  public Object aroundReadFrom(ReaderInterceptorContext interceptorContext) throws WebApplicationException, IOException {
//    Span span = TRACER.startSpan("deserialize", Kind.INTERNAL);
    Object spanObj = interceptorContext.getProperty(ClientTracingFilter.SPAN_PROPERTY_NAME);
//    Span span = T
    try {
      return interceptorContext.proceed();
    } catch (Exception e) {
//      span.recordException(e);
      throw e;
    } finally {
//      span.end();
    }
  }
}
