package library.server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

public class GenerateFile {
    public static void main(String[] args) throws IOException {
        Random rand = new Random();
        JSONArray arr = new JSONArray();
        for (int i = 1; i <=25; ++i) {
            int id = rand.nextInt(1000000);
            String author = "Author"+i;
            String name = "Name" + i;
            int pageCount = 1000*i;
            int year = 2000+i;
            Date date = new Date(i+500, 4, i);
            JSONObject object = new JSONObject();
            object.put("id", id);
            object.put("author", author);
            object.put("name", name);
            object.put("page", pageCount);
            object.put("year", year);
            object.put("date", date.getTime());
            arr.add(object);
        }
        FileWriter writer = new FileWriter("books.json");
        writer.write(arr.toJSONString());
        writer.close();
    }

}
