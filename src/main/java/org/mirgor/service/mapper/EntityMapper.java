package org.mirgor.service.mapper;

public interface EntityMapper<E, D> {

    E fromDto(D entityDto);

    D toDto(E entity);
}
