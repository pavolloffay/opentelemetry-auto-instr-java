/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.auto.servlet.v3_0;

import io.opentelemetry.trace.Span;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletResponse;

public class TagSettingAsyncListener implements AsyncListener {

  private final AtomicBoolean responseHandled;
  private final Span span;

  public TagSettingAsyncListener(AtomicBoolean responseHandled, Span span) {
    this.responseHandled = responseHandled;
    this.span = span;
  }

  @Override
  public void onComplete(AsyncEvent event) {
    if (responseHandled.compareAndSet(false, true)) {
      System.out.println("Async tag listener onComplete");
      Servlet3HttpServerTracer.TRACER.end(span, (HttpServletResponse) event.getSuppliedResponse());
    }
  }

  @Override
  public void onTimeout(AsyncEvent event) {
    if (responseHandled.compareAndSet(false, true)) {
      Servlet3HttpServerTracer.TRACER.onTimeout(span, event.getAsyncContext().getTimeout());
    }
  }

  @Override
  public void onError(AsyncEvent event) {
    if (responseHandled.compareAndSet(false, true)) {
      System.out.println("Async tag listener onError");
      Servlet3HttpServerTracer.TRACER.endExceptionally(
          span, event.getThrowable(), (HttpServletResponse) event.getSuppliedResponse());
    }
  }

  /** Transfer the listener over to the new context. */
  @Override
  public void onStartAsync(AsyncEvent event) {
    event.getAsyncContext().addListener(this);
  }
}
