/*
 * 
 */
package sk.antons.example.spring.model;

/**
 * Author of book. 
 * @author antons
 */
public class Author {
    private long id;
    private String givenName; 
    private String surname;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getGivenName() { return givenName; }
    public void setGivenName(String givenName) { this.givenName = givenName; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
   
}
