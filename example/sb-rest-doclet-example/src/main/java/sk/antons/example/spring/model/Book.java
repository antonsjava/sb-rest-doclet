/*
 * 
 */
package sk.antons.example.spring.model;

import java.util.List;

/**
 *
 * @author antons
 */
public class Book {
    private long id;     
    private String title;     
    private int numberOfPages;     
    private Binding binding;     
    private List<Author> authors;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getNumberOfPages() { return numberOfPages; }
    public void setNumberOfPages(int numberOfPages) { this.numberOfPages = numberOfPages; }
    public Binding getBinding() { return binding; }
    public void setBinding(Binding binding) { this.binding = binding; }
    public List<Author> getAuthors() { return authors; }
    public void setAuthors(List<Author> authors) { this.authors = authors; }

    
}
