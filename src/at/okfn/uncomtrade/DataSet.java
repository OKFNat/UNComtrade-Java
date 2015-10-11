package at.okfn.uncomtrade;


import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Represents a result data set from the UN Comtrade database.
 */
public class DataSet extends LinkedList<DataRow> {

    /**
     * Removes all data rows with "World" as the trading partner from this set.
     *
     * @return The called object, for easier method chaining.
     */
    public DataSet removeWorld() {
        Iterator<DataRow> it = this.iterator();
        while (it.hasNext()) {
            DataRow row = it.next();
            if (row.getPtCode().equals(0)) {
                it.remove();
            }
        }
        return this;
    }

    /**
     * @param property
     *            The name of a numeric property.
     *
     * @return The sum of all the property's values in this data set.
     */
    public Long getSum(String property) {
        Map<String, Long> sums = getSum(property, new String[0]);
        return sums.isEmpty() ? 0 : sums.get("");
    }

    /**
     * @param property
     *            The name of a numeric property.
     * @param groupBy
     *            The properties to group by when aggregating.
     *
     * @return The sum of all the property's values in this data set, grouped by
     *         the given properties. The keys in the map are comma-separated
     *         property values from all the properties which the results is
     *         grouped by, the map values are the respective sums for the group.
     */
    public Map<String, Long> getSum(String property, String[] groupBy) {
        return getSumOrAvg(property, groupBy, false);
    }

    /**
     * @param property
     *            The name of a numeric property.
     *
     * @return The average of all the property's values in this data set.
     */
    public Long getAvg(String property) {
        Map<String, Long> avgs = getSum(property, new String[0]);
        return avgs.isEmpty() ? 0 : avgs.get("");
    }

    /**
     * @param property
     *            The name of a numeric property.
     * @param groupBy
     *            The properties to group by when aggregating.
     *
     * @return The average of all the property's values in this data set,
     *         grouped by the given properties. The keys in the map are
     *         comma-separated property values from all the properties which the
     *         results is grouped by, the map values are the respective averages
     *         for the group.
     */
    public Map<String, Long> getAvg(String property, String[] groupBy) {
        return getSumOrAvg(property, groupBy, true);
    }

    /**
     * @param property
     *            The name of a numeric property.
     * @param groupBy
     *            The properties to group by when aggregating.
     * @param avg
     *            Whether to compute the average (true) or the sum (false).
     *
     * @return The sum or average of all the property's values in this data set,
     *         grouped by the given properties. The keys in the map are
     *         comma-separated property values from all the properties which the
     *         results is grouped by, the map values are the respective sums or
     *         averages for the group.
     */
    protected Map<String, Long> getSumOrAvg(String property, String[] groupBy, boolean avg) {
        Map<String, Long> results = new HashMap<String, Long>();

        for (Entry<String, List<Long>> group : aggregate(property, groupBy).entrySet()) {
            long sum = 0;
            for (Long value : group.getValue()) {
                sum += value;
            }
            if (avg) {
                sum = sum / group.getValue().size();
            }
            results.put(group.getKey(), sum);
        }

        return results;
    }

    /**
     * Aggregates all values of a given property into one set of data.
     *
     * @param property
     *            The name of a numeric property.
     *
     * @return A set of all the property's (numeric) values in this data set.
     */
    public List<Long> aggregate(String property) {
        Map<String, List<Long>> values = aggregate(property, new String[0]);
        return values.isEmpty() ? new LinkedList<Long>() : values.get("");
    }

    /**
     * Aggregates all values of a given property, grouped by other properties.
     *
     * @param property
     *            The name of a numeric property.
     * @param groupBy
     *            The properties to group by.
     *
     * @return A map of group IDs (the comma-separated values of the groupBy
     *         fields) mapped to all the property values in this group.
     */
    public Map<String, List<Long>> aggregate(String property, String[] groupBy) {
        Map<String, List<Long>> aggregation = new HashMap<String, List<Long>>();

        for (DataRow row : this) {
            StringBuilder groupId = new StringBuilder();
            for (String groupProperty : groupBy) {
                Object groupValue = row.get(groupProperty);
                if (groupValue == null) {
                    groupValue = "";
                }
                if (groupId.length() > 0) {
                    groupId.append(',');
                }
                groupId.append(groupValue);
            }

            Object value = row.get(property);
            if (value == null) {
                continue;
            }
            Long longValue;
            if (value instanceof Number) {
                longValue = ((Number) value).longValue();
            }
            else {
                try {
                    longValue = Long.parseLong(value.toString());
                }
                catch (NumberFormatException e) {
                    continue;
                }
            }
            List<Long> groupList = aggregation.get(groupId.toString());
            if (groupList == null) {
                groupList = new LinkedList<Long>();
                aggregation.put(groupId.toString(), groupList);
            }
            groupList.add(longValue);
        }

        return aggregation;
    }

    /**
     * A serial ID for this version of the class.
     */
    private static final long serialVersionUID = 827754937096085408L;

}
