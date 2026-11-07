package io.app._cents.backend.categories.services;

import io.app._cents.backend.categories.CategoriesRepository;
import io.app._cents.backend.categories.dtos.admin.create.CreateCategoryRequest;
import io.app._cents.backend.categories.dtos.admin.update.UpdateCategoryRequest;
import io.app._cents.backend.categories.models.Category;
import io.app._cents.backend.common.dtos.Response;
import io.app._cents.backend.common.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  public ResponseEntity<Response> deleteCategory(String id) {
    if (!categoriesRepository.existsById(id)) {
      throw new NotFoundException("Category");
    }
    categoriesRepository.deleteById(id);
    return new ResponseEntity<>(new Response(true, "Category deleted successfully"), HttpStatus.OK);
  }

  @Transactional
  public ResponseEntity<Response> updateCategory(String id, UpdateCategoryRequest req) {
    Category category =
        categoriesRepository.findById(id).orElseThrow(() -> new NotFoundException("Category"));

    if (req.getName() != null) {
      category.setName(req.getName());
    }
    if (req.getCode() != null) {
      category.setCode(req.getCode());
    }
    if (req.getImage() != null) {
      category.setImage(req.getImage());
    }

    categoriesRepository.save(category);
    return new ResponseEntity<>(new Response(true, "Category updated successfully"), HttpStatus.OK);
  }
}
