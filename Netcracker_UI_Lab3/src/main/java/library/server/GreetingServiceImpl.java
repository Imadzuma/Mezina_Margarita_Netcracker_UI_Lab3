package library.server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import library.client.GreetingService;
import library.shared.Book;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import library.shared.FieldVerifier;
import org.json.simple.parser.ParseException;

import java.beans.Expression;
import java.io.*;
import java.util.*;

@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {
  // Fields
  List<Book> books;
  File file;

  // GreetService methods
  @Override
  public void reading() {
    System.out.println("Read books");
    books = new ArrayList<>();
    try {
    InputStreamReader stream = new InputStreamReader(this.getClass().getResourceAsStream("/books.json"));
      Scanner in = new Scanner(stream);
      String jsonStr = in.nextLine();
      System.out.println(jsonStr);
      JSONParser parser = new JSONParser();
      JSONArray jsonArray = (JSONArray)parser.parse(jsonStr);
      for(int i = 0; i < jsonArray.size(); ++i) {
        JSONObject jsonBook = (JSONObject)jsonArray.get(i);
        Long id = (Long)jsonBook.get("id");
        String author = (String)jsonBook.get("author");
        String name = (String)jsonBook.get("name");
        Long page = (Long)jsonBook.get("page");
        Long year = (Long)jsonBook.get("year");
        Long date = (Long)jsonBook.get("date");
        System.out.println("Book: " + id + " " + author + " " + name + " " + page + " " + year + " " + date);
        Book book = new Book(id.intValue(), author, name, page.intValue(), year.intValue(), new Date(date.longValue()));
        books.add(book);
      }
      stream.close();
    } catch (FileNotFoundException e) {
      System.out.println("Can't find file");
    } catch (ParseException e) {
      System.out.println("Can't parse file");
    } catch (IOException e) {
      System.out.println("Can't close file");
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("Reading result: " + books.toString());
  }
  @Override
  public String getBooks(int from, int to, String sortBy, String author, String name, Integer fromPage, Integer toPage,
                         Integer fromYear, Integer toYear, Long fromDate, Long toDate) {
    System.out.println("Get books from " + from + " to " + to);
    List<Book> filtrationBooks = filtraion(sortBy, author, name, fromPage, toPage, fromYear, toYear, fromDate, toDate);
    System.out.println("Filtration result: " + filtrationBooks.toString());
    if (filtrationBooks.size() < from) return "";
    JSONArray array = new JSONArray();
    if (filtrationBooks.size() < to) to = filtrationBooks.size();
    for (int i = from; i < to; ++i)
      array.add(toJSONObject(filtrationBooks.get(i)));
    String json = array.toJSONString();
    System.out.println("Send: " + json);
    return json;
  }
  @Override
  public void deleteBook(int id) {
    System.out.println("Delete book with id: " + id);
    Book delBook = null;
    for(Book book: books) {
      if (book.getId() == id)
        delBook = book;
    }
    books.remove(delBook);
    writing();
  }
  @Override
  public Integer addBook(String author, String name, String page, String year) {
    System.out.println("Try add book: " + author + " " + name + " " + page + " " + year);
    boolean checker[] = check(author, name, page, year);
    if (checker[0] && checker[1] && checker[2] && checker[3]) {
      Book book = new Book(generateID(), author, name, Integer.valueOf(page), Integer.valueOf(year), new Date());
      books.add(book);
      System.out.println("Generate book: " + book.toString());
      writing();
      return book.getId();
    }
    else {
      System.out.println("Wrong!");
      return null;
    }
  }
  @Override
  public Integer updateBook(int id, String author, String name, String page, String year) {
    System.out.println("Update book with id: " + id);
    boolean[] checker = check(author, name, page, year);
    if (checker[0] && checker[1] && checker[2] && checker[3]) {
      Book book = null;
      for (int i = 0; i < books.size(); ++i) {
        if (books.get(i).getId() == id)
          book = books.get(i);
      }
      if (book != null) {
        book.setAuthor(author);
        book.setName(name);
        book.setPageCount(Integer.valueOf(page));
        book.setYear(Integer.valueOf(year));
      }
      System.out.println("Book changed: " + book.toString());
      writing();
      return book.getId();
    }
    else {
      System.out.println("Check failure");
      return null;
    }
  }
  @Override
  public int booksCount(String sortBy, String author, String name, Integer fromPage, Integer toPage,
                        Integer fromYear, Integer toYear, Long fromDate, Long toDate) {
    System.out.println("Get books count");
    return filtraion(sortBy, author, name, fromPage, toPage, fromYear, toYear, fromDate, toDate).size();
  }
  @Override
  public int getPage(int id, String sortBy, String author, String name, Integer fromPage, Integer toPage,
                     Integer fromYear, Integer toYear, Long fromDate, Long toDate) {
    System.out.println("Get page for book id: " + id);
    List<Book> filtrationBooks = filtraion(sortBy, author, name, fromPage, toPage, fromYear, toYear, fromDate, toDate);
    for (int i = 0; i < filtrationBooks.size(); ++i) {
      if (id == filtrationBooks.get(i).getId())
        return i;
    }
    return -1;
  }

  public String greetServer(String input) throws IllegalArgumentException {
    // Verify that the input is valid.
    if (!FieldVerifier.isValidName(input)) {
      // If the input is not valid, throw an IllegalArgumentException back to
      // the client.
      throw new IllegalArgumentException(
              "Name must be at least 4 characters long");
    }

    String serverInfo = getServletContext().getServerInfo();
    String userAgent = getThreadLocalRequest().getHeader("User-Agent");

    // Escape data from the client to avoid cross-site script vulnerabilities.
    input = escapeHtml(input);
    userAgent = escapeHtml(userAgent);

    return "Hello, " + input + "!<br><br>I am running " + serverInfo
            + ".<br><br>It looks like you are using:<br>" + userAgent;
  }

  /**
   * Escape an html string. Escaping data received from the client helps to
   * prevent cross-site script vulnerabilities.
   *
   * @param html the html string to escape
   * @return the escaped string
   */
  private String escapeHtml(String html) {
    if (html == null) {
      return null;
    }
    return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(
            ">", "&gt;");
  }

  void writing() {
    System.out.println("Writing");
    /*try {
      FileWriter writer = new FileWriter(file);
      JSONArray jsonArray = new JSONArray();
      for (Book book: books) {
        JSONObject jsonBook = new JSONObject();
        jsonBook.put("id", Long.valueOf(book.getId()));
        jsonBook.put("author", book.getAuthor());
        jsonBook.put("name", book.getName());
        jsonBook.put("page", Long.valueOf(book.getPageCount()));
        jsonBook.put("year", Long.valueOf(book.getYear()));
        jsonBook.put("date", Long.valueOf(book.getAddDate().getTime()));
        System.out.println(jsonBook.toJSONString());
        jsonArray.add(jsonBook);
      }
      writer.write(jsonArray.toJSONString());
      writer.close();
    } catch (IOException e) {
      System.out.println("Can't open file for writing");
    }*/
  }
  JSONObject toJSONObject(Book book) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("id", String.valueOf(book.getId()));
    jsonObject.put("author", book.getAuthor());
    jsonObject.put("name", book.getName());
    jsonObject.put("page", String.valueOf(book.getPageCount()));
    jsonObject.put("year", String.valueOf(book.getYear()));
    jsonObject.put("date", String.valueOf(book.getAddDate().getTime()));
    return jsonObject;
  }
  boolean[] check(String author, String name, String page, String year) {
    boolean[] checker = new boolean[4];
    checker[0] = (author != null) && (!author.equals(""));
    checker[1] = (name != null) && (!name.equals(""));
    checker[2] = (page != null) && (!page.equals(""));
    try {
      Integer val = Integer.valueOf(page);
      if (val == null || val <= 0) checker[2] = false;
    }
    catch (NumberFormatException e) {checker[2] = false; }
    checker[3] = (year != null) && (!year.equals(""));
    try {
      Integer val = Integer.valueOf(year);
      if (val == null || val <= 0) checker[3] = false;
    }
    catch (NumberFormatException e) {checker[3] = false; }
    System.out.println(checker[0] + " " + checker[1] + " " + checker[2] + " " + checker[3]);
    return checker;
  }
  int generateID() {
    Random rand = new Random();
    boolean valid = false;
    int newId = 0;
    while (!valid) {
      newId = rand.nextInt(1000000);
      valid = true;
      for(Book book: books) {
        if (book.getId() == newId) valid = false;
      }
    }
    System.out.println(newId);
    return newId;
  }
  List<Book> filtraion(String sortBy, String author, String name, Integer fromPage, Integer toPage,
                       Integer fromYear, Integer toYear, Long fromDate, Long toDate) {
    System.out.println("Filtration:");
    System.out.println("\tSortBy: " + sortBy);
    System.out.println("\tAuthor: " + ((author != null) ? author : "null"));
    System.out.println("\tName: " + ((name != null) ? name : "null"));
    System.out.println("\tPage: from " + ((fromPage != null) ? fromPage : "null") + " to " + ((toPage != null) ? toPage : "null"));
    System.out.println("\tYear: from " + ((fromYear != null) ? fromYear : "null") + " to " + ((toYear != null) ? toYear : "null"));
    System.out.println("\tDate: from " + ((fromDate != null) ? fromDate : "null") + " to " + ((toDate != null) ? toDate : "null"));
    List<Book> filterBooks = new ArrayList<>();
    for(Book book: books) {
      if (author != null && !book.getAuthor().contains(author)) continue;
      if (name != null && !book.getName().contains(name)) continue;
      if (fromPage != null && book.getPageCount() < fromPage) continue;
      if (toPage != null && toPage < book.getPageCount()) continue;
      if (fromYear != null && book.getYear() < fromYear) continue;
      if (toYear != null && toYear < book.getYear()) continue;
      if (fromDate != null && book.getAddDate().getTime() < fromDate) continue;
      if (toDate !=null && toDate < book.getAddDate().getTime()) continue;
      filterBooks.add(book);
    }
    if (sortBy != null) {
      if (sortBy.equals("id")) Collections.sort(filterBooks, new Comparator<Book>() {
        @Override
        public int compare(Book o1, Book o2) {
          return Integer.compare(o1.getId(), o2.getId());
        }
      });
      if (sortBy.equals("author")) Collections.sort(filterBooks, new Comparator<Book>() {
        @Override
        public int compare(Book o1, Book o2) {
          return o1.getAuthor().compareTo(o2.getAuthor());
        }
      });
      if (sortBy.equals("name")) Collections.sort(filterBooks, new Comparator<Book>() {
        @Override
        public int compare(Book o1, Book o2) {
          return o1.getName().compareTo(o2.getName());
        }
      });
      if (sortBy.equals("page")) Collections.sort(filterBooks, new Comparator<Book>() {
        @Override
        public int compare(Book o1, Book o2) {
          return Integer.compare(o1.getPageCount(), o2.getPageCount());
        }
      });
      if (sortBy.equals("year")) Collections.sort(filterBooks, new Comparator<Book>() {
        @Override
        public int compare(Book o1, Book o2) {
          return Integer.compare(o1.getYear(), o2.getYear());
        }
      });
      if (sortBy.equals("date")) Collections.sort(filterBooks, new Comparator<Book>() {
        @Override
        public int compare(Book o1, Book o2) {
          return Long.compare(o1.getAddDate().getTime(), o2.getAddDate().getTime());
        }
      });
    }
    return filterBooks;
  }
}