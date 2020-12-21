package io.opentelemetry.javaagent.instrumentation.api;

import java.io.InputStream;

/**
 * @author Pavol Loffay
 */
public class InputStreamObjectRegistry {

  public static WeakMap<InputStream, String> objects = WeakMap.Provider.newWeakMap();
}
