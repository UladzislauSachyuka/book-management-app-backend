package com.book_management.books.controllers;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.book_management.books.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.book_management.books.models.Book;
import com.book_management.books.repository.BookRepository;
import com.book_management.books.repository.UserRepository;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class BookController {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/books")
    public ResponseEntity<List<Book>> getAllBooks(Principal principal) {
        try {
            String username = principal.getName();

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Book> books = bookRepository.findByUser(user);

            if (books.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(books, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable("id") Long id, Principal principal) {
        try {
            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Optional<Book> bookData = bookRepository.findById(id);
            if (bookData.isPresent() && bookData.get().getUser().equals(user)) {
                return new ResponseEntity<>(bookData.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/books/add")
    public ResponseEntity<Book> createBook(@RequestBody Book book, Principal principal) {
        try {
            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            book.setUser(user);

            Book _book = bookRepository.save(book);
            return new ResponseEntity<>(_book, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable("id") Long id, @RequestBody Book book, Principal principal) {
        try {
            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Optional<Book> bookData = bookRepository.findById(id);
            if (bookData.isPresent() && bookData.get().getUser().equals(user)) {
                Book _book = bookData.get();
                _book.setTitle(book.getTitle());
                _book.setAuthor(book.getAuthor());
                _book.setReadingStartDate(book.getReadingStartDate());
                _book.setReadingEndDate(book.getReadingEndDate());
                return new ResponseEntity<>(bookRepository.save(_book), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<HttpStatus> deleteBook(@PathVariable("id") Long id) {
        try {
            bookRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/books/filter/status")
    public ResponseEntity<List<Book>> getBooksByStatus(
            @RequestParam("status") String status, Principal principal) {
        try {
            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Book> books;
            if (status.equalsIgnoreCase("not-read")) {
                books = bookRepository.findByUserAndReadingEndDateIsNull(user);
            } else if (status.equalsIgnoreCase("read")) {
                books = bookRepository.findByUserAndReadingEndDateIsNotNull(user);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            if (books.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(books, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/books/filter/date")
    public ResponseEntity<List<Book>> getBooksByEndDate(
            @RequestParam("endDate") String endDate, Principal principal) {
        try {
            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            LocalDate parsedDate = LocalDate.parse(endDate);

            List<Book> books = bookRepository.findByUserAndReadingEndDate(user, parsedDate);

            if (books.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(books, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/books/filter/title")
    public ResponseEntity<List<Book>> getBooksByTitle(
            @RequestParam("title") String title, Principal principal) {
        try {
            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Book> books = bookRepository.findByUserAndTitleContainingIgnoreCase(user, title);

            if (books.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(books, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/books/sort/startDate")
    public ResponseEntity<List<Book>> getBooksSortedByStartDate(
            @RequestParam(value = "order", defaultValue = "asc") String order,
            Principal principal) {
        try {
            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Sort sort = order.equalsIgnoreCase("desc") ? Sort.by("readingStartDate").descending() : Sort.by("readingStartDate").ascending();

            List<Book> books = bookRepository.findByUser(user, sort);

            if (books.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(books, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/books/sort/endDate")
    public ResponseEntity<List<Book>> getBooksSortedByEndDate(
            @RequestParam(value = "order", defaultValue = "asc") String order,
            Principal principal) {
        try {
            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Sort sort = order.equalsIgnoreCase("desc") ? Sort.by("readingEndDate").descending() : Sort.by("readingEndDate").ascending();

            List<Book> books = bookRepository.findByUser(user, sort);

            if (books.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(books, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
