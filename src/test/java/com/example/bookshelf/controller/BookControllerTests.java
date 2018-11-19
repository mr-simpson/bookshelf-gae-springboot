package com.example.bookshelf.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;

import com.example.bookshelf.form.AddForm;
import com.example.bookshelf.service.BookService;
import com.example.bookshelf.stub.StubBookService;
import com.example.bookshelf.util.CloudStorageHelper;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

class BookControllerTests {
    
    private BookController bookController;
    
    private BookService bookService = new StubBookService();
    
    private CloudStorageHelper cloudStorageHelper = new CloudStorageHelper(null, null);
    
    private Model model;
    
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalUserServiceTestConfig());

    @BeforeEach
    void setUp() throws Exception {
        this.helper.setUp();
        UserService userService = UserServiceFactory.getUserService();
        this.bookController = new BookController(bookService, cloudStorageHelper, userService);
        this.model = new ExtendedModelMap();
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    void getBookList() {
        assertEquals("list", bookController.getBookList(null, model));
        Map<String, Object> modelMap = model.asMap();
        assertAll("model", 
                () -> assertNotNull(modelMap.get("bookList")),
                () -> assertNotNull(modelMap.get("cursor")));
    }
    
    @Test
    void getMyBooksUserIsNotLoggedIn() {
        helper.setEnvIsLoggedIn(false);
        HttpServletRequest request = new MockHttpServletRequest(null, "/books/mine");
        assertEquals("redirect:/_ah/login?continue=%2Fbooks%2Fmine", bookController.getMyBooks(null, model, request));
    }
    
    @Test
    void getMyBooksUserIsLoggedIn() {
        helper.setEnvIsLoggedIn(true).setEnvEmail("test@example.com").setEnvAuthDomain("example.com");
        HttpServletRequest request = new MockHttpServletRequest(null, "/books/mine");
        assertEquals("list", bookController.getMyBooks(null, model, request));
        Map<String, Object> modelMap = model.asMap();
        assertAll("model", 
                () -> assertNotNull(modelMap.get("bookList")),
                () -> assertNotNull(modelMap.get("cursor")));
    }
    
    @Test
    void getAddForm() {
        AddForm addForm = new AddForm();
        assertEquals("form", bookController.getAddForm(addForm, model));
        Map<String, Object> modelMap = model.asMap();
        assertAll("model", 
                () -> assertEquals("Add", modelMap.get("action")),
                () -> assertEquals("add", modelMap.get("destination")));
    }
    
    @Test
    void addBookBindingResultHasErrors() throws IOException {
        AddForm addForm = new AddForm();
        BindingResult bindingResult = new DataBinder(addForm).getBindingResult();
        bindingResult.addError(new FieldError("addForm", "test", "test"));
        assertEquals("form", bookController.addBook(addForm, bindingResult, model));
        Map<String, Object> modelMap = model.asMap();
        assertAll("model", 
                () -> assertEquals("Add", modelMap.get("action")),
                () -> assertEquals("add", modelMap.get("destination")));
    }
    
    @Test
    void addBookImageFileUploaded() throws IOException {
        AddForm addForm = new AddForm();
        addForm.setFile(new MockMultipartFile("file", "test".getBytes()));
        BindingResult bindingResult = new DataBinder(addForm).getBindingResult();
        assertEquals("redirect:/read?id=1", bookController.addBook(addForm, bindingResult, model));
    }
    
    @Test
    void addBookUserIsLoggedIn() throws IOException {
        helper.setEnvIsLoggedIn(true).setEnvEmail("test@example.com").setEnvAuthDomain("example.com");
        AddForm addForm = new AddForm();
        BindingResult bindingResult = new DataBinder(addForm).getBindingResult();
        assertEquals("redirect:/read?id=1", bookController.addBook(addForm, bindingResult, model));
    }
    
    @Test
    void readBook() {
        assertEquals("view", bookController.readBook(1L, model));
        Map<String, Object> modelMap = model.asMap();
        assertNotNull(modelMap.get("book"));
    }
    
    @Test
    void getEditForm() {
        AddForm addForm = new AddForm();
        BindingResult bindingResult = new DataBinder(addForm).getBindingResult();
        bindingResult.addError(new FieldError("addForm", "test", "test"));
        assertEquals("form", bookController.getEditForm(1L, addForm, model));
        assertAll("form",
                () -> assertEquals("1", addForm.getId()),
                () -> assertEquals("foo", addForm.getAuthor()),
                () -> assertEquals("test title", addForm.getTitle()),
                () -> assertNotNull(addForm.getPublishedDate()),
                () -> assertNotNull("http://example.com", addForm.getImageUrl()),
                () -> assertEquals("test description", addForm.getDescription()));
        Map<String, Object> modelMap = model.asMap();
        assertAll("model", 
                () -> assertEquals("Edit", modelMap.get("action")),
                () -> assertEquals("edit", modelMap.get("destination")));
    }
    
    @Test
    void editBookBindingResultHasErrors() throws IOException {
        AddForm addForm = new AddForm();
        BindingResult bindingResult = new DataBinder(addForm).getBindingResult();
        bindingResult.addError(new FieldError("addForm", "test", "test"));
        assertEquals("form", bookController.editBook(addForm, bindingResult, model));
        Map<String, Object> modelMap = model.asMap();
        assertAll("model", 
                () -> assertEquals("Edit", modelMap.get("action")),
                () -> assertEquals("edit", modelMap.get("destination")));
    }
    
    @Test
    void editBookImageFileUploaded() throws IOException {
        AddForm addForm = new AddForm();
        addForm.setId("3");
        addForm.setFile(new MockMultipartFile("file", "test".getBytes()));
        BindingResult bindingResult = new DataBinder(addForm).getBindingResult();
        assertEquals("redirect:/read?id=3", bookController.editBook(addForm, bindingResult, model));
    }
    
    @Test
    void editBookImageFileNotUploaded() throws IOException {
        AddForm addForm = new AddForm();
        addForm.setId("2");
        BindingResult bindingResult = new DataBinder(addForm).getBindingResult();
        assertEquals("redirect:/read?id=2", bookController.editBook(addForm, bindingResult, model));
    }
    
    @Test
    void deleteBook() {
        assertEquals("redirect:/books", bookController.deleteBook(1L));
    }
    
    @Test
    void login() {
        assertEquals("redirect:/_ah/login?continue=%2Fbooks", bookController.login());
    }
    
    @Test
    void logout() {
        assertEquals("redirect:/_ah/logout?continue=%2Fbooks", bookController.logout());
    }
}
