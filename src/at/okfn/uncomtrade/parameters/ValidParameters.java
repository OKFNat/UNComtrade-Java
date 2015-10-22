package at.okfn.uncomtrade.parameters;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import at.okfn.uncomtrade.Parameters;


/**
 * Base class for classes providing information about possible parameter values.
 */
public abstract class ValidParameters {

    /**
     * Creates a new ValidParameters instance for the given parameter.
     *
     * @param parameter
     *            The parameter for which to return valid values.
     *
     * @return An object encapsulating the valid values for the given parameter.
     *
     * @throws IllegalArgumentException
     *             If the parameter is unknown, or has no known list of valid
     *             values.
     */
    public static ValidParameters create(String parameter) {
        switch (parameter) {
            case Parameters.REPORTER:
                return new ReporterValidParameters();

            default:
                throw new IllegalArgumentException("No known parameter: " + parameter);
        }
    }

    /**
     * Retrieve valid parameter values for this parameter.
     *
     * @return The valid parameter values mapped to their labels.
     *
     * @throws IOException
     *             If any I/O error occurred.
     */
    public Map<String, String> getValues() throws IOException {
        return new TreeMap<String, String>();
    }

    /**
     * Retrieve valid parameter values matching the given string.
     *
     * @param input
     *            The user input which should be matched against the possible
     *            parameter values (both values and their labels).
     *
     * @return The valid parameter values mapped to their labels.
     *
     * @throws IOException
     *             If any I/O error occurred.
     */
    public Map<String, String> getValues(String input) throws IOException {
        input = input.toLowerCase();
        Map<String, String> values = getValues();
        Iterator<Entry<String, String>> it = values.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> element = it.next();
            if (element.getKey().toLowerCase().indexOf(input) < 0
                    && element.getValue().toLowerCase().indexOf(input) < 0) {
                it.remove();
            }
        }
        return values;
    }

    /**
     * Retrieves a map of valid parameter values from a remote JSON resource.
     *
     * @param url
     *            The URL from which to retrieve the JSON.
     *
     * @return The valid parameter values mapped to their labels.
     *
     * @throws IOException
     *             If any I/O error occurred.
     */
    protected static Map<String, String> readFromJson(String url) throws IOException {
        ResponseHandler<Map<String, String>> rh = new ResponseHandler<Map<String, String>>() {

            @Override
            public Map<String, String> handleResponse(final HttpResponse response) throws IOException {
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() >= 300) {
                    throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
                }
                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    throw new ClientProtocolException("Response contains no content");
                }
                ContentType contentType = ContentType.getOrDefault(entity);
                Charset charset = contentType.getCharset();
                if (charset == null) {
                    charset = StandardCharsets.UTF_8;
                }
                Reader reader = new InputStreamReader(entity.getContent(), charset);
                JsonParser parser = new JsonParser();
                JsonObject responseObject = parser.parse(reader).getAsJsonObject();

                Map<String, String> results = new HashMap<String, String>();
                for (JsonElement element : responseObject.get("results").getAsJsonArray()) {
                    String id = element.getAsJsonObject().get("id").getAsString();
                    String label = element.getAsJsonObject().get("text").getAsString();
                    results.put(id, label);
                }
                return results;
            }

        };

        CloseableHttpClient httpClient = HttpClients.createDefault();
        return httpClient.execute(new HttpGet(url), rh);
    }

}
