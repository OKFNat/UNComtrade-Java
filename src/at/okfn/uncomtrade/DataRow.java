package at.okfn.uncomtrade;


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
    protected String TradeQuantity;

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

    public String getPfCode() {
        return pfCode;
    }

    public Integer getYr() {
        return yr;
    }

    public Integer getPeriod() {
        return period;
    }

    public String getPeriodDesc() {
        return periodDesc;
    }

    public Integer getAggrLevel() {
        return aggrLevel;
    }

    public Integer isLeaf() {
        return IsLeaf;
    }

    public Integer getRgCode() {
        return rgCode;
    }

    public String getRgDesc() {
        return rgDesc;
    }

    public Integer getRtCode() {
        return rtCode;
    }

    public String getRtTitle() {
        return rtTitle;
    }

    public String getRt3ISO() {
        return rt3ISO;
    }

    public Integer getPtCode() {
        return ptCode;
    }

    public String getPtTitle() {
        return ptTitle;
    }

    public String getPt3ISO() {
        return pt3ISO;
    }

    public String getCmdCode() {
        return cmdCode;
    }

    public String getCmdDescE() {
        return cmdDescE;
    }

    public Integer getQtCode() {
        return qtCode;
    }

    public String getQtDesc() {
        return qtDesc;
    }

    public String getTradeQuantity() {
        return TradeQuantity;
    }

    public String getNetWeight() {
        return NetWeight;
    }

    public Long getTradeValue() {
        return TradeValue;
    }

    public Integer getEstCode() {
        return estCode;
    }

    @Override
    public String toString() {
        NumberFormat format = NumberFormat.getInstance();
        return String.format("%s of %s (%d) with partner %s (%d) in %s: commodity %s, value USD %s", rgDesc, rtTitle,
                rtCode, ptTitle, ptCode, periodDesc, cmdDescE, format.format(TradeValue));
    }

}
