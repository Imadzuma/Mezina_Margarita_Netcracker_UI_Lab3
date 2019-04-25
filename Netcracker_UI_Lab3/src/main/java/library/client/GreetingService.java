package library.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import library.shared.Book;
import java.util.List;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
  void reading();
  String getBooks(int from, int to, String sortBy, String author, String name, Integer fromPage, Integer toPage,
                  Integer fromYear, Integer toYear, Long fromDate, Long toDate);
  void deleteBook(int id);
  Integer addBook(String author, String name, String page, String year);
  Integer updateBook(int id, String author, String name, String page, String year);
  int booksCount(String sortBy, String author, String name, Integer fromPage, Integer toPage,
                 Integer fromYear, Integer toYear, Long fromDate, Long toDate);
  int getPage(int id, String sortBy, String author, String name, Integer fromPage, Integer toPage,
              Integer fromYear, Integer toYear, Long fromDate, Long toDate);
  String greetServer(String name) throws IllegalArgumentException;
}
