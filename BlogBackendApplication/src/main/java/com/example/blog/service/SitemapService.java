package com.example.blog.service;

import com.example.blog.model.Blog;
import com.example.blog.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;
import java.nio.file.*;
import java.io.IOException;

@Service
public class SitemapService {

    private final BlogRepository blogRepository;

    @Value("${app.siteUrl}")
    private String siteUrl;

    @Value("${app.sitemap.filepath:}")
    private String sitemapFilepath;

    private volatile String cachedSitemap = null;

    public SitemapService(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    @jakarta.annotation.PostConstruct
    public void init() {
        regenerate(); // safe here — siteUrl is injected
    }

    public String regenerate() {
        List<Blog> blogs = blogRepository.findAllByPublishedTrue();
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        String base = siteUrl.endsWith("/") ? siteUrl.substring(0, siteUrl.length() - 1) : siteUrl;
        for (Blog b : blogs) {
            sb.append("  <url>\n");
            sb.append("    <loc>").append(base).append("/blog/").append(b.getSlug()).append("</loc>\n");
            sb.append("  </url>\n");
        }
        sb.append("</urlset>");

        cachedSitemap = sb.toString();

        if (sitemapFilepath != null && !sitemapFilepath.trim().isEmpty()) {
            try {
                Files.write(Paths.get(sitemapFilepath), cachedSitemap.getBytes());
            } catch (IOException e) {
                System.err.println("Failed to write sitemap to " + sitemapFilepath + ": " + e.getMessage());
            }
        }
        return cachedSitemap;
    }

    public String getSitemap() {
        if (cachedSitemap == null) {
            regenerate();
        }
        return cachedSitemap;
    }
}
