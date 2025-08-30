package com.example.blog.repository;

import com.example.blog.model.Blog;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
import java.util.List;

public interface BlogRepository extends MongoRepository<Blog, String> {
    Optional<Blog> findBySlug(String slug);
    boolean existsBySlug(String slug);
    List<Blog> findAllByPublishedTrue();
    List<Blog> findAllByFeaturedTrue();
    List<Blog> findByCategoryIdAndPublishedTrue(String categoryId);
}


