package com.example.bookshelf.service;

import com.example.bookshelf.model.Book;
import com.example.bookshelf.model.Result;

public interface BookService {
    
    long save(Book book);
    
    void delete(long id);
    
    void update(Book book);
    
    Book findById(long id);
    
    Result<Book> findByCursor(String startCursor);
    
    Result<Book> findByUserAndCursor(String userId, String startCursor);
}
