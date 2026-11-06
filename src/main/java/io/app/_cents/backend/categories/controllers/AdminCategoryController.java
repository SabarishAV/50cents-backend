package io.app._cents.backend.categories.controllers;

import io.app._cents.backend.categories.dtos.admin.create.CreateCategoryRequest;
import io.app._cents.backend.categories.services.AdminCategoriesService;
import io.app._cents.backend.common.annotations.AdminOnly;
import io.app._cents.backend.common.dtos.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/categories")
@Tag(name = "Admin category APIs", description = "Admin category APIs - Admin only endpoints")
@AdminOnly
public class AdminCategoryController {

    @Autowired AdminCategoriesService adminCategoriesService;

    @PostMapping
    @Operation(summary = "Create category", description = "Create category by admin")
    public ResponseEntity<Response> createCategory(
            @Valid @RequestBody CreateCategoryRequest req
    ){
       return adminCategoriesService.create(req);
    }

}
