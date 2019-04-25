package library.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GreetingServiceAsync
{

    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see library.client.GreetingService
     */
    void reading( AsyncCallback<Void> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see library.client.GreetingService
     */
    void getBooks( int from, int to, java.lang.String sortBy, java.lang.String author, java.lang.String name, java.lang.Integer fromPage, java.lang.Integer toPage, java.lang.Integer fromYear, java.lang.Integer toYear, java.lang.Long fromDate, java.lang.Long toDate, AsyncCallback<java.lang.String> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see library.client.GreetingService
     */
    void deleteBook( int id, AsyncCallback<Void> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see library.client.GreetingService
     */
    void addBook( java.lang.String author, java.lang.String name, java.lang.String page, java.lang.String year, AsyncCallback<java.lang.Integer> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see library.client.GreetingService
     */
    void updateBook( int id, java.lang.String author, java.lang.String name, java.lang.String page, java.lang.String year, AsyncCallback<java.lang.Integer> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see library.client.GreetingService
     */
    void booksCount( java.lang.String sortBy, java.lang.String author, java.lang.String name, java.lang.Integer fromPage, java.lang.Integer toPage, java.lang.Integer fromYear, java.lang.Integer toYear, java.lang.Long fromDate, java.lang.Long toDate, AsyncCallback<java.lang.Integer> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see library.client.GreetingService
     */
    void getPage( int id, java.lang.String sortBy, java.lang.String author, java.lang.String name, java.lang.Integer fromPage, java.lang.Integer toPage, java.lang.Integer fromYear, java.lang.Integer toYear, java.lang.Long fromDate, java.lang.Long toDate, AsyncCallback<java.lang.Integer> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see library.client.GreetingService
     */
    void greetServer( java.lang.String name, AsyncCallback<java.lang.String> callback );


    /**
     * Utility class to get the RPC Async interface from client-side code
     */
    public static final class Util 
    { 
        private static GreetingServiceAsync instance;

        public static final GreetingServiceAsync getInstance()
        {
            if ( instance == null )
            {
                instance = (GreetingServiceAsync) GWT.create( GreetingService.class );
            }
            return instance;
        }

        private Util()
        {
            // Utility class should not be instantiated
        }
    }
}
