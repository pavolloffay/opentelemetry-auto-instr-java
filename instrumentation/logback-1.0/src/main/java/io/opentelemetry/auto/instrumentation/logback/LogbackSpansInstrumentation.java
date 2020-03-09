/*
 * Copyright 2020, OpenTelemetry Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.opentelemetry.auto.instrumentation.logback;

import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import com.google.auto.service.AutoService;
import io.opentelemetry.auto.tooling.Instrumenter;
import java.util.HashMap;
import java.util.Map;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import unshaded.ch.qos.logback.classic.spi.ILoggingEvent;

@AutoService(Instrumenter.class)
public class LogbackSpansInstrumentation extends Instrumenter.Default {
  public LogbackSpansInstrumentation() {
    super("logback");
  }

  @Override
  public ElementMatcher<? super TypeDescription> typeMatcher() {
    return named("unshaded.ch.qos.logback.classic.Logger");
  }

  @Override
  public String[] helperClassNames() {
    return new String[] {packageName + ".LogbackSpans"};
  }

  @Override
  public Map<? extends ElementMatcher<? super MethodDescription>, String> transformers() {
    final Map<ElementMatcher<? super MethodDescription>, String> transformers = new HashMap<>();
    transformers.put(
        isMethod()
            .and(isPublic())
            .and(named("callAppenders"))
            .and(takesArguments(1))
            .and(takesArgument(0, named("unshaded.ch.qos.logback.classic.spi.ILoggingEvent"))),
        LogbackSpansInstrumentation.class.getName() + "$CallAppendersAdvice");
    return transformers;
  }

  public static class CallAppendersAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void methodEnter(@Advice.Argument(0) final ILoggingEvent event) {
      LogbackSpans.capture(event);
    }
  }
}