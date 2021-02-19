package com.project.bookapp.controllers;

import com.project.bookapp.domain.Book;
import com.project.bookapp.domain.Bookshelf;
import com.project.bookapp.domain.googlebooks.Item;
import com.project.bookapp.payload.bookspayload.AddBooksToBookshelfRequest;
import com.project.bookapp.payload.bookspayload.BookAddedResponse;
import com.project.bookapp.payload.bookspayload.CreateBookshelfRequest;
import com.project.bookapp.payload.bookspayload.RemoveBookFromBookshelfRequest;
import com.project.bookapp.services.BookService;
import com.project.bookapp.services.ValidationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(BookController.BASE_URL)
public class BookController {
    public static final String BASE_URL = "/api/books";

    private final BookService bookService;
    private final ValidationService validationService;

    public BookController(BookService bookService, ValidationService validationService) {
        this.bookService = bookService;
        this.validationService = validationService;
    }

    @PostMapping("/create-bookshelf")
    public ResponseEntity<?> createBookShelf(@Valid @RequestBody CreateBookshelfRequest createBookShelfRequest,
                                             BindingResult result, Principal principal) throws Exception {

        if (result.hasErrors()) {
            return validationService.createErrorResponse(result);
        }

        String username = principal.getName();
        String bookShelfName = createBookShelfRequest.getBookshelfName();

        Bookshelf newBookshelf = bookService.createBookShelf(username, bookShelfName);

        return new ResponseEntity<>(newBookshelf, HttpStatus.CREATED);
    }

    @DeleteMapping("/remove/{bookshelfName}")
    public ResponseEntity<?> removeBookshelf(@PathVariable String bookshelfName, Principal principal) {

        String username = principal.getName();
        bookService.removeBookshelf(username, bookshelfName);

        return ResponseEntity.ok(bookshelfName + " removed.");
    }


    @PostMapping("/add-book-to-bookshelf")
    public ResponseEntity<?> addBookToBookshelf(@Valid @RequestBody AddBooksToBookshelfRequest request,
                                                BindingResult result,
                                                Principal principal) {

        if (result.hasErrors()) {
            return validationService.createErrorResponse(result);
        }

        String bookshelfName = request.getBookshelfName();
        String username = principal.getName();
        Item bookItem = request.getBook();

        Book addedBook = bookService.addBookToBookshelf(username, bookshelfName, bookItem);

        // send helpful message to the frontend
        String responseMessage = bookItem.volumeInfo.title + " added to " + bookshelfName + ".";
        BookAddedResponse response = new BookAddedResponse(responseMessage, addedBook);


        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/remove-book-from-bookshelf")
    public ResponseEntity<?> removeBookFromBookshelf(@Valid @RequestBody RemoveBookFromBookshelfRequest request,
                                                     BindingResult result, Principal principal) {

        if (result.hasErrors()) {
            return validationService.createErrorResponse(result);
        }

        String bookshelfName = request.getBookshelfName();
        String username = principal.getName();
        Book bookItem = request.getBook();

        bookService.removeBookFromBookshelf(username, bookshelfName, bookItem);

        String responseMessage = bookItem.getTitle() + " removed from " + bookshelfName + ".";

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @GetMapping("/get-bookshelves")
    public ResponseEntity<?> getUsersBookshelves(Principal principal) {

        String username = principal.getName();


        List<Bookshelf> bookshelves = bookService.getUsersBookShelves(username);

        return new ResponseEntity<>(bookshelves, HttpStatus.OK);
    }

    @GetMapping("/{bookshelfName}")
    public ResponseEntity<?> getBookshelf(@PathVariable String bookshelfName, Principal principal) {


        String username = principal.getName();

        Bookshelf bookshelf = bookService.getBookshelf(username, bookshelfName);

        return new ResponseEntity<>(bookshelf, HttpStatus.OK);
    }
}
