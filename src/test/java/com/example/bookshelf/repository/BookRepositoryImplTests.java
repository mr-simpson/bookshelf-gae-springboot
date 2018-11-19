package com.example.bookshelf.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.bookshelf.model.Book;
import com.example.bookshelf.model.Result;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

class BookRepositoryImplTests {
    
    private LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    private BookRepositoryImpl bookRepositoryImpl;
    
    private static final String BOOK_KIND = "Book";
    
    @BeforeEach
    void setUp() throws Exception {
        this.helper.setUp();
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        this.bookRepositoryImpl = new BookRepositoryImpl(datastoreService);
    }

    @AfterEach
    void tearDown() throws Exception {
        this.helper.tearDown();
    }

    @Test
    void save() {
        Book book = generateBook();
        long saveResult = bookRepositoryImpl.save(book);
        
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Key bookKey = KeyFactory.createKey(BOOK_KIND, saveResult);
        assertEquals(1, ds.prepare(new Query(BOOK_KIND, bookKey)).countEntities(FetchOptions.Builder.withLimit(10)));
    }
    
    @Test
    void findByIdWithValidId() {
        Entity bookEntity = generateBookEntity(generateBook());
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Key bookKey = ds.put(bookEntity);
        
        Book findResult = bookRepositoryImpl.findById(bookKey.getId());
        assertEquals("foo", findResult.getAuthor());
    }
    
    @Test
    void findByIdWithInvalidId() {
        assertNull(bookRepositoryImpl.findById(999L));
    }
    
    @Test
    void findByCursorNumberOfEntityIsOverLimit() {
        List<Entity> entities = generateBookEntities();
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(entities);
        
        Result<Book> result = bookRepositoryImpl.findByCursor(null);
        assertEquals(10, result.result.size());
        assertNotNull(result.cursor);
    }
    
    @Test
    void findByUserAndCursor() {
        List<Entity> entities = generateBookEntities();
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(entities);
        
        Result<Book> result = bookRepositoryImpl.findByUserAndCursor("abcde", null);
        assertEquals(4, result.result.size());
        assertNull(result.cursor);
    }
    
    @Test
    void update() {
        Book oldBook = generateBook();
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Key bookKey = ds.put(generateBookEntity(oldBook));
        
        Book newBook = generateBook();
        newBook.setId(bookKey.getId());
        newBook.setAuthor("new");
        bookRepositoryImpl.update(newBook);
        
        try {
            Entity updatedEntity = ds.get(bookKey);
            assertEquals("new", updatedEntity.getProperty(Book.AUTHOR));
        } catch (EntityNotFoundException e) {
            fail("Entity not found.");
        }
    }
    
    @Test
    void delete() {
        Book book = generateBook();
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Key bookKey = ds.put(generateBookEntity(book));
        int beforeCount = ds.prepare(new Query(BOOK_KIND)).countEntities(FetchOptions.Builder.withLimit(10));
        
        bookRepositoryImpl.delete(bookKey.getId());
        int afterCount = ds.prepare(new Query(BOOK_KIND)).countEntities(FetchOptions.Builder.withLimit(10));
        assertEquals(-1, afterCount - beforeCount);
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
    
    private Entity generateBookEntity(Book book) {
        Entity bookEntity = new Entity(BOOK_KIND);
        bookEntity.setProperty(Book.AUTHOR, book.getAuthor());
        bookEntity.setProperty(Book.DESCRIPTION, book.getDescription());
        bookEntity.setProperty(Book.PUBLISHED_DATE, book.getPublishedDate());
        bookEntity.setProperty(Book.TITLE, book.getTitle());
        bookEntity.setProperty(Book.IMAGE_URL, book.getImageUrl());
        bookEntity.setProperty(Book.CREATED_BY_ID, book.getCreatedById());
        return bookEntity;
    }
    
    private List<Entity> generateBookEntities() {
        List<Entity> entities = new ArrayList<>();
        String[] titles = {"title01", "title02", "title03", "title04", "title05",
                "title06", "title07"};
        for (String title : titles) {
            Book book = generateBook();
            book.setTitle(title);
            entities.add(generateBookEntity(book));
        }
        
        String[] titlesByAnotherId = {"title08", "title09", "title10", "title11"};
        for (String title : titlesByAnotherId) {
            Book book = generateBook();
            book.setTitle(title);
            book.setCreatedById("abcde");
            entities.add(generateBookEntity(book));
        }
        
        return entities;
    }
}
