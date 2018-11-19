package com.example.bookshelf.repository;

import com.example.bookshelf.model.Book;
import com.example.bookshelf.model.Result;

public interface BookRepository {
    
    long save(Book book);
    
    Book findById(long id);
    
    Result<Book> findByCursor(String startCursorString);
    
    Result<Book> findByUserAndCursor(String userId, String startCursorString);
    
    void update(Book book);
    
    void delete(long id);
}
