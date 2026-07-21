package repository;

import entity.Entity;
import java.util.List;
import java.util.Optional;

public interface Repository<T extends Entity<ID>, ID> {
    void save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
}
