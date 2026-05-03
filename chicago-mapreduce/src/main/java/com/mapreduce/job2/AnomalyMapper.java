package com.mapreduce.job2;

import com.mapreduce.util.CsvParser;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;

public class AnomalyMapper extends Mapper<Object, Text, Text, IntWritable> {

    private static final IntWritable ONE = new IntWritable(1);
    private Text outputKey = new Text();

    @Override
    public void map(Object key, Text line, Context context)
            throws IOException, InterruptedException {

        String row = line.toString().trim();
        if (row.startsWith("measurement_title") || row.isEmpty()) return;

        String[] fields = CsvParser.parse(row);
        if (fields.length <= CsvParser.UNITS) return;

        try {
            String type = fields[CsvParser.MEASUREMENT_TYPE].trim().replace("\"", "");
            String unit = fields[CsvParser.UNITS].trim().replace("\"", "").toLowerCase();
            double val  = Double.parseDouble(fields[CsvParser.MEASUREMENT_VALUE].trim());

            // Déterminer si la mesure est valide ou invalide
            boolean isAnomaly = false;

            if (val < 0) isAnomaly = true;
            if (unit.equals("millivolts"))                      isAnomaly = true;
            if (unit.equals("count"))                           isAnomaly = true;
            if (type.equals("RelativeHumidity") && val == -1.0) isAnomaly = true;
            if (type.equals("WindSpeed")        && val == 0.0)  isAnomaly = true;
            if (type.equals("WindDirection")    && val == 0.0)  isAnomaly = true;

            // Clé : "SensorType|VALID" ou "SensorType|ANOMALY"
            String status = isAnomaly ? "ANOMALY" : "VALID";
            outputKey.set(type + "|" + status);
            context.write(outputKey, ONE);

        } catch (NumberFormatException e) {
            // Ligne corrompue = anomalie aussi
            String type = fields.length > CsvParser.MEASUREMENT_TYPE
                ? fields[CsvParser.MEASUREMENT_TYPE].trim().replace("\"", "")
                : "Unknown";
            outputKey.set(type + "|ANOMALY");
            context.write(outputKey, ONE);
        }
    }
}