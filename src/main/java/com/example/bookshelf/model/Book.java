package com.example.bookshelf.model;

import java.util.Date;

public class Book {
  
    private String title;
    private String author;
    private String createdById;
    private Date publishedDate;  
    private String description;
    private Long id;
    private String imageUrl;
    
    public static final String AUTHOR = "author";
    public static final String CREATED_BY_ID = "createdById";
    public static final String DESCRIPTION = "description";
    public static final String ID = "id";
    public static final String PUBLISHED_DATE = "publishedDate";
    public static final String TITLE = "title";
    public static final String IMAGE_URL = "imageUrl";

    private Book(Builder builder) {
        this.title = builder.title;
        this.author = builder.author;
        this.createdById = builder.createdById;
        this.publishedDate = builder.publishedDate;
        this.description = builder.description;
        this.id = builder.id;
        this.imageUrl = builder.imageUrl;
    }
    
    public static class Builder {
        private String title;
        private String author;
        private String createdById;
        private Date publishedDate;
        private String description;
        private Long id;
        private String imageUrl;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder createdById(String createdById) {
            this.createdById = createdById;
            return this;
        }

        public Builder publishedDate(Date publishedDate) {
            this.publishedDate = publishedDate;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Book build() {
            return new Book(this);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreatedById() {
        return createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "Title: " + title + ", Author: " + author + ", Published date: " + publishedDate + ", Added by: "
                + createdById;
    }
}
