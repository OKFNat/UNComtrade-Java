package at.okfn.uncomtrade;


import java.lang.reflect.Field;
import java.text.NumberFormat;


public class DataRow {

    /**
     * Classification.
     */
    protected String pfCode;

    /**
     * Year.
     */
    protected Integer yr;

    /**
     * Period, if applicable.
     */
    protected Integer period;

    /**
     * Period description, if applicable.
     */
    protected String periodDesc;

    /**
     * Aggregation level.
     */
    protected Integer aggrLevel;

    /**
     * Is Leaf Code.
     */
    protected Integer IsLeaf;

    /**
     * Trade flow code.
     */
    protected Integer rgCode;

    /**
     * Trade flow label.
     */
    protected String rgDesc;

    /**
     * Reporter country code.
     */
    protected Integer rtCode;

    /**
     * Reporter country name.
     */
    protected String rtTitle;

    /**
     * Reporter country 3-character ISO code.
     */
    protected String rt3ISO;

    /**
     * Partner country code.
     */
    protected Integer ptCode;

    /**
     * Partner country name.
     */
    protected String ptTitle;

    /**
     * Partner country 3-character ISO code.
     */
    protected String pt3ISO;

    /**
     * Commodity code.
     */
    protected String cmdCode;

    /**
     * Commodity label.
     */
    protected String cmdDescE;

    /**
     * Quantity unit code.
     */
    protected Integer qtCode;

    /**
     * Quantity unit.
     */
    protected String qtDesc;

    /**
     * Quantity.
     */
    protected Long TradeQuantity;

    /**
     * Net weight (in kg).
     */
    protected String NetWeight;

    /**
     * Trade value (in USD).
     */
    protected Long TradeValue;

    /**
     * Estimations flag.
     */
    protected Integer estCode;

    /**
     * Retrieves a single property by machine name.
     *
     * @param property
     *   The property to retrieve.
     *
     * @return The property value.
     */
    public Object get(String property) {
        try {
            Class<?> c = this.getClass();
            Field field = c.getDeclaredField(property);
            return field.get(this);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return The classification code.
     */
    public String getPfCode() {
        return pfCode;
    }

    /**
     * @return The year of the data.
     */
    public Integer getYr() {
        return yr;
    }

    /**
     * @return The internal representation for the period of the data.
     */
    public Integer getPeriod() {
        return period;
    }

    /**
     * @return A human-readable label for the period of the data.
     */
    public String getPeriodDesc() {
        return periodDesc;
    }

    /**
     * @return The aggregation level.
     */
    public Integer getAggrLevel() {
        return aggrLevel;
    }

    /**
     * @return Whether this data row represents a leaf or not.
     */
    public Integer isLeaf() {
        return IsLeaf;
    }

    /**
     * @return The trade flow code (import, export, etc).
     */
    public Integer getRgCode() {
        return rgCode;
    }

    /**
     * @return The label for the trade flow.
     */
    public String getRgDesc() {
        return rgDesc;
    }

    /**
     * @return The internal code for the reporting area.
     */
    public Integer getRtCode() {
        return rtCode;
    }

    /**
     * @return The human-readable label for the reporting area.
     */
    public String getRtTitle() {
        return rtTitle;
    }

    /**
     * @return The three-character ISO code for the reporting area.
     */
    public String getRt3ISO() {
        return rt3ISO;
    }

    /**
     * @return The internal code for the partner area.
     */
    public Integer getPtCode() {
        return ptCode;
    }

    /**
     * @return The human-readable label for the partner area.
     */
    public String getPtTitle() {
        return ptTitle;
    }

    /**
     * @return The three-character ISO code for the partner area.
     */
    public String getPt3ISO() {
        return pt3ISO;
    }

    /**
     * @return The internal code for the type of commodity being traded.
     */
    public String getCmdCode() {
        return cmdCode;
    }

    /**
     * @return The human-readable label for the type of commodity being traded.
     */
    public String getCmdDescE() {
        return cmdDescE;
    }

    /**
     * @return The quantity code.
     */
    public Integer getQtCode() {
        return qtCode;
    }

    /**
     * @return The quantity description.
     */
    public String getQtDesc() {
        return qtDesc;
    }

    /**
     * @return The trade quantity.
     */
    public Long getTradeQuantity() {
        return TradeQuantity;
    }

    /**
     * @return The net weight of the trade flow (in kg).
     */
    public String getNetWeight() {
        return NetWeight;
    }

    /**
     * @return The total value of the trade flow (in USD).
     */
    public Long getTradeValue() {
        return TradeValue;
    }

    /**
     * @return The estimation code.
     */
    public Integer getEstCode() {
        return estCode;
    }

    /**
     * @return A string representation of this data row.
     */
    @Override
    public String toString() {
        NumberFormat format = NumberFormat.getInstance();
        return String.format("%s of %s (%d) with partner %s (%d) in %s: commodity %s, value USD %s", rgDesc, rtTitle,
                rtCode, ptTitle, ptCode, periodDesc, cmdDescE, format.format(TradeValue));
    }

}
