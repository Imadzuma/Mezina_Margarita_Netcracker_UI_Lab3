package library.shared;

import java.util.Date;
import com.google.gwt.json.client.*;

public class Book {
    // Fields
    private int id;
    private String author;
    private String name;
    private int pageCount;
    private int year;
    private Date addDate;

    // Constructors
    public Book(int id, String author, String name, int pageCount, int year, Date addDate) {
        this.id = id;
        this.author = author;
        this.name = name;
        this.pageCount = pageCount;
        this.year = year;
        this.addDate = addDate;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }
    public String getAuthor() {
        return author;
    }
    public String getName() {
        return name;
    }
    public int getPageCount() {
        return pageCount;
    }
    public int getYear() {
        return year;
    }
    public Date getAddDate() {
        return addDate;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    // Override methods
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", author='" + author + '\'' +
                ", name='" + name + '\'' +
                ", pageCount=" + pageCount +
                ", year=" + year +
                ", addDate=" + addDate +
                '}';
    }
}
