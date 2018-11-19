package com.example.bookshelf.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.bookshelf.form.AddForm;
import com.example.bookshelf.model.Book;
import com.example.bookshelf.model.Result;
import com.example.bookshelf.service.BookService;
import com.example.bookshelf.util.CloudStorageHelper;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;

@Controller
public class BookController {
    
    private BookService bookService;
    
    private CloudStorageHelper cloudStorageHelper;
    
    private UserService userService;
    
    public BookController(BookService booksService, CloudStorageHelper cloudStorageHelper, 
            UserService userService) {
        this.bookService = booksService;
        this.cloudStorageHelper = cloudStorageHelper;
        this.userService = userService;
    }
    
    @GetMapping({"/", "/books"})
    public String getBookList(@RequestParam(name="cursor", required=false) String startCursor, Model model) {        
        Result<Book> bookList = bookService.findByCursor(startCursor);
        model.addAttribute("bookList", bookList.result);
        model.addAttribute("cursor", bookList.cursor);
        return "list";
    }
    
    @GetMapping("/books/mine")
    public String getMyBooks(@RequestParam(name="cursor", required=false) String startCursor, 
            Model model, HttpServletRequest request) {
        if (!userService.isUserLoggedIn()) {
            return "redirect:" + userService.createLoginURL(request.getRequestURI());
        }
        
        Result<Book> bookList = bookService.findByUserAndCursor(
                userService.getCurrentUser().getUserId(), startCursor);
        model.addAttribute("bookList", bookList.result);
        model.addAttribute("cursor", bookList.cursor);
        return "list";
    }
    
    @GetMapping("/add")
    public String getAddForm(@ModelAttribute AddForm addForm, Model model) {
        model.addAttribute("action", "Add");
        model.addAttribute("destination", "add");
        return "form";
    }
    
    @PostMapping("/add")
    public String addBook(@Validated @ModelAttribute AddForm addForm, 
            BindingResult bindingResult, Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("action", "Add");
            model.addAttribute("destination", "add");
            return "form";
        }
        
        MultipartFile uploadFile = addForm.getFile();
        String newImageUrl = null;
        if (uploadFile != null && uploadFile.getSize() > 0) {
            newImageUrl = cloudStorageHelper.save(
                uploadFile.getOriginalFilename(), uploadFile.getBytes());
        }
            
        String createdByIdString = "";
        if (userService.isUserLoggedIn()) {
            User user = userService.getCurrentUser();
            createdByIdString = user.getUserId();
        }
        
        Book book = new Book.Builder()
                .author(addForm.getAuthor())
                .description(addForm.getDescription())
                .publishedDate(addForm.getPublishedDate())
                .title(addForm.getTitle())
                .imageUrl(null == newImageUrl ? null : newImageUrl)
                .createdById(createdByIdString)
                .build();
        
        long id = bookService.save(book);
        return "redirect:/read?id=" + id;
    }
    
    @GetMapping("/read")
    public String readBook(@RequestParam("id") long id, Model model) {
        Book book = bookService.findById(id);
        model.addAttribute("book", book);
        return "view";
    }
    
    @GetMapping("/edit")
    public String getEditForm(@RequestParam("id") long id, @ModelAttribute AddForm addForm, Model model) {
        Book book = bookService.findById(id);
        addForm.setId(String.valueOf(book.getId()));
        addForm.setAuthor(book.getAuthor());
        addForm.setTitle(book.getTitle());
        addForm.setDescription(book.getDescription());
        addForm.setPublishedDate(book.getPublishedDate());
        addForm.setImageUrl(book.getImageUrl());
        
        model.addAttribute("action", "Edit");
        model.addAttribute("destination", "edit");
        
        return "form";
    }
    
    @PostMapping("/edit")
    public String editBook(@Validated @ModelAttribute AddForm addForm, BindingResult bindingResult, Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("action", "Edit");
            model.addAttribute("destination", "edit");
            return "form";
        }
        
        MultipartFile uploadFile = addForm.getFile();
        String newImageUrl = null;
        if (uploadFile != null && uploadFile.getSize() > 0) {
            newImageUrl = cloudStorageHelper.save(
                uploadFile.getOriginalFilename(), uploadFile.getBytes());
        }
        
        Book oldBook = bookService.findById(Long.decode(addForm.getId()));
        
        Book book = new Book.Builder()
                .author(addForm.getAuthor())
                .description(addForm.getDescription())
                .id(Long.decode(addForm.getId()))
                .publishedDate(addForm.getPublishedDate())
                .title(addForm.getTitle())
                .imageUrl(null == newImageUrl ? oldBook.getImageUrl() : newImageUrl)
                .createdById(oldBook.getCreatedById())
                .build();
        
        bookService.update(book);
        return "redirect:/read?id=" + Long.valueOf(book.getId());
    }
    
    @GetMapping("/delete")
    public String deleteBook(@RequestParam("id") long id) {
        bookService.delete(id);
        return "redirect:/books";
    }

    @GetMapping("/login")
    public String login() {
        return "redirect:" + userService.createLoginURL("/books");  
    }
    
    @GetMapping("/logout")
    public String logout() {
        return "redirect:" + userService.createLogoutURL("/books");
    }
}
