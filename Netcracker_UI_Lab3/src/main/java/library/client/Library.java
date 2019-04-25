package library.client;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.web.bindery.event.shared.UmbrellaException;
import library.shared.Book;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Library implements EntryPoint {
  private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
  Button prevButton;
  Label pageLabel;
  Button nextButton;
  List<Book> booksView;
  CellTable<Book> booksTable;
  Book selectedBook = null;
  Book updateBook = null;
  int fromElem = 0, toElem = 10;

  class Filter {
    String sortBy = "id";
    String author = null;
    String name = null;
    Integer fromPage = null;
    Integer toPage = null;
    Integer fromYear = null;
    Integer toYear = null;
    Long fromDate = null;
    Long toDate = null;
    void clear() {
      sortBy = "id";
      author = null;
      name = null;
      fromPage = null;
      toPage = null;
      fromYear = null;
      toYear = null;
      fromDate = null;
      toDate = null;
    }
    boolean isValid(String author, String name, int page, int year, long date) {
      if (this.author != null && !author.contains(this.author)) return false;
      if (this.name != null && !name.contains(this.name)) return false;
      if (this.fromPage != null && page < this.fromPage) return false;
      if (this.toPage != null && this.toPage < page) return false;
      if (this.fromYear != null && year < this.fromYear) return false;
      if (this.toYear != null && this.toYear < year) return false;
      if (this.fromDate != null && date < this.fromDate) return false;
      if (this.toDate !=null && this.toDate < date) return false;
      return true;
    }
  }

  TextBox authorField;
  TextBox nameField;
  TextBox pageField;
  TextBox yearField;
  DialogBox dialog;
  DialogBox filterDialog;
  DateBox fromDateFilter;
  DateBox toDateFilter;

  ListBox sortByField;
  TextBox authorFilter;
  TextBox nameFilter;
  TextBox fromPageFilter, toPageFilter;
  TextBox fromYearFilter, toYearFilter;
  Filter filter = new Filter();

  // OnLoad function
  public void onModuleLoad() {
    greetingService.reading(new AsyncCallback<Void>() {
      @Override
      public void onFailure(Throwable throwable) {
        Window.alert("Error in reading");
      }
      @Override
      public void onSuccess(Void aVoid) {
        createElements();
      }
    });
  }

  // create elements
  void createElements() {
    // Create main panel
    FlowPanel mainPanel = createFlowPanel("gwt-main-panel", RootPanel.get("gwtContainer"));
    createLabel("My Library", "gwt-main-header", mainPanel);
    createButton("Filter & Sort", "gwt-page-button", new FilterButtonHandler(), mainPanel);

    // Create table
    booksTable = new CellTable<Book>();
    booksTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
    TextColumn<Book> idColumn = new TextColumn<Book>() {
      @Override
      public String getValue(Book book) {
        return String.valueOf(book.getId());
      }
    };
    TextColumn<Book> authorColumn = new TextColumn<Book>() {
      @Override
      public String getValue(Book book) {
        return book.getAuthor();
      }
    };
    TextColumn<Book> nameColumn = new TextColumn<Book>() {
      @Override
      public String getValue(Book book) {
        return book.getName();
      }
    };
    TextColumn<Book> pageColumn = new TextColumn<Book>() {
      @Override
      public String getValue(Book book) {
        return String.valueOf(book.getPageCount());
      }
    };
    TextColumn<Book> yearColumn = new TextColumn<Book>() {
      @Override
      public String getValue(Book book) {
        return String.valueOf(book.getYear());
      }
    };
    Column<Book, Date> dateColumn = new Column<Book, Date>(new DateCell()) {
      @Override
      public Date getValue(Book book) {
        return book.getAddDate();
      }
    };
    booksTable.addColumn(idColumn, "ID");
    booksTable.addColumn(authorColumn, "Author");
    booksTable.addColumn(nameColumn, "Name");
    booksTable.addColumn(pageColumn, "Page Count");
    booksTable.addColumn(yearColumn, "Writing Year");
    booksTable.addColumn(dateColumn, "Creating Date");
    booksTable.addStyleName("gwt-table");
    final SingleSelectionModel<Book> selectionModel = new SingleSelectionModel<Book>();
    booksTable.setSelectionModel(selectionModel);
    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
      @Override
      public void onSelectionChange(SelectionChangeEvent event) {
        selectedBook = selectionModel.getSelectedObject();
      }
    });
    mainPanel.add(booksTable);

    // Create top buttons panel
    HorizontalPanel topPanel = createHorizontalPanel("gwt-table-panel", mainPanel);
    FlowPanel flowPrevPanel = createFlowPanel("gwt-button-panel", topPanel);
    prevButton = createButton("Previous", "gwt-page-button", new PrevButtonHandler(), flowPrevPanel);
    pageLabel = createLabel("", "gwt-page-info", topPanel);
    FlowPanel flowNextPanel = createFlowPanel("gwt-button-panel", topPanel);
    nextButton = createButton("Next", "gwt-page-button", new NextButtonHandler(), flowNextPanel);

    // Create bottom buttons label
    HorizontalPanel bottomPanel = createHorizontalPanel("gwt-table-panel", mainPanel);
    createButton("Add", "gwt-page-button", new NewButtonHandler(), bottomPanel);
    createButton("Update", "gwt-page-button", new UpdateButtonHandler(), bottomPanel);
    createButton("Delete", "gwt-page-button", new DeleteButtonHandler(), bottomPanel);

    // Create update dialog
    dialog =createDialog("Enter information", "gwt-dialog");
    FlowPanel dialogPanel = createFlowPanel(null, dialog);
    HorizontalPanel authorPanel = createHorizontalPanel("gwt-table-panel", dialogPanel);
    createLabel("Author: ", "gwt-dialog-label", authorPanel);
    authorField = createUnEmptyTextBox("gwt-dialog-field", authorPanel);
    HorizontalPanel namePanel = createHorizontalPanel("gwt-table-panel", dialogPanel);
    createLabel("Name: ","gwt-dialog-label", namePanel);
    nameField = createUnEmptyTextBox("gwt-dialog-field", namePanel);
    HorizontalPanel pagePanel = createHorizontalPanel("gwt-table-panel", dialogPanel);
    createLabel("Page count: ","gwt-dialog-label", pagePanel);
    pageField = createUnNegativeNumberBox("gwt-dialog-field", pagePanel);
    HorizontalPanel yearPanel = createHorizontalPanel("gwt-table-panel", dialogPanel);
    createLabel("Year: ","gwt-dialog-label", yearPanel);
    yearField = createUnNegativeNumberBox("gwt-dialog-field", yearPanel);
    HorizontalPanel buttonsPanel = createHorizontalPanel("gwt-table-panel", dialogPanel);
    createButton("Okey","gwt-dialog-button", new OkeyButtonHandler(), buttonsPanel);
    createButton("Back","gwt-dialog-button", new BackButtonHandler(), buttonsPanel);

    // Create filter dialog
    filterDialog = createDialog("Filter & Sort", "gwt-dialog");
    FlowPanel filterPanel = createFlowPanel(null, filterDialog);
    HorizontalPanel sortPanel = createHorizontalPanel("gwt-table-panel", filterPanel);
    createLabel("Sort by: ", "gwt-dialog-label", sortPanel);
    sortByField = new ListBox();
    sortByField.addItem("id");
    sortByField.addItem("author");
    sortByField.addItem("name");
    sortByField.addItem("page");
    sortByField.addItem("year");
    sortByField.addItem("date");
    sortByField.addStyleName("gwt-dialog-field");
    sortByField.setVisibleItemCount(1);
    sortPanel.add(sortByField);
    HorizontalPanel authorFilterPanel = createHorizontalPanel("gwt-table-panel", filterPanel);
    createLabel("Author: ", "gwt-dialog-label", authorFilterPanel);
    authorFilter = createTextBox("gwt-dialog-field", authorFilterPanel);
    HorizontalPanel nameFilterPanel = createHorizontalPanel("gwt-table-panel", filterPanel);
    createLabel("Name: ", "gwt-dialog-label", nameFilterPanel);
    nameFilter = createTextBox("gwt-dialog-field", nameFilterPanel);
    HorizontalPanel pageFilterPanel = createHorizontalPanel("gwt-table-panel", filterPanel);
    createLabel("Page count: ", "gwt-dialog-label", pageFilterPanel);
    createLabel(" from ", "gwt-dialog-label-mini", pageFilterPanel);
    fromPageFilter = createNumberBox("gwt-dialog-field-mini", pageFilterPanel);
    createLabel(" to ", "gwt-dialog-label-mini", pageFilterPanel);
    toPageFilter = createNumberBox("gwt-dialog-field-mini", pageFilterPanel);
    HorizontalPanel yearFilterPanel = createHorizontalPanel("gwt-table-panel", filterPanel);
    createLabel("Year: ", "gwt-dialog-label", yearFilterPanel);
    createLabel(" from ", "gwt-dialog-label-mini", yearFilterPanel);
    fromYearFilter = createNumberBox("gwt-dialog-field-mini", yearFilterPanel);
    createLabel(" to ", "gwt-dialog-label-mini", yearFilterPanel);
    toYearFilter = createNumberBox("gwt-dialog-field-mini", yearFilterPanel);
    HorizontalPanel dateFilterPanel = createHorizontalPanel("gwt-table-panel", filterPanel);
    createLabel("Creating date: ", "gwt-dialog-label", dateFilterPanel);
    createLabel(" from ", "gwt-dialog-label-mini", dateFilterPanel);
    fromDateFilter = createDateBox("gwt-dialog-field-mini", dateFilterPanel);
    createLabel(" to ", "gwt-dialog-label-mini", dateFilterPanel);
    toDateFilter = createDateBox("gwt-dialog-field-mini", dateFilterPanel);
    HorizontalPanel buttonsFilterPanel = createHorizontalPanel("gwt-table-panel", filterPanel);
    createButton("Change", "gwt-dialog-button", new ChangeFilterButtonHandler(), buttonsFilterPanel);
    createButton("Back", "gwt-dialog-button", new BackFilterButtonHandler(), buttonsFilterPanel);

    // Get info
    getView(0, 10, null);
  }

  // Create some elements
  FlowPanel createFlowPanel(String style, Panel parent) {
    FlowPanel flowPanel = new FlowPanel();
    if (style != null) flowPanel.addStyleName(style);
    parent.add(flowPanel);
    return flowPanel;
  }
  HorizontalPanel createHorizontalPanel(String style, Panel parent) {
    HorizontalPanel horizontalPanel = new HorizontalPanel();
    horizontalPanel.addStyleName(style);
    parent.add(horizontalPanel);
    return horizontalPanel;
  }
  Label createLabel(String name, String style, Panel parent) {
    Label label = new Label(name);
    label.addStyleName(style);
    parent.add(label);
    return label;
  }
  Button createButton(String name, String style, ClickHandler clickHandler, Panel parent) {
    Button button = new Button(name);
    button.addStyleName(style);
    button.addClickHandler(clickHandler);
    parent.add(button);
    return  button;
  }
  DialogBox createDialog(String name, String style) {
    DialogBox dialogBox = new DialogBox();
    dialogBox.setText(name);
    dialogBox.addStyleName(style);
    dialogBox.setAnimationEnabled(true);
    return dialogBox;
  }
  TextBox createTextBox(String style, Panel parent) {
    TextBox textBox = new TextBox();
    textBox.addStyleName(style);
    parent.add(textBox);
    return textBox;
  }
  DateBox createDateBox(String style, Panel parent) {
    DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd.mm.yyyy");
    DateBox dateBox = new DateBox();
    dateBox.setFormat(new DateBox.DefaultFormat(dateFormat));
    dateBox.addStyleName(style);
    parent.add(dateBox);
    return dateBox;
  }
  TextBox createNumberBox(String style, Panel parent) {
    TextBox textBox = new TextBox();
    textBox.addStyleName(style);
    textBox.addValueChangeHandler(new ValueChangeHandler<String>() {
      @Override
      public void onValueChange(ValueChangeEvent<String> valueChangeEvent) {
        if (checkForInt(valueChangeEvent.getValue())) textBox.removeStyleName("gwt-dialog-field-wrong");
        else textBox.addStyleName("gwt-dialog-field-wrong");
      }
    });
    parent.add(textBox);
    return textBox;
  }
  TextBox createUnEmptyTextBox(String style, Panel parent) {
    TextBox textBox = new TextBox();
    textBox.addStyleName(style);
    textBox.addValueChangeHandler(new ValueChangeHandler<String>() {
      @Override
      public void onValueChange(ValueChangeEvent<String> valueChangeEvent) {
        if (!valueChangeEvent.getValue().equals("")) textBox.removeStyleName("gwt-dialog-field-wrong");
        else textBox.addStyleName("gwt-dialog-field-wrong");
      }
    });
    parent.add(textBox);
    return textBox;
  }
  TextBox createUnNegativeNumberBox(String style, Panel parent) {
    TextBox textBox = new TextBox();
    textBox.addStyleName(style);
    textBox.addValueChangeHandler(new ValueChangeHandler<String>() {
      @Override
      public void onValueChange(ValueChangeEvent<String> valueChangeEvent) {
        if (valueChangeEvent.getValue().equals("")) textBox.addStyleName("gwt-dialog-field-wrong");
        else if (!checkForInt(valueChangeEvent.getValue())) textBox.addStyleName("gwt-dialog-field-wrong");
        else if (Integer.valueOf(valueChangeEvent.getValue())< 0) textBox.addStyleName("gwt-dialog-field-wrong");
        else textBox.removeStyleName("gwt-dialog-field-wrong");
      }
    });
    parent.add(textBox);
    return textBox;
  }

  // Dialog
  void openDialog(Book book) {
    if (book == null) {
      authorField.setText("");
      nameField.setText("");
      pageField.setText("");
      yearField.setText("");
    }
    else {
      authorField.setText(book.getAuthor());
      nameField.setText(book.getName());
      pageField.setText(String.valueOf(book.getPageCount()));
      yearField.setText(String.valueOf(book.getYear()));
    }
    updateBook = book;
    dialog.show();
  }
  void openFilter() {
      if (filter.sortBy.equals("id")) sortByField.setSelectedIndex(0);
      if (filter.sortBy.equals("author")) sortByField.setSelectedIndex(1);
      if (filter.sortBy.equals("name")) sortByField.setSelectedIndex(2);
      if (filter.sortBy.equals("page")) sortByField.setSelectedIndex(3);
      if (filter.sortBy.equals("year")) sortByField.setSelectedIndex(4);
      if (filter.sortBy.equals("date")) sortByField.setSelectedIndex(5);
      authorFilter.setText(filter.author);
      nameFilter.setText(filter.name);
      fromPageFilter.setText((filter.fromPage==null) ? null : String.valueOf(filter.fromPage));
      toPageFilter.setText((filter.toPage==null) ? null : String.valueOf(filter.toPage));
      fromYearFilter.setText((filter.fromYear==null) ? null : String.valueOf(filter.fromYear));
      toYearFilter.setText((filter.toYear==null) ? null : String.valueOf(filter.toYear));
      fromDateFilter.setValue((filter.fromDate==null) ? null : new Date(filter.fromDate));
      toDateFilter.setValue((filter.toDate==null) ? null : new Date(filter.toDate));
      filterDialog.show();
  }

  // Buttons functions
  class NewButtonHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent clickEvent) {
      openDialog(null);
    }
  }
  class UpdateButtonHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent clickEvent) {
      if (selectedBook!=null)
        openDialog(selectedBook);
    }
  }
  class DeleteButtonHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent clickEvent) {
      if (selectedBook != null) {
        greetingService.deleteBook(selectedBook.getId(), new AsyncCallback<Void>() {
          @Override
          public void onFailure(Throwable throwable) {
            Window.alert("Can't delete book");
          }
          @Override
          public void onSuccess(Void aVoid) {
              if(booksView.size() == 1 && fromElem != 0) {
                  fromElem -= 10;
                  toElem -= 10;
              }
            getView(fromElem, toElem, null);
          }
        });
      }
    }
  }
  class PrevButtonHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent clickEvent) {
      getView(fromElem - 10, toElem - 10, null);
    }
  }
  class NextButtonHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent clickEvent) {
      getView(fromElem + 10, toElem + 10, null);
    }
  }
  class OkeyButtonHandler implements  ClickHandler {
    @Override
    public void onClick(ClickEvent clickEvent) {
      String author = authorField.getText();
      String name = nameField.getText();
      String page = pageField.getText();
      String year = yearField.getText();
      if (updateBook == null) {
        greetingService.addBook(author, name, page, year, new AsyncCallback<Integer>() {
          @Override
          public void onFailure(Throwable throwable) {Window.alert("Can't add book");}
          @Override
          public void onSuccess(Integer id) {
            if (id != null) {
              dialog.hide();
              getBook(id, author, name, Integer.valueOf(page), Integer.valueOf(year), new Date().getTime());
            }
          }
        });
      }
      else {
        greetingService.updateBook(updateBook.getId(), author, name, page, year, new AsyncCallback<Integer>() {
          @Override
          public void onFailure(Throwable throwable) { Window.alert("Can't update book");}
          @Override
          public void onSuccess(Integer id) {
            if (id != null) {
              dialog.hide();
              getBook(id, author, name, Integer.valueOf(page), Integer.valueOf(year), new Date().getTime());
            }
          }
        });
      }
    }
  }
  class BackButtonHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent clickEvent) {
      dialog.hide();
    }
  }
  class FilterButtonHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent clickEvent) {
      openFilter();
    }
  }
  class ChangeFilterButtonHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent clickEvent) {

      if (checkForInt(fromPageFilter.getText()) && checkForInt(toPageFilter.getText()) &&
          checkForInt(fromYearFilter.getText()) && checkForInt(toYearFilter.getText()) &&
          checkForDate(fromDateFilter.getTextBox().getText()) &&
          checkForDate(toDateFilter.getTextBox().getText())) {
        filter.sortBy = sortByField.getSelectedItemText();
        filter.author = !authorFilter.getText().equals("") ? authorFilter.getText() : null;
        filter.name = !nameFilter.getText().equals("") ? nameFilter.getText() : null;
        filter.fromPage = !fromPageFilter.getText().equals("") ? Integer.valueOf(fromPageFilter.getText()) : null;
        filter.toPage = !toPageFilter.getText().equals("") ? Integer.valueOf(toPageFilter.getText()) : null;
        filter.fromYear = !fromYearFilter.getText().equals("") ? Integer.valueOf(fromYearFilter.getText()) : null;
        filter.toYear = !toYearFilter.getText().equals("") ? Integer.valueOf(toYearFilter.getText()) : null;
        filter.fromDate = !fromDateFilter.getTextBox().getText().equals("") ? fromDateFilter.getValue().getTime() : null;
        filter.toDate = !toDateFilter.getTextBox().getText().equals("") ? toDateFilter.getValue().getTime() : null;
        filterDialog.hide();
        getView(0, 10, null);
      }
    }
  }
  class BackFilterButtonHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent clickEvent) {
      filterDialog.hide();
    }
  }

  // Request
  void getBook(int id, String author, String name, int page, int year, long date) {
    if (!filter.isValid(author, name, page, year, date)) filter.clear();
    greetingService.getPage(id, filter.sortBy, filter.author, filter.name,
            filter.fromPage, filter.toPage, filter.fromYear, filter.toYear, filter.fromDate,
            filter.toDate, new AsyncCallback<Integer>() {
              @Override
              public void onFailure(Throwable throwable) {Window.alert("Can't find book");}
              @Override
              public void onSuccess(Integer page) {
                int newFrom = (page/10) * 10;
                int newTo = newFrom + 10;
                getView(newFrom, newTo, page);
              }
            });
  }
  void getView(int from, int to, Integer page) {
    greetingService.getBooks(from, to, filter.sortBy, filter.author, filter.name, filter.fromPage,
            filter.toPage, filter.fromYear, filter.toYear, filter.fromDate, filter.toDate, new AsyncCallback<String>(){
      @Override
      public void onFailure(Throwable throwable) {
        Window.alert("Error getting from " + from + " to " + to);
      }
      @Override
      public void onSuccess(String json) {
        JSONArray jsonArray = (JSONArray)JSONParser.parse(json);
        booksView = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); ++i)
          booksView.add(getBookFromJSON(jsonArray.get(i).isObject()));
        booksTable.setRowData(booksView);
        replaceView(from, to);
        if (page != null)
          booksTable.getSelectionModel().setSelected(booksView.get(page - from), true);
      }
    });
  }

  // Checkers & parsing
  Book getBookFromJSON(JSONObject jsonBook) {
    int id = Integer.valueOf(jsonBook.get("id").isString().stringValue());
    String author = jsonBook.get("author").isString().stringValue();
    String name = jsonBook.get("name").isString().stringValue();
    int pageCount = Integer.valueOf(jsonBook.get("page").isString().stringValue());
    int year = Integer.valueOf(jsonBook.get("year").isString().stringValue());
    long timeDate = Long.valueOf(jsonBook.get("date").isString().stringValue());
    Book book = new Book(id, author, name, pageCount, year, new Date(timeDate));
    return book;
  }
  void replaceView(int from, int to) {
    fromElem = from;
    toElem = to;
    greetingService.booksCount(filter.sortBy, filter.author, filter.name, filter.fromPage, filter.toPage,
            filter.fromYear, filter.toYear, filter.fromDate, filter.toDate, new AsyncCallback<Integer>() {
      @Override
      public void onFailure(Throwable throwable) {
        Window.alert("Can't get elements count");
      }
      @Override
      public void onSuccess(Integer count) {
        pageLabel.setText((fromElem + 1) + " - " + (booksView.size() < toElem - fromElem ? booksView.size() + fromElem  : toElem)
                + " from " + count);
        if (fromElem == 0) prevButton.setVisible(false);
        else prevButton.setVisible(true);
        if (booksView.size() < toElem - fromElem) nextButton.setVisible(false);
        else nextButton.setVisible(true);
      }
    });
  }
  boolean checkForInt(String str) {
    for(int i = 0; i < str.length(); ++i) {
      if (i == 0 && str.charAt(i) == '-') continue;
      if (str.charAt(i) < '0' || '9' < str.charAt(i))
        return false;
    }
    return true;
  }
  boolean checkForDate(String str) {
    if (str.equals("")) return true;
    int pointCount = 0;
    for (int i = 0; i < str.length(); ++i) {
      if (str.charAt(i) == '.') pointCount++;
      else if (str.charAt(i) < '0' || '9' < str.charAt(i)) return false;
    }
    if (pointCount != 2) return false;
    return true;
  }
}
