package com.example.blog.controller;

import com.example.blog.model.Blog;
import com.example.blog.model.Category;
import com.example.blog.model.AdminUser;
import com.example.blog.repository.AdminUserRepository;
import com.example.blog.service.BlogService;
import com.example.blog.service.CategoryService;
import com.example.blog.util.JwtUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AdminController {

    private final BlogService blogService;
    private final CategoryService categoryService;
    private final AdminUserRepository adminRepo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder;

    public AdminController(BlogService blogService,
                           CategoryService categoryService,
                           AdminUserRepository adminRepo,
                           JwtUtil jwtUtil,
                           PasswordEncoder encoder) {
        this.blogService = blogService;
        this.categoryService = categoryService;
        this.adminRepo = adminRepo;
        this.jwtUtil = jwtUtil;
        this.encoder = encoder;
    }

    // ADMIN BOOTSTRAP - create admin if none exists (call once or use a proper seed script)
    @PostMapping("/bootstrap")
    public ResponseEntity<?> bootstrap(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || password == null) return ResponseEntity.badRequest().body("username & password required");
        if (adminRepo.findByUsername(username).isPresent()) return ResponseEntity.badRequest().body("user exists");
        AdminUser u = new AdminUser();
        u.setUsername(username);
        u.setPasswordHash(encoder.encode(password));
        adminRepo.save(u);
        return ResponseEntity.ok("admin created");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        Optional<AdminUser> o = adminRepo.findByUsername(username);
        if (!o.isPresent()) return ResponseEntity.status(401).body("invalid");
        AdminUser u = o.get();
        if (!encoder.matches(password, u.getPasswordHash())) return ResponseEntity.status(401).body("invalid");
        String token = jwtUtil.generateToken(u.getUsername());
        return ResponseEntity.ok(Map.of("token", token));
    }

    // BLOG CRUD
    @PostMapping("/admin/blogs")
    public ResponseEntity<?> createBlog(@RequestBody Blog blog) {
        Blog saved = blogService.create(blog);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/admin/blogs/{id}")
    public ResponseEntity<?> updateBlog(@PathVariable String id, @RequestBody Blog blog) {
        try {
            Blog updated = blogService.update(id, blog);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/admin/blogs/{id}")
    public ResponseEntity<?> deleteBlog(@PathVariable String id) {
        blogService.delete(id);
        return ResponseEntity.ok(Map.of("deleted", id));
    }

    // CATEGORY CRUD
    @PostMapping("/admin/categories")
    public ResponseEntity<?> createCategory(@RequestBody Category c) {
        return ResponseEntity.ok(categoryService.create(c));
    }

    @PutMapping("/admin/categories/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable String id, @RequestBody Category c) {
        try {
            return ResponseEntity.ok(categoryService.update(id, c));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/admin/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable String id) {
        categoryService.delete(id);
        return ResponseEntity.ok(Map.of("deleted", id));
    }
}
