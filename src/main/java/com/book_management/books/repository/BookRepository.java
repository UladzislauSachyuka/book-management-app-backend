package com.book_management.books.repository;

import com.book_management.books.models.Book;
import com.book_management.books.models.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByUser(User user);

    List<Book> findByUser(User user, Sort sort);

    List<Book> findByUserAndReadingEndDateIsNull(User user);

    List<Book> findByUserAndReadingEndDateIsNotNull(User user);

    List<Book> findByUserAndReadingEndDate(User user, LocalDate readingEndDate);

    @Query("SELECT b FROM Book b WHERE b.user = :user AND LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Book> findByUserAndTitleContainingIgnoreCase(@Param("user") User user, @Param("title") String title);
}
