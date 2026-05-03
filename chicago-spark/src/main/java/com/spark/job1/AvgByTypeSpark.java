package com.spark.job1;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AvgByTypeSpark {

    private static final Set<String> VALID_TYPES = new HashSet<>(Arrays.asList(
        "CumulativePrecipitation",
        "SoilMoisture",
        "DifferentialPressure"
    ));

    private static final Set<String> INVALID_UNITS = new HashSet<>(Arrays.asList(
        "millivolts", "count", "universal coordinated time"
    ));

    public static void main(String[] args) {

        SparkConf conf = new SparkConf()
            .setAppName("AvgByType-Spark");

        JavaSparkContext sc = new JavaSparkContext(conf);

        // Lire le CSV depuis HDFS
        JavaRDD<String> lines = sc.textFile(args[0]);

        // Filtrer l'entête
        JavaRDD<String> data = lines.filter(line ->
            !line.startsWith("measurement_title") && !line.isEmpty()
        );

        // MAP : extraire (sensorType, value)
        JavaPairRDD<String, Double> pairs = data.mapToPair(line -> {
            String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            if (fields.length <= 5) return null;

            String type  = fields[1].trim().replace("\"", "");
            String unit  = fields[5].trim().replace("\"", "").toLowerCase();
            double val   = Double.parseDouble(fields[4].trim());

            if (!VALID_TYPES.contains(type)) return null;
            if (INVALID_UNITS.contains(unit)) return null;
            if (val < 0) return null;

            return new Tuple2<>(type + " (" + unit + ")", val);
        }).filter(t -> t != null);

        // REDUCE : calculer la moyenne
        // On accumule (sum, count) puis on divise
        JavaPairRDD<String, Tuple2<Double, Long>> sumCount = pairs.mapToPair(t ->
            new Tuple2<>(t._1, new Tuple2<>(t._2, 1L))
        ).reduceByKey((a, b) ->
            new Tuple2<>(a._1 + b._1, a._2 + b._2)
        );

        JavaPairRDD<String, Double> averages = sumCount.mapToPair(t ->
            new Tuple2<>(t._1, t._2._1 / t._2._2)
        );

        // Sauvegarder le résultat
        averages.saveAsTextFile(args[1]);

        sc.close();
    }
}