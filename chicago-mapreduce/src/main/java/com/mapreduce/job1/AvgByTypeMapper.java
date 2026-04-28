package com.mapreduce.job1;

import com.mapreduce.util.CsvParser;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AvgByTypeMapper extends Mapper<Object, Text, Text, DoubleWritable> {

    private Text sensorType = new Text();
    private DoubleWritable value = new DoubleWritable();

    private static final Set<String> VALID_TYPES = new HashSet<>(Arrays.asList(
        "CumulativePrecipitation",
        "SoilMoisture",
        "RelativeHumidity",
        "DifferentialPressure"
        // Temperature: all °C rows are error sentinels, all positive rows are millivolts → no usable data
        // WindSpeed / WindDirection: all 0.0 in this sample → broken sensor
    ));

    // Unit strings that indicate raw/uncalibrated signal — not physical measurements
    private static final Set<String> INVALID_UNITS = new HashSet<>(Arrays.asList(
        "millivolts",
        "count",
        "universal coordinated time"
    ));

    @Override
    public void map(Object key, Text line, Context context)
            throws IOException, InterruptedException {

        String row = line.toString().trim();
        if (row.startsWith("measurement_title") || row.isEmpty()) return;

        String[] fields = CsvParser.parse(row);
        if (fields.length <= CsvParser.UNITS) return;

        try {
            String title = fields[CsvParser.MEASUREMENT_TITLE].trim().replace("\"", "");
            String type  = fields[CsvParser.MEASUREMENT_TYPE].trim().replace("\"", "");
            String unit  = fields[CsvParser.UNITS].trim().replace("\"", "").toLowerCase();
            double val   = Double.parseDouble(fields[CsvParser.MEASUREMENT_VALUE].trim());

            // Skip sensor types with no usable data in this sample
            if (!VALID_TYPES.contains(type)) return;

            // Skip raw electrical signal units
            if (INVALID_UNITS.contains(unit)) return;

            // Skip negative sentinel/error values
            if (val < 0) return;

            // Separate the two pressure sensors by sensor title
            if (type.equals("DifferentialPressure")) {
                if (title.contains("Cumulus")) {
                    sensorType.set("AtmosphericPressure (pascals)");
                } else {
                    sensorType.set("DifferentialPressure (pascals)");
                }
                value.set(val);
                context.write(sensorType, value);
                return;
            }

            // Sanity bounds for remaining types
            if (type.equals("RelativeHumidity") && val > 100) return;

            // Emit: key = "SensorType (unit)", value = measurement
            String compositeKey = type + " (" + unit + ")";
            sensorType.set(compositeKey);
            value.set(val);
            context.write(sensorType, value);

        } catch (NumberFormatException e) {
            // skip rows with corrupt or missing values
        }
    }
}