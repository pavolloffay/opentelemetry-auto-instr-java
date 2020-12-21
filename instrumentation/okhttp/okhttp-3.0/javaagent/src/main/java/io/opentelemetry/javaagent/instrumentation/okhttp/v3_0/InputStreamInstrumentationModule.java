/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.okhttp.v3_0;

import static io.opentelemetry.javaagent.tooling.bytebuddy.matcher.AgentElementMatchers.extendsClass;
import static io.opentelemetry.javaagent.tooling.bytebuddy.matcher.AgentElementMatchers.safeHasSuperType;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.namedOneOf;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.instrumentation.api.InputStreamObjectRegistry;
import io.opentelemetry.javaagent.tooling.InstrumentationModule;
import io.opentelemetry.javaagent.tooling.TypeInstrumentation;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

@AutoService(InstrumentationModule.class)
public class InputStreamInstrumentationModule extends InstrumentationModule {

  public InputStreamInstrumentationModule() {
    super("inputstream", "ht");
  }

  @Override
  public List<TypeInstrumentation> typeInstrumentations() {
    return Collections.singletonList(new InputStreamInstrumentation());
  }

  static class InputStreamInstrumentation implements TypeInstrumentation {

    @Override
    public ElementMatcher<? super TypeDescription> typeMatcher() {
      return extendsClass(namedOneOf(InputStream.class.getName()));
    }

    @Override
    public Map<? extends ElementMatcher<? super MethodDescription>, String> transformers() {
      Map<ElementMatcher<? super MethodDescription>, String> transformers = new HashMap<>();
      transformers.put(
          named("read").and(takesArguments(0)).and(isPublic()),
          InputStreamInstrumentationModule.class.getName() + "$InputStream_ReadNoArgsAdvice");
      //      transformers.put(
      //          named("read")
      //              .and(takesArguments(1))
      //              .and(takesArgument(0, is(byte[].class)))
      //              .and(isPublic()),
      //          InputStreamInstrumentationModule.class.getName() +
      // "$InputStream_ReadByteArrayAdvice");
      //      transformers.put(
      //          named("read")
      //              .and(takesArguments(3))
      //              .and(takesArgument(0, is(byte[].class)))
      //              .and(takesArgument(1, is(int.class)))
      //              .and(takesArgument(2, is(int.class)))
      //              .and(isPublic()),
      //          InputStreamInstrumentationModule.class.getName()
      //              + "$InputStream_ReadByteArrayOffsetAdvice");
      //      transformers.put(
      //          named("readAllBytes").and(takesArguments(0)).and(isPublic()),
      //          InputStream_ReadAllBytes.class.getName());
      //      transformers.put(
      //          named("readNBytes")
      //              .and(takesArguments(0))
      //              .and(takesArgument(0, is(byte[].class)))
      //              .and(takesArgument(1, is(int.class)))
      //              .and(takesArgument(2, is(int.class)))
      //              .and(isPublic()),
      //          InputStreamInstrumentationModule.class.getName() + "$InputStream_ReadNBytes");
      return transformers;
    }
  }

  public static class InputStream_ReadNoArgsAdvice {
    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void enter(@Advice.This InputStream thizz) {
//      System.out.println(InputStreamObjectRegistry.objects);
      if (InputStreamObjectRegistry.objects.get(thizz) != null) {
        System.out.println("ENTER\n\n\n");
      }
    }

    //    @Advice.OnMethodExit(suppress = Throwable.class)
    //    public static void exit(@Advice.This InputStream thizz, @Advice.Return int read) {
    //      System.out.println("\n\nreading no args");
    //      InputStreamUtils.read(thizz, read);
    //    }
  }

  public static class InputStream_ReadByteArrayAdvice {
    @Advice.OnMethodExit(suppress = Throwable.class)
    public static void exit(
        @Advice.This InputStream thizz, @Advice.Return int read, @Advice.Argument(0) byte b[]) {
      System.out.println("\n\nreading byte array");
    }
  }

  //  public static class InputStream_ReadByteArrayOffsetAdvice {
  //    @Advice.OnMethodExit(suppress = Throwable.class)
  //    public static void exit(
  //        @Advice.This InputStream thizz,
  //        @Advice.Return int read,
  //        @Advice.Argument(0) byte b[],
  //        @Advice.Argument(1) int off,
  //        @Advice.Argument(2) int len) {
  //      System.out.println("\n\nreading byte array offset");
  //      InputStreamUtils.read(thizz, read, b, off, len);
  //    }
  //  }
  //
  //  public static class InputStream_ReadAllBytes {
  //    @Advice.OnMethodExit(suppress = Throwable.class)
  //    public static void exit(@Advice.This InputStream thizz, @Advice.Return byte[] b)
  //        throws IOException {
  //      System.out.println("\n\nreading All");
  //      InputStreamUtils.readAll(thizz, b);
  //    }
  //  }
  //
  //  public static class InputStream_ReadNBytes {
  //    @Advice.OnMethodExit(suppress = Throwable.class)
  //    public static void exit(
  //        @Advice.This InputStream thizz,
  //        @Advice.Return int read,
  //        @Advice.Argument(0) byte[] b,
  //        @Advice.Argument(1) int off,
  //        @Advice.Argument(2) int len) {
  //      System.out.println("\n\nreading N Bytes");
  //      InputStreamUtils.readNBytes(thizz, read, b, off, len);
  //    }
  //  }
}
