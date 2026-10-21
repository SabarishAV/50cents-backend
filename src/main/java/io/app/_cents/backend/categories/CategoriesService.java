package io.app._cents.backend.categories;

import io.app._cents.backend.categories.models.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriesService {

    @Autowired CategoriesRepository categoriesRepository;

    public List<Category> listAll(){
        return categoriesRepository.findAll();
    }

}
