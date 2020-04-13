package sk.antons.example.spring.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sk.antons.example.spring.model.Book;

/**
 * Example of spring boot controller. Provides some examples of 
 * REST api definitions.
 */
@CrossOrigin
@RestController
@RequestMapping(path = "/example")
public class ExampleController {

    private static Logger log = LoggerFactory.getLogger(ExampleController.class);

    /**
	 * Search for books. If both parameters are empty empty list is returned
     * 
     * @param title substring of book title 
     * @param authorSurname substring of book authod surname
     * @return list of books
     */
    @GetMapping(path = "/book"
        , produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody List<Book> bookSearch(
              @RequestParam(name = "title", required = false) String title
              , @RequestParam(name = "authorSurname", required = false) String authorSurname
            ) {
        return null;    
    }
    
    /**
     * Reads book by id. Id is mandatory 
     * @param id id of the book. 
     * @return book with id or null
     */
    @GetMapping(path = "/book/{id}"
        , produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody Book readBook(@PathVariable(name = "id") long id) throws UnknownBookException {
        return null;
    }
    
    /**
     * Creates new book. Attribute title ia mandatory 
     * @param book book to be created
     * @return new created book
     */
    @PostMapping(path = "/book"
        , produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody Book createBook(@RequestBody Book book        
                ) {
        return book;
    }
    
    /**
     * Delete book. 
     * @param id id of book to be deleted
     */
    @DeleteMapping(path = "/book/{id}"
    )
    public void deleteBook(@PathVariable(name = "id") long id) {
    }
    
    /**
     * Update existing book.
     * @param id id of book to be updated
     * @param book Data of book to update
     * @return updated book
     */
    @PutMapping(path = "/book/{id}"
        , produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody Book updateBook(@PathVariable(name = "id") long id
                    , @RequestBody Book book        
                ) throws UnknownBookException {
        return book;
    }
    
}
