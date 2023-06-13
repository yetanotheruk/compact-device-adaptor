package uk.yetanother.compact.device.adaptor.mapping;

import org.hibernate.Hibernate;

import java.util.Collection;

public interface LazyAwareMapper {
    default boolean isInitialized(Collection<?> collection) {
        return Hibernate.isInitialized(collection);
    }

}
