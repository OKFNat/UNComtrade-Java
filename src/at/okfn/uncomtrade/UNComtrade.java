package at.okfn.uncomtrade;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * Provides an object-oriented interface to the UN Comtrade database.
 *
 * Use like this:
 *
 * <pre>
 * {@code
 *   UNComtrade client = new UNComtrade();
 *   client.set
 *
 *
 *
 * }
 * </pre>
 */
public class UNComtrade {

    /**
     * The base URL to use for accessing the database.
     */
    protected String baseUrl = "http://comtrade.un.org/api/get";

    /**
     * The GET parameters to use for the next request.
     */
    protected Map<String, String> params;

    /**
     * The used HTTP client object.
     */
    protected CloseableHttpClient httpClient;

    /**
     * The metadata for all of the requests so far.
     */
    protected Stack<JsonObject> responseMetadata;

    /**
     * Executes this class as a program, for testing or simple use.
     */
    public static void main(String[] args) {
        try {
            NumberFormat format = NumberFormat.getInstance();
            Map<String, String> defaultParams = new HashMap<String, String>();

            if (args.length == 0) {
                args = new String[1];
                args[0] = "-o";
            }

            String verb = null;

            boolean printSeparator = false;
            for (int i = 0; i < args.length; ++i) {
                if (printSeparator) {
                    System.out.println("\n");
                }
                printSeparator = true;

                UNComtrade client = new UNComtrade();
                if (!defaultParams.isEmpty()) {
                    client.setParams(defaultParams);
                }

                switch (args[i]) {
                    case "-o":
                    case "--overview":
                        client.setPartnerArea("0");
                        System.out.println("- Overview of trade flows:");
                        for (DataRow row : client.retrieve()) {
                            System.out.println(row);
                        }
                        break;

                    case "-e":
                    case "--top-exports":
                        verb = "exports to";
                        client.setTradeFlow("2");
                    case "-i":
                    case "--top-imports":
                        if (verb == null) {
                            verb = "imports from";
                            client.setTradeFlow("1");
                        }
                        client.setReporter(args[++i]);
                        client.setPartnerArea("all");
                        int count = 5;
                        if (i + 1 < args.length && args[i + 1].matches("^\\d+$")) {
                            count = Integer.parseInt(args[++i]);
                        }

                        Comparator<DataRow> comp = new Comparator<DataRow>() {

                            @Override
                            public int compare(DataRow o1, DataRow o2) {
                                return -Long.compare(o1.getTradeValue(), o2.getTradeValue());
                            }

                        };

                        SortedSet<DataRow> values = new TreeSet<DataRow>(comp);
                        values.addAll(client.retrieve());

                        int j = 0;
                        for (DataRow row : values) {
                            if (row.getPtCode().equals(0)) {
                                continue;
                            }
                            if (j == 0) {
                                System.out.println("- Listing the top " + count + " areas that " + row.getRtTitle()
                                        + " " + verb + ":");
                            }
                            System.out.println(row.getPtTitle() + " (USD " + format.format(row.getTradeValue()) + ")");
                            if (++j >= count) {
                                break;
                            }
                        }
                        verb = null;
                        Thread.sleep(1000);
                        break;

                    case "-p":
                    case "--period":
                        String period = args[++i];
                        System.out.println("Setting period to " + period + ".");
                        defaultParams.put(Parameters.TIME_PERIOD, period);
                        if (period.matches("(^|,)\\d{6}(,|$)")) {
                            defaultParams.put(Parameters.DATA_FREQUENCY, "M");
                        }
                        else {
                            defaultParams.put(Parameters.DATA_FREQUENCY, "A");
                        }
                }
            }
        }
        catch (ClientProtocolException e) {
            e.printStackTrace(System.err);
        }
        catch (IOException e) {
            e.printStackTrace(System.err);
        }
        catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Creates a new UN Comtrade client object.
     */
    public UNComtrade() {
        httpClient = HttpClients.createDefault();
        responseMetadata = new Stack<JsonObject>();

        params = new HashMap<String, String>();
        params.put("fmt", "json");
        params.put(Parameters.REPORTER, "all");
        params.put(Parameters.DATA_FREQUENCY, "A");
        params.put(Parameters.TIME_PERIOD, "now");
        params.put(Parameters.CLASSIFICATION, "HS");
        params.put(Parameters.PARTNER_AREA, "all");
        params.put(Parameters.TRADE_FLOW, "all");
        params.put(Parameters.CLASSIFICATION_CODE, "TOTAL");
        params.put(Parameters.LIMIT, "50000");
        params.put(Parameters.TRADE_DATA_TYPE, "C");
        params.put("head", "H");
    }

    /**
     * Creates a new UN Comtrade client object.
     *
     * @param baseUrl
     *            The base URL to use, instead of the default.
     */
    public UNComtrade(String baseUrl) {
        this();
        this.baseUrl = baseUrl;
    }

    /**
     * Retrieves the data set with the current settings.
     *
     * @return The requested data set.
     */
    public DataSet retrieve() throws IOException, ClientProtocolException {
        ResponseHandler<DataSet> rh = new ResponseHandler<DataSet>() {

            @Override
            public DataSet handleResponse(final HttpResponse response) throws IOException {
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
                UNComtrade.this.responseMetadata.push(responseObject.get("validation").getAsJsonObject());
                Gson gson = new GsonBuilder().create();
                return gson.fromJson(responseObject.get("dataset"), DataSet.class);
            }

        };

        return httpClient.execute(new HttpGet(getApiUrl()), rh);
    }

    /**
     * @return The API URL that corresponds to the current settings on this
     *         client.
     */
    public String getApiUrl() {
        StringBuilder url = new StringBuilder(baseUrl);
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            url.append(first ? '?' : '&');
            first = false;
            url.append(entry.getKey());
            url.append('=');
            try {
                url.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.name()));
            }
            catch (UnsupportedEncodingException e) {
                assert (false);
            }
        }
        return url.toString();
    }

    /**
     * Sets a parameter value for the API call.
     *
     * @param name
     *            The name of the parameter.
     * @param value
     *            The new value of the parameter. Most parameters accept
     *            comma-separated lists of values as well.
     */
    public void setParam(String name, String value) {
        params.put(name, value);
    }

    /**
     * Sets several parameters at once.
     *
     * @param params
     *            The parameters to set.
     */
    public void setParams(Map<String, String> params) {
        this.params.putAll(params);
    }

    /**
     * Finalizes this object before destruction.
     */
    @Override
    public void finalize() throws Throwable {
        httpClient.close();
    }

    /**
     * Sets the reporter.
     *
     * @param reporter
     *            The internal code for a country, territory or area, or "all"
     *            to list all reporters. Defaults to "all".
     */
    public void setReporter(String reporter) {
        try {
            if (reporter.equals("all") || Integer.parseInt(reporter) >= 0) {
                params.put(Parameters.REPORTER, reporter);
                return;
            }
        }
        catch (NumberFormatException e) {}
        throw new IllegalArgumentException("Only \"all\" or numeric area codes are allowed for the reporter area.");
    }

    /**
     * Sets the data frequency.
     *
     * @param dataFrequency
     *            "A" for "annual" or "M" for "monthly". Defaults to "A".
     */
    public void setDataFrequency(String dataFrequency) {
        params.put(Parameters.DATA_FREQUENCY, dataFrequency);
    }

    /**
     * Sets the time period.
     *
     * @param timePeriod
     *            Depending on the data frequency, either a year (YYYY) or a
     *            year and month (YYYYMM). Additionally, "now" and "recent" are
     *            valid. Defaults to "now".
     */
    public void setTimePeriod(String timePeriod) {
        params.put(Parameters.TIME_PERIOD, timePeriod);
    }

    /**
     * Sets the classification.
     *
     * @param classification
     *            The trade data classification scheme to use. Defaults to "HS",
     *            meaning "Harmonized System (HS), as reported".
     */
    public void setClassification(String classification) {
        params.put(Parameters.CLASSIFICATION, classification);
    }

    /**
     * Sets the partner area.
     *
     * @param partnerArea
     *            The internal code for a country, territory or area (0 being
     *            the whole world), or "all" to list all reporters. Defaults to
     *            "all".
     */
    public void setPartnerArea(String partnerArea) {
        try {
            if (partnerArea.equals("all") || Integer.parseInt(partnerArea) >= 0) {
                params.put(Parameters.PARTNER_AREA, partnerArea);
                return;
            }
        }
        catch (NumberFormatException e) {}
        throw new IllegalArgumentException("Only \"all\" or numeric area codes are allowed for the partner area.");
    }

    /**
     * Sets the trade flow.
     *
     * @param tradeFlow
     *            1 for imports, 2 for exports, 3/4 for re-export/-import or
     *            "all". Defaults to "all".
     */
    public void setTradeFlow(String tradeFlow) {
        try {
            if (tradeFlow.equals("all") || Integer.parseInt(tradeFlow) > 0) {
                params.put(Parameters.TRADE_FLOW, tradeFlow);
                return;
            }
        }
        catch (NumberFormatException e) {}
        throw new IllegalArgumentException(
                "Only \"all\" or positive integers are allowed for the trade flow parameter.");
    }

    /**
     * Sets the classification code.
     *
     * @param classificationCode
     *            The classification codes (depending on the classification
     *            scheme used) to list. Defaults to "TOTAL".
     */
    public void setClassificationCode(String classificationCode) {
        params.put(Parameters.CLASSIFICATION_CODE, classificationCode);
    }

    /**
     * Sets the limit.
     *
     * @param limit
     *            The maximum number of results to return. Defaults to 50,000,
     *            which is the maximum allowed by the API.
     */
    public void setLimit(int limit) {
        if (limit > 0 && limit <= 50000) {
            params.put(Parameters.LIMIT, "" + limit);
        }
        else {
            throw new IllegalArgumentException("Limit has to be between 0 and 50000.");
        }
    }

    /**
     * Sets the trade data type.
     *
     * @param tradeDataType
     *            "C" for commodities (default) or "S" for futures (not in use
     *            yet).
     */
    public void setTradeDataType(String tradeDataType) {
        if (tradeDataType.equals("C") || tradeDataType.equals("S")) {
            params.put(Parameters.TRADE_DATA_TYPE, tradeDataType);
        }
        else {
            throw new IllegalArgumentException("Only \"C\" or \"S\" are allowed as trade data types.");
        }
    }

}
