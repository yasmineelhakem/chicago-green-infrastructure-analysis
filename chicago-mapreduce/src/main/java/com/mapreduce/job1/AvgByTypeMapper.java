package com.mapreduce.job1;

import com.mapreduce.util.CsvParser;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;

public class AvgByTypeMapper extends Mapper<Object, Text, Text, DoubleWritable> {

    private Text sensorType = new Text();
    private DoubleWritable value = new DoubleWritable();

    // map runs for every line in the dataset
    @Override
    public void map(Object key, Text line, Context context)
            throws IOException, InterruptedException {

        String row = line.toString().trim();

        // Skip the header row
        if (row.startsWith("measurement_title") || row.isEmpty()) return;

        // Parse the CSV line safely (handles commas inside quotes)
        String[] fields = CsvParser.parse(row);
        if (fields.length <= CsvParser.MEASUREMENT_VALUE) return; // incomplete row

        try {
            // Extract the two fields key = measurement type and value = numeric measurement
            String type = fields[CsvParser.MEASUREMENT_TYPE].trim().replace("\"", "");
            double val  = Double.parseDouble(fields[CsvParser.MEASUREMENT_VALUE].trim());

            // Emit the key-value pair => sends data to Reducer
            if (!type.isEmpty()) {
                sensorType.set(type);
                value.set(val);
                context.write(sensorType, value);
            }

        } catch (NumberFormatException e) {
            // Row has missing or corrupt value skip it
        }
    }
}