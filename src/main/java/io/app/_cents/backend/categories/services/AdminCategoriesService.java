package io.app._cents.backend.categories.services;

import io.app._cents.backend.categories.CategoriesRepository;
import io.app._cents.backend.categories.dtos.admin.create.CreateCategoryRequest;
import io.app._cents.backend.categories.models.Category;
import io.app._cents.backend.common.dtos.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AdminCategoriesService {

  @Autowired CategoriesRepository categoriesRepository;

  public ResponseEntity<Response> create(CreateCategoryRequest c) {
    Category category = Category.builder().name(c.getName()).code(c.getCode()).build();
    if (!c.getImage().isEmpty()) {
      category.setImage(c.getImage());
    }

    categoriesRepository.save(category);
    return new ResponseEntity<>(
        new Response(true, "Category created successfully"), HttpStatus.CREATED);
  }
}
