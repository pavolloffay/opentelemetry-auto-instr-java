package io.opentelemetry.instrumentation.auto.servlet.v3_0.bodycapture;

import io.opentelemetry.trace.Span;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class BodyCaptureAsyncListener implements AsyncListener {

  private final AtomicBoolean responseHandled;
  private final Span span;

  public BodyCaptureAsyncListener(AtomicBoolean responseHandled, Span span) {
    this.responseHandled = responseHandled;
    this.span = span;
    span.setAttribute("foo", "bar");
  }

  @Override
  public void onComplete(AsyncEvent event) throws IOException {
    span.setAttribute("onComplete", "yes");
    if (responseHandled.compareAndSet(false, true)) {
      System.out.println("Body capture onComplete");
      System.out.println(span);
      annotateRequest(event.getSuppliedRequest());
      annotateResponse(event.getSuppliedResponse());
    }
    System.out.println("finishing setting attributes");
  }

  @Override
  public void onError(AsyncEvent event) throws IOException {
    if (responseHandled.compareAndSet(false, true)) {
      System.out.println("Body capture onError");
      annotateRequest(event.getSuppliedRequest());
      annotateResponse(event.getSuppliedResponse());
    }
  }

  @Override
  public void onTimeout(AsyncEvent event) throws IOException {
  }

  @Override
  public void onStartAsync(AsyncEvent event) throws IOException {
  }

  private void annotateRequest(ServletRequest servletRequest) {
    if (servletRequest instanceof BufferingHttpServletRequest) {
      BufferingHttpServletRequest bufferingRequest = (BufferingHttpServletRequest) servletRequest;
      span.setAttribute(
          "request.body", bufferingRequest.getBufferedBodyAsString());
      System.out.println("setting attributes to buffering request");
    } else {
      System.out.println("\n\n\n--->it is not buffering request!");
    }
  }

  private void annotateResponse(ServletResponse servletResponse) {
    if (servletResponse instanceof BufferingHttpServletResponse) {
      BufferingHttpServletResponse bufferingResponse = (BufferingHttpServletResponse) servletResponse;
      // set response headers
      String responseBody = bufferingResponse.getBufferAsString();
      System.out.printf("Response body %s\n", responseBody);
      span.setAttribute(
          "response.body", responseBody);
      System.out.println("setting attributes to buffering response");
      for (String headerName : bufferingResponse.getHeaderNames()) {
        System.out.printf("response header %s %s\n", headerName, bufferingResponse.getHeader(headerName));
        String headerValue = bufferingResponse.getHeader(headerName);
        span.setAttribute("response.header." + headerName, headerValue);
      }
    } else {
      System.out.println("\n\n--->it is not buffering response!");
    }
  }
}
