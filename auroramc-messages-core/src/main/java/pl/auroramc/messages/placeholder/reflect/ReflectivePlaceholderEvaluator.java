package pl.auroramc.messages.placeholder.reflect;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

class ReflectivePlaceholderEvaluator implements PlaceholderEvaluator {

  private static final Lookup LOOKUP = MethodHandles.lookup();
  private static final Map<Class<?>, MethodType> METHOD_TYPE_CACHE = new HashMap<>();
  private static final Map<PlaceholderCompositeKey, Class<?>> RETURN_TYPE_CACHE = new HashMap<>();

  ReflectivePlaceholderEvaluator() {}

  @Override
  public Object evaluate(final Object object, final String path) {
    return evaluate(object, path, getReturnType(object, path));
  }

  @Override
  public Object evaluate(final Object object, final String path, final Class<?> type) {
    final MethodType methodType = METHOD_TYPE_CACHE.computeIfAbsent(type, MethodType::methodType);
    try {
      return LOOKUP.findVirtual(object.getClass(), path, methodType).invoke(object);
    } catch (final Throwable exception) {
      throw new PlaceholderEvaluationException(
          "Could not evaluate placeholders, because of unexpected exception.", exception);
    }
  }

  @Override
  public Class<?> getReturnType(final Object object, final String path) {
    return RETURN_TYPE_CACHE.computeIfAbsent(
        new PlaceholderCompositeKey(object.getClass(), path), key -> getReturnType0(object, path));
  }

  private Class<?> getReturnType0(final Object object, final String path) {
    final Class<?> clazz = object.getClass();
    try {
      final Method method = clazz.getMethod(path);
      if (method.getReturnType() != void.class) {
        return method.getReturnType();
      }

      return clazz.getField(path).getType();
    } catch (final NoSuchMethodException | NoSuchFieldException exception) {
      return null;
    }
  }

  private record PlaceholderCompositeKey(Class<?> type, String path) {}
}
