package com.spark.job3;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MonthlyCountSpark {

    private static final Set<String> INVALID_UNITS = new HashSet<>(Arrays.asList(
        "millivolts", "count", "universal coordinated time"
    ));

    public static void main(String[] args) {

        SparkConf conf = new SparkConf()
            .setAppName("MonthlyCount-Spark");

        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> lines = sc.textFile(args[0]);

        JavaRDD<String> data = lines.filter(line ->
            !line.startsWith("measurement_title") && !line.isEmpty()
        );

        // MAP : émettre ("YYYY-MM|SensorType", 1)
        JavaPairRDD<String, Integer> pairs = data.mapToPair(line -> {
            try {
                String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (fields.length <= 5) return null;

                String type      = fields[1].trim().replace("\"", "");
                String unit      = fields[5].trim().replace("\"", "").toLowerCase();
                double val       = Double.parseDouble(fields[4].trim());
                String timestamp = fields[3].trim().replace("\"", "");

                // Garder seulement les mesures valides
                if (val < 0) return null;
                if (INVALID_UNITS.contains(unit)) return null;
                if (timestamp.length() < 7) return null;

                String month = timestamp.substring(0, 7); // "YYYY-MM"
                return new Tuple2<>(month + "|" + type, 1);

            } catch (Exception e) {
                return null;
            }
        }).filter(t -> t != null);

        // REDUCE : compter par mois
        JavaPairRDD<String, Integer> counts = pairs.reduceByKey(
            (a, b) -> a + b
        );

        // Trier par clé (ordre chronologique)
        JavaPairRDD<String, Integer> sorted = counts.sortByKey();

        sorted.saveAsTextFile(args[1]);

        sc.close();
    }
}