package at.okfn.uncomtrade.parameters;


import java.io.IOException;
import java.util.Map;


/**
 * Exposes the valid values for the reporter ("r") parameter.
 */
public class ReporterValidParameters extends ValidParameters {

    @Override
    public Map<String, String> getValues() throws IOException {
        return readFromJson("http://comtrade.un.org/data/cache/reporterAreas.json");
    }

}
