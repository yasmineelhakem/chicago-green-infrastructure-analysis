package com.spark.job2;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

public class AnomalyDetectionSpark {

    public static void main(String[] args) {

        SparkConf conf = new SparkConf()
            .setAppName("AnomalyDetection-Spark");

        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> lines = sc.textFile(args[0]);

        JavaRDD<String> data = lines.filter(line ->
            !line.startsWith("measurement_title") && !line.isEmpty()
        );

        // MAP : émettre (sensorType|VALID ou ANOMALY, 1)
        JavaPairRDD<String, Integer> pairs = data.mapToPair(line -> {
            try {
                String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (fields.length <= 5) return null;

                String type = fields[1].trim().replace("\"", "");
                String unit = fields[5].trim().replace("\"", "").toLowerCase();
                double val  = Double.parseDouble(fields[4].trim());

                boolean isAnomaly = false;
                if (val < 0)                                        isAnomaly = true;
                if (unit.equals("millivolts"))                      isAnomaly = true;
                if (unit.equals("count"))                           isAnomaly = true;
                if (type.equals("RelativeHumidity") && val == -1.0) isAnomaly = true;
                if (type.equals("WindSpeed")        && val == 0.0)  isAnomaly = true;
                if (type.equals("WindDirection")    && val == 0.0)  isAnomaly = true;

                String status = isAnomaly ? "ANOMALY" : "VALID";
                return new Tuple2<>(type + "|" + status, 1);

            } catch (Exception e) {
                return null;
            }
        }).filter(t -> t != null);

        // REDUCE : compter
        JavaPairRDD<String, Integer> counts = pairs.reduceByKey(
            (a, b) -> a + b
        );

        counts.saveAsTextFile(args[1]);

        sc.close();
    }
}
