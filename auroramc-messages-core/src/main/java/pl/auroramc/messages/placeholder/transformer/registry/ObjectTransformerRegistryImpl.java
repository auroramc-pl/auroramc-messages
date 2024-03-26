package pl.auroramc.messages.placeholder.transformer.registry;

import java.util.HashMap;
import java.util.Map;
import pl.auroramc.messages.placeholder.transformer.ObjectTransformer;
import pl.auroramc.messages.placeholder.transformer.ObjectTransformerPack;

class ObjectTransformerRegistryImpl implements ObjectTransformerRegistry {

  private final Map<Class<?>, ObjectTransformer<?, ?>> transformersByTypes;

  ObjectTransformerRegistryImpl() {
    this.transformersByTypes = new HashMap<>();
  }

  @Override
  public <T, R> ObjectTransformer<T, R> getTransformer(final Class<?> type) {
    // noinspection unchecked
    return (ObjectTransformer<T, R>) transformersByTypes.get(type);
  }

  @Override
  public boolean hasTransformer(final Class<?> type) {
    return transformersByTypes.containsKey(type);
  }

  @Override
  public void register(final ObjectTransformer<?, ?> transformer) {
    transformersByTypes.put(transformer.getType(), transformer);
  }

  @Override
  public void register(final ObjectTransformerPack transformerPack) {
    transformerPack.register(this);
  }
}
