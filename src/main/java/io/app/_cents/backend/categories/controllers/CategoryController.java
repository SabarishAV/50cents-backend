package io.app._cents.backend.categories.controllers;

import io.app._cents.backend.categories.dtos.predict.PredictCategoryRequest;
import io.app._cents.backend.categories.models.Category;
import io.app._cents.backend.categories.services.CategoriesService;
import io.app._cents.backend.categories.services.CategoryPredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categories")
@Tag(name = "Category APIs", description = "Category APIs")
public class CategoryController {

  @Autowired CategoriesService categoriesService;
  @Autowired CategoryPredictionService categoryPredictionService;

  @GetMapping
  @Operation(summary = "List categories", description = "List all categories")
  public ResponseEntity<List<Category>> listCategories() {
    List<Category> categories = categoriesService.listAll();
    return new ResponseEntity<>(categories, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get category by id", description = "Get category by id")
  public ResponseEntity<Category> getCategory(
      @Parameter(description = "Category ID", required = true) @PathVariable String id) {
    return categoriesService.getCategoryById(id);
  }

  @GetMapping("/predict")
  @Operation(
      summary = "Predict category",
      description = "Predict a category for a payment based on recipient and note")
  public ResponseEntity<Map<String, String>> predictCategory(
      @Valid @ParameterObject PredictCategoryRequest req) {
    return categoryPredictionService.predictCode(req);
  }
}
