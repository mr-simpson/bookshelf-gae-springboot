package com.example.bookshelf.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.bookshelf.model.Book;
import com.example.bookshelf.model.Result;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultIterator;

@Repository
public class BookRepositoryImpl implements BookRepository {

    private DatastoreService datastoreService;

    private static final String BOOK_KIND = "Book";

    public BookRepositoryImpl(DatastoreService datastoreService) {
        this.datastoreService = datastoreService;
    }

    @Override
    public long save(Book book) {
        Entity bookEntity = new Entity(BOOK_KIND);
        bookEntity.setProperty(Book.AUTHOR, book.getAuthor());
        bookEntity.setProperty(Book.DESCRIPTION, book.getDescription());
        bookEntity.setProperty(Book.PUBLISHED_DATE, book.getPublishedDate());
        bookEntity.setProperty(Book.TITLE, book.getTitle());
        bookEntity.setProperty(Book.IMAGE_URL, book.getImageUrl());
        bookEntity.setProperty(Book.CREATED_BY_ID, book.getCreatedById());

        Key bookKey = datastoreService.put(bookEntity);
        return bookKey.getId();
    }

    @Override
    public Book findById(long id) {
        try {
            Entity bookEntity = datastoreService.get(KeyFactory.createKey(BOOK_KIND, id));
            return entityToBook(bookEntity);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    @Override
    public Result<Book> findByCursor(String startCursorString) {
        Query query = new Query(BOOK_KIND).addSort(Book.TITLE, SortDirection.ASCENDING);
        return fetch(query, startCursorString);
    }

    @Override
    public Result<Book> findByUserAndCursor(String userId, String startCursorString) {
        Query query = new Query(BOOK_KIND)
                .setFilter(new Query.FilterPredicate(Book.CREATED_BY_ID, Query.FilterOperator.EQUAL, userId))
                .addSort(Book.TITLE, SortDirection.ASCENDING);
        return fetch(query, startCursorString);
    }
    
    private Result<Book> fetch(Query query, String startCursorString) {
        FetchOptions fetchOptions = FetchOptions.Builder.withLimit(10);
        if (startCursorString != null && !startCursorString.equals("")) {
            fetchOptions.startCursor(Cursor.fromWebSafeString(startCursorString));
        }

        PreparedQuery preparedQuery = datastoreService.prepare(query);
        QueryResultIterator<Entity> results = preparedQuery.asQueryResultIterator(fetchOptions);

        List<Book> bookList = entitiesToBooks(results);
        Cursor cursor = results.getCursor();
        if (cursor != null && bookList.size() == 10) {
            String cursorString = cursor.toWebSafeString();
            return new Result<>(bookList, cursorString);
        } else {
            return new Result<>(bookList);
        }
    }

    @Override
    public void update(Book book) {
        Key bookKey = KeyFactory.createKey(BOOK_KIND, book.getId());
        Entity bookEntity = new Entity(bookKey);
        bookEntity.setProperty(Book.AUTHOR, book.getAuthor());
        bookEntity.setProperty(Book.DESCRIPTION, book.getDescription());
        bookEntity.setProperty(Book.PUBLISHED_DATE, book.getPublishedDate());
        bookEntity.setProperty(Book.TITLE, book.getTitle());
        bookEntity.setProperty(Book.IMAGE_URL, book.getImageUrl());
        bookEntity.setProperty(Book.CREATED_BY_ID, book.getCreatedById());

        datastoreService.put(bookEntity);
    }

    @Override
    public void delete(long id) {
        Key key = KeyFactory.createKey(BOOK_KIND, id);
        datastoreService.delete(key);
    }

    private Book entityToBook(Entity entity) {
        return new Book.Builder().author((String) entity.getProperty(Book.AUTHOR))
                .description((String) entity.getProperty(Book.DESCRIPTION)).id(entity.getKey().getId())
                .publishedDate((Date) entity.getProperty(Book.PUBLISHED_DATE))
                .imageUrl((String) entity.getProperty(Book.IMAGE_URL))
                .createdById((String) entity.getProperty(Book.CREATED_BY_ID))
                .title((String) entity.getProperty(Book.TITLE)).build();
    }

    public List<Book> entitiesToBooks(Iterator<Entity> entities) {
        List<Book> bookList = new ArrayList<>();
        while (entities.hasNext()) {
            bookList.add(entityToBook(entities.next()));
        }
        return bookList;
    }
}
