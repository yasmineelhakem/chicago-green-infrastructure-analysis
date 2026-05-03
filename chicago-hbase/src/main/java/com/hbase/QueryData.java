package com.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

public class QueryData {

    public static void main(String[] args) throws Exception {

        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", "localhost");
        config.set("hbase.zookeeper.property.clientPort", "2181");

        try (Connection connection = ConnectionFactory.createConnection(config);
             Table table = connection.getTable(
                 TableName.valueOf("chicago_sensors"))) {

            // Query 1 : Toutes les moyennes
            System.out.println("\nJob 1 Averages ");
            Scan scanAvg = new Scan();
            scanAvg.withStartRow(Bytes.toBytes("avg"));
            scanAvg.withStopRow(Bytes.toBytes("avg~"));
            printScan(table, scanAvg);

            // Query 2 : Toutes les anomalies 
            System.out.println("\nJob 2 Anomalies");
            Scan scanAnomaly = new Scan();
            scanAnomaly.withStartRow(Bytes.toBytes("anomaly"));
            scanAnomaly.withStopRow(Bytes.toBytes("anomaly~"));
            printScan(table, scanAnomaly);

            //  Query 3 : Mensuel 2018-03 
            System.out.println("\nJob 3  Monthly 2018-03 ");
            Scan scan0318 = new Scan();
            scan0318.withStartRow(Bytes.toBytes("monthly#2018-03"));
            scan0318.withStopRow(Bytes.toBytes("monthly#2018-03~"));
            printScan(table, scan0318);

            // Query 4 : Mensuel 2018-04 
            System.out.println("\nJob 3 Monthly 2018-04 ");
            Scan scan0418 = new Scan();
            scan0418.withStartRow(Bytes.toBytes("monthly#2018-04"));
            scan0418.withStopRow(Bytes.toBytes("monthly#2018-04~"));
            printScan(table, scan0418);

            // Query 5 : Get une ligne spécifique
            System.out.println("\nGet specific row : avg#SoilMoisture ");
            Get get = new Get(Bytes.toBytes("avg#SoilMoisture"));
            Result result = table.get(get);
            System.out.println("Value: " + Bytes.toString(
                result.getValue(
                    Bytes.toBytes("avg"),
                    Bytes.toBytes("value")
                )
            ));
        }
    }

    // Helper : afficher les résultats d'un scan
    private static void printScan(Table table, Scan scan) throws Exception {
        try (ResultScanner scanner = table.getScanner(scan)) {
            for (Result result : scanner) {
                String rowKey = Bytes.toString(result.getRow());
                // Afficher toutes les colonnes de la ligne
                result.listCells().forEach(cell -> {
                    String family  = Bytes.toString(CellUtil.cloneFamily(cell));
                    String column  = Bytes.toString(CellUtil.cloneQualifier(cell));
                    String value   = Bytes.toString(CellUtil.cloneValue(cell));
                    System.out.println("  " + rowKey +
                        " | " + family + ":" + column +
                        " = " + value);
                });
            }
        }
    }
}
