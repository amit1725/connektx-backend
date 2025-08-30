package com.example.blog.controller;

import com.example.blog.model.Blog;
import com.example.blog.model.Category;
import com.example.blog.repository.BlogRepository;
import com.example.blog.repository.CategoryRepository;
import com.example.blog.service.SitemapService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PublicController {

    private final BlogRepository blogRepo;
    private final CategoryRepository categoryRepo;
    private final SitemapService sitemapService;

    public PublicController(BlogRepository blogRepo, CategoryRepository categoryRepo, SitemapService sitemapService) {
        this.blogRepo = blogRepo;
        this.categoryRepo = categoryRepo;
        this.sitemapService = sitemapService;
    }

    @GetMapping("/categories/{categorySlug}/blogs")
    public ResponseEntity<List<Blog>> getBlogsByCategory(@PathVariable String categorySlug) {
        Optional<Category> category = categoryRepo.findBySlug(categorySlug);
        if (category.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // use categoryId instead of Category object
        List<Blog> blogs = blogRepo.findByCategoryIdAndPublishedTrue(category.get().getId());
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/blogs")
    public List<Blog> listBlogs() {
        return blogRepo.findAll(); // add pagination as needed
    }
    
    @GetMapping("/blogs/published")
    public List<Blog> listPublishedBlogs(){
    	return blogRepo.findAllByPublishedTrue();
    }
    
    @GetMapping("/blogs/featured")
    public List<Blog> listFeaturedBlog(){
    	return blogRepo.findAllByFeaturedTrue();
    }

    @GetMapping("/blogs/slug/{slug}")
    public ResponseEntity<Blog> getBySlug(@PathVariable String slug) {
        Optional<Blog> b = blogRepo.findBySlug(slug);
        Long count=b.get().getCount();
        b.get().setCount(count+1);
        if(count+1>100) {
        	b.get().setFeatured(true);
        }
        blogRepo.save(b.get());
        return b.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/categories")
    public ResponseEntity<?> listCategories() {
        return ResponseEntity.ok(categoryRepo.findAll());
    }
    
    @GetMapping("/categories/slug/{slug}")
    public ResponseEntity<Category> getBySlugCategory(@PathVariable String slug) {
        Optional<Category> b = categoryRepo.findBySlug(slug);
        return b.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Sitemap endpoint (returns cached/generated XML)
    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> sitemapXml() {
        return ResponseEntity.ok(sitemapService.getSitemap());
    }
}


