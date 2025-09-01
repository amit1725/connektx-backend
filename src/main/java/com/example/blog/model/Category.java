package com.example.blog.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categories")
public class Category {
    @Id
    private String id;
    private String name;
    private String slug;
    private String description;
    private String imgUrl;

    public Category() {}

    // getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getImgUrl() { return imgUrl; }
    public void setImgUrl(String imgUrl){ this.imgUrl=imgUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

