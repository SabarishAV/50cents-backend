package io.app._cents.backend.categories.controllers;

import io.app._cents.backend.categories.services.CategoriesService;
import io.app._cents.backend.categories.models.Category;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
@Tag(name = "Category APIs", description = "Category APIs")
public class CategoryController {

    @Autowired
    CategoriesService categoriesService;

    @GetMapping
    @Operation(summary = "List categories", description = "List all categories")
    public ResponseEntity<List<Category>> listCategories(){
        List<Category> categories = categoriesService.listAll();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

}
