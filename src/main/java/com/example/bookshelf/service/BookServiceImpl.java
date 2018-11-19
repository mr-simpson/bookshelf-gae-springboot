package com.example.bookshelf.service;

import org.springframework.stereotype.Service;

import com.example.bookshelf.model.Book;
import com.example.bookshelf.model.Result;
import com.example.bookshelf.repository.BookRepository;

@Service
public class BookServiceImpl implements BookService {
    
    private BookRepository bookRepository;
    
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public long save(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public void delete(long id) {
        bookRepository.delete(id);
    }

    @Override
    public void update(Book book) {
        bookRepository.update(book);
    }

    @Override
    public Book findById(long id) {
        return bookRepository.findById(id);
    }

    @Override
    public Result<Book> findByCursor(String startCursor) {
        return bookRepository.findByCursor(startCursor);
    }
    
    @Override
    public Result<Book> findByUserAndCursor(String userId, String startCursor) {
        return bookRepository.findByUserAndCursor(userId, startCursor);
    }
    
}
