package com.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

public class InsertData {

    private static Table table;

    public static void main(String[] args) throws Exception {

        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", "localhost");
        config.set("hbase.zookeeper.property.clientPort", "2181");

        try (Connection connection = ConnectionFactory.createConnection(config)) {

            table = connection.getTable(TableName.valueOf("chicago_sensors"));

            // job 1 : Averages
            System.out.println("Inserting Job 1 data (Averages)...");
            insertAvg("AtmosphericPressure",     "99628.52");
            insertAvg("DifferentialPressure",    "9962.84");
            insertAvg("SoilMoisture",            "38.135");
            insertAvg("CumulativePrecipitation", "8.865");

            // job 2 : Anomalies
            System.out.println("Inserting Job 2 data (Anomalies)...");
            insertAnomaly("DifferentialPressure",    "VALID",   "53052");
            insertAnomaly("WindDirection",            "VALID",   "26527");
            insertAnomaly("CumulativePrecipitation", "VALID",   "26525");
            insertAnomaly("CumulativePrecipitation", "ANOMALY", "26527");
            insertAnomaly("SoilMoisture",            "VALID",   "52241");
            insertAnomaly("SoilMoisture",            "ANOMALY", "91138");
            insertAnomaly("Temperature",             "ANOMALY", "144191");
            insertAnomaly("RelativeHumidity",        "ANOMALY", "26527");
            insertAnomaly("WindSpeed",               "ANOMALY", "53052");

            // job 3 : Monthly
            System.out.println("Inserting Job 3 data (Monthly)...");
            insertMonthly("2018-03", "CumulativePrecipitation", "5016");
            insertMonthly("2018-03", "DifferentialPressure",    "10032");
            insertMonthly("2018-03", "SoilMoisture",            "9882");
            insertMonthly("2018-03", "WindDirection",            "5016");
            insertMonthly("2018-03", "WindSpeed",               "10032");
            insertMonthly("2018-04", "CumulativePrecipitation", "21509");
            insertMonthly("2018-04", "DifferentialPressure",    "43020");
            insertMonthly("2018-04", "SoilMoisture",            "42359");
            insertMonthly("2018-04", "WindDirection",            "21511");
            insertMonthly("2018-04", "WindSpeed",               "43020");

            System.out.println("All data inserted successfully!");
            table.close();
        }
    }

    // Helper : insérer une moyenne
    // Row key : "avg#SensorType"
    private static void insertAvg(String sensor, String value) throws Exception {
        String rowKey = "avg#" + sensor;
        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(
            Bytes.toBytes("avg"),
            Bytes.toBytes("value"),
            Bytes.toBytes(value)
        );
        table.put(put);
        System.out.println("  Inserted: " + rowKey + " -> " + value);
    }

    // Helper : insérer une anomalie
    // Row key : "anomaly#SensorType#STATUS"
    private static void insertAnomaly(String sensor, String status, String count)
            throws Exception {
        String rowKey = "anomaly#" + sensor + "#" + status;
        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(
            Bytes.toBytes("anomaly"),
            Bytes.toBytes("count"),
            Bytes.toBytes(count)
        );
        table.put(put);
        System.out.println("  Inserted: " + rowKey + " -> " + count);
    }

    // Helper : insérer un count mensuel
    // Row key : "monthly#YYYY-MM#SensorType"
    private static void insertMonthly(String month, String sensor, String count)
            throws Exception {
        String rowKey = "monthly#" + month + "#" + sensor;
        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(
            Bytes.toBytes("monthly"),
            Bytes.toBytes("count"),
            Bytes.toBytes(count)
        );
        table.put(put);
        System.out.println("  Inserted: " + rowKey + " -> " + count);
    }
}