package com.example.bookshelf.stub;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.bookshelf.model.Book;
import com.example.bookshelf.model.Result;
import com.example.bookshelf.service.BookService;

public class StubBookService implements BookService {

    @Override
    public long save(Book book) {
        return 1L;
    }

    @Override
    public void delete(long id) {
    }

    @Override
    public void update(Book book) {
    }

    @Override
    public Book findById(long id) {
        Book book = generateBook();
        book.setId(id);
        return book;
    }

    @Override
    public Result<Book> findByCursor(String startCursor) {
        List<Book> bookList = new ArrayList<Book>();
        bookList.add(generateBook());
        return new Result<Book>(bookList, "findByCursor");
    }

    @Override
    public Result<Book> findByUserAndCursor(String userId, String startCursor) {
        List<Book> bookList = new ArrayList<Book>();
        bookList.add(generateBook());
        return new Result<Book>(bookList, "findByUserAndCursor");
    }

    private Book generateBook() {
        Date now = new Date();
        return new Book.Builder()
                .author("foo")
                .description("test description")
                .publishedDate(now)
                .title("test title")
                .imageUrl("http://example.com")
                .createdById("12345678")
                .build();
    }
}
