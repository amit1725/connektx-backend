package com.example.blog.service;

import com.example.blog.model.Blog;
import com.example.blog.repository.AdminUserRepository;
import com.example.blog.repository.BlogRepository;
import com.example.blog.util.SlugUtil;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Optional;

@Service
public class BlogService {

    private final BlogRepository blogRepo;
    private final SitemapService sitemapService;
   
    public BlogService(BlogRepository blogRepo, SitemapService sitemapService) {
        this.blogRepo = blogRepo;
        this.sitemapService = sitemapService;
    }

    public Blog create(Blog blog) {
        String slug = blog.getSlug();
        if (slug == null || slug.trim().isEmpty()) {
            slug = SlugUtil.toSlug(blog.getTitle());
        } else {
            slug = SlugUtil.toSlug(slug);
        }
        slug = ensureUniqueSlug(slug);
        blog.setSlug(slug);
        blog.setCreatedAt(new Date());
        blog.setUpdatedAt(new Date());
        Blog saved = blogRepo.save(blog);
        sitemapService.regenerate();
        return saved;
    }

    public Blog update(String id, Blog payload) throws Exception {
        Optional<Blog> o = blogRepo.findById(id);
        if (!o.isPresent()) throw new Exception("Blog not found");
        Blog b = o.get();

        if (payload.getTitle() != null) b.setTitle(payload.getTitle());
        if (payload.getContent() != null) b.setContent(payload.getContent());
        if (payload.getExcerpt() != null) b.setExcerpt(payload.getExcerpt());
        if (payload.getCategoryId() != null) b.setCategoryId(payload.getCategoryId());
        if (payload.getThumbnail() !=null ) b.setThumbnail(payload.getThumbnail());
        b.setPublished(payload.isPublished());
        b.setCreatedBy(payload.getCreatedBy());

        // handle slug change
        if (payload.getSlug() != null && !payload.getSlug().trim().isEmpty()) {
            String newSlug = SlugUtil.toSlug(payload.getSlug());
            if (!newSlug.equals(b.getSlug())) {
                newSlug = ensureUniqueSlug(newSlug);
                b.setSlug(newSlug);
            }
        }

        b.setUpdatedAt(new Date());
        Blog saved = blogRepo.save(b);
        sitemapService.regenerate();
        return saved;
    }

    public void delete(String id) {
        blogRepo.deleteById(id);
        sitemapService.regenerate();
    }

    private String ensureUniqueSlug(String base) {
        String candidate = base;
        int i = 1;
        while (blogRepo.existsBySlug(candidate)) {
            candidate = base + "-" + i;
            i++;
        }
        return candidate;
    }
}

