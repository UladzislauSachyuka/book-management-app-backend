package com.book_management.books.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 30)
    private String title;

    @NotBlank
    @Size(max = 30)
    private String author;

    private LocalDate readingStartDate;

    private LocalDate readingEndDate = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Book() {}

    public Book(String title, String author, LocalDate readingStartDate) {
        this.title = title;
        this.author = author;
        this.readingStartDate = readingStartDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getReadingStartDate() {
        return readingStartDate;
    }

    public void setReadingStartDate(LocalDate readingStartDate) {
        this.readingStartDate = readingStartDate;
    }

    public LocalDate getReadingEndDate() {
        return readingEndDate;
    }

    public void setReadingEndDate(LocalDate readingEndDate) {
        this.readingEndDate = readingEndDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
