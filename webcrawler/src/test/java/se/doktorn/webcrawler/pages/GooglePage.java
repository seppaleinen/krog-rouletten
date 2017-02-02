package se.doktorn.webcrawler.pages;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.io.FileUtils;
import se.doktorn.webcrawler.Bar;
import se.doktorn.webcrawler.util.Helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

public class GooglePage extends Helper {
    private static final String GOOGLE_MAPS_API_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=%s&key=%s";
    private static final String API_KEY = System.getenv("MAPS_API_KEY");
    private static final File STATUS_FILE = new File(ClassLoader.getSystemResource(".").getPath(), "statusFile.txt");

    public enum StatusCode {
        OK,
        UNKNOWN_ERROR,
        ZERO_RESULTS,
        OVER_QUERY_LIMIT,
        REQUEST_DENIED,
        INVALID_REQUEST,
        NOT_FOUND
    }

    public List<Bar> refineBarList(List<Bar> barList) {
        for(Bar bar: barList) {
            if(checkIfProcessable(bar)) {
                final String urlEncodedSearchParams = urlEncode(bar.getName(), bar.getAdress());
                if (urlEncodedSearchParams != null) {
                    Request request = new Request.Builder().
                            url(String.format(GOOGLE_MAPS_API_URL, urlEncodedSearchParams, API_KEY)).
                            build();

                    try {
                        Response response = new OkHttpClient().newCall(request).execute();

                        parseJson(response.body().string(), bar);

                    } catch (IOException e) {
                        System.out.println("Call for bar: " + bar + " failed");
                        e.printStackTrace();
                    }
                }
            }
        }

        return barList;
    }

    private void parseJson(String json, Bar bar) {
        JsonElement jsonElement = new JsonParser().parse(json);

        if(checkStatus(jsonElement, bar)) {
            JsonArray results = jsonElement.getAsJsonObject().getAsJsonArray("results");

            results.forEach(result -> parseResult(result, bar));
        }
    }

    /**
     * If bar is not in status file, it means that it hasn't been processed yet and should be
     */
    private boolean checkIfProcessable(Bar bar) {
        boolean bool = false;

        try {
            List<String> lines = FileUtils.readLines(STATUS_FILE, "UTF-8");
            bool = lines.stream().noneMatch(line -> line.contains(bar.getName() + ":" + bar.getAdress()));
        } catch (FileNotFoundException e) {
            System.out.println("No status file created yet. Proceeding");
            bool = true;
        } catch (IOException e) {
            System.out.println("Unable to read from status file");
            e.printStackTrace();
        }

        return bool;
    }

    /**
     * Check status from json response if we're over the query limit
     * (100/24hr for textsearch)
     * (1000/24hr for detailsearch)
     * And if successfully imported writes line in status file
     */
    private boolean checkStatus(JsonElement jsonElement, Bar bar) {
        String status = parseJsonPrimitive(jsonElement.getAsJsonObject().getAsJsonPrimitive("status"));

        if(StatusCode.OK.name().equals(status)) {
            try {
                FileUtils.writeStringToFile(STATUS_FILE, bar.getName() + ":" + bar.getAdress() + "\n", "UTF-8", true);
            } catch (IOException e) {
                System.out.println("Unable to update statusfile with: " + bar.getName() + ":" + bar.getAdress());
                e.printStackTrace();
            }
            return true;
        } else if(StatusCode.OVER_QUERY_LIMIT.name().equals(status)) {
            return false;
        }

        return false;
    }

    private void parseResult(JsonElement result, Bar bar) {
        String adress = parseJsonPrimitive(result.getAsJsonObject().getAsJsonPrimitive("formatted_address"));
        String rating = parseJsonPrimitive(result.getAsJsonObject().getAsJsonPrimitive("rating"));
        String name = parseJsonPrimitive(result.getAsJsonObject().getAsJsonPrimitive("name"));
        String latitude = parseJsonPrimitive(result.getAsJsonObject().getAsJsonObject("geometry").
                getAsJsonObject("location").
                getAsJsonPrimitive("lat"));
        String longitude = parseJsonPrimitive(result.getAsJsonObject().getAsJsonObject("geometry").
                getAsJsonObject("location").
                getAsJsonPrimitive("lng"));

        bar.setName(name);
        bar.setAdress(adress);
        bar.setBetyg(rating);
    }


    private String urlEncode(String namn, String address) {
        try {
            return URLEncoder.encode(namn + " " + address, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private String parseJsonPrimitive(JsonPrimitive jsonPrimitive) {
        return jsonPrimitive != null ? jsonPrimitive.getAsString() : null;
    }
}
