package io.app._cents.backend.categories;

import io.app._cents.backend.categories.models.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriesRepository extends JpaRepository<Category, String> {
  Optional<Category> findByCode(String code);
}
