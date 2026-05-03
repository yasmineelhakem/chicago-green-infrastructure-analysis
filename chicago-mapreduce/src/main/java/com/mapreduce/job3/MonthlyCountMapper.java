package com.mapreduce.job3;

import com.mapreduce.util.CsvParser;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;

public class MonthlyCountMapper extends Mapper<Object, Text, Text, IntWritable> {

    private static final IntWritable ONE = new IntWritable(1);
    private Text yearMonth = new Text();

    @Override
    public void map(Object key, Text line, Context context)
            throws IOException, InterruptedException {

        String row = line.toString().trim();
        if (row.startsWith("measurement_title") || row.isEmpty()) return;

        String[] fields = CsvParser.parse(row);
        if (fields.length <= CsvParser.MEASUREMENT_TIME) return;

        try {
            String type      = fields[CsvParser.MEASUREMENT_TYPE].trim().replace("\"", "");
            String unit      = fields[CsvParser.UNITS].trim().replace("\"", "").toLowerCase();
            double val       = Double.parseDouble(fields[CsvParser.MEASUREMENT_VALUE].trim());
            String timestamp = fields[CsvParser.MEASUREMENT_TIME].trim().replace("\"", "");

            // Garder seulement les mesures valides (même logique que Job 1)
            if (val < 0) return;
            if (unit.equals("millivolts") || unit.equals("count")) return;

            // Extraire "YYYY-MM" depuis le timestamp
            // Ex: "2024-03-15T10:30:00" → "2024-03"
            if (timestamp.length() < 7) return;
            String month = timestamp.substring(0, 7); // "YYYY-MM"

            // Clé : "2024-03|SoilMoisture"
            yearMonth.set(month + "|" + type);
            context.write(yearMonth, ONE);

        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            // ignorer lignes corrompues
        }
    }
} 
    

