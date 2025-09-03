package com.example.blog.service;

import com.example.blog.model.Category;
import com.example.blog.repository.CategoryRepository;
import com.example.blog.util.SlugUtil;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository repo;
    public CategoryService(CategoryRepository repo) { this.repo = repo; }

    public Category create(Category c) {
        if (c.getSlug() == null || c.getSlug().trim().isEmpty()) {
            c.setSlug(SlugUtil.toSlug(c.getName()));
        } else c.setSlug(SlugUtil.toSlug(c.getSlug()));
        int i = 1;
        String base = c.getSlug();
        while (repo.existsBySlug(c.getSlug())) {
            c.setSlug(base + "-" + i);
            i++;
        }
        return repo.save(c);
    }

    public Category update(String id, Category payload) throws Exception {
        Optional<Category> o = repo.findById(id);
        if (!o.isPresent()) throw new Exception("Category not found");
        Category c = o.get();
        if (payload.getName() != null) c.setName(payload.getName());
        if (payload.getDescription() != null) c.setDescription(payload.getDescription());
        if (payload.getSlug() != null) c.setSlug(SlugUtil.toSlug(payload.getSlug()));
        if (payload.getImgUrl() !=null ) c.setImgUrl(payload.getImgUrl());
        return repo.save(c);
    }

    public void delete(String id) { repo.deleteById(id); }
}

