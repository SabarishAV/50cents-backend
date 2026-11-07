package io.app._cents.backend.categories.controllers;

import io.app._cents.backend.categories.dtos.admin.create.CreateCategoryRequest;
import io.app._cents.backend.categories.dtos.admin.update.UpdateCategoryRequest;
import io.app._cents.backend.categories.services.AdminCategoriesService;
import io.app._cents.backend.common.annotations.AdminOnly;
import io.app._cents.backend.common.dtos.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/categories")
@Tag(name = "Admin category APIs", description = "Admin category APIs - Admin only endpoints")
@AdminOnly
public class AdminCategoryController {

  @Autowired AdminCategoriesService adminCategoriesService;

  @PostMapping
  @Operation(summary = "Create category", description = "Create category")
  public ResponseEntity<Response> createCategory(@Valid @RequestBody CreateCategoryRequest req) {
    return adminCategoriesService.create(req);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete category by id", description = "Delete category by id")
  public ResponseEntity<Response> deleteCategory(
      @Parameter(description = "Category ID", required = true) @PathVariable String id) {
    return adminCategoriesService.deleteCategory(id);
  }

  @PatchMapping("/{id}")
  @Operation(summary = "Update category", description = "Update category by id")
  public ResponseEntity<Response> updateCategory(
      @Parameter(description = "Category ID", required = true) @PathVariable String id,
      @Valid @RequestBody UpdateCategoryRequest req) {
    return adminCategoriesService.updateCategory(id, req);
  }
}
