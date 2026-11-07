package io.app._cents.backend.categories.services;

import io.app._cents.backend.categories.CategoriesRepository;
import io.app._cents.backend.categories.models.Category;
import io.app._cents.backend.common.exceptions.NotFoundException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CategoriesService {

  @Autowired CategoriesRepository categoriesRepository;

  public List<Category> listAll() {
    return categoriesRepository.findAll();
  }

  public Category findById(String id) {
    return categoriesRepository.findById(id).orElse(null);
  }

  public ResponseEntity<Category> getCategoryById(String id) {
    Category category =
        categoriesRepository
            .findById(id)
            .orElseThrow(
                () -> {
                  throw new NotFoundException("category");
                });
    return new ResponseEntity<>(category, HttpStatus.OK);
  }
}
