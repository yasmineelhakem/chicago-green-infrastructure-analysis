package com.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;

public class CreateTable {

    public static void main(String[] args) throws Exception {

        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", "localhost");
        config.set("hbase.zookeeper.property.clientPort", "2181");

        try (Connection connection = ConnectionFactory.createConnection(config);
             Admin admin = connection.getAdmin()) {

            TableName tableName = TableName.valueOf("chicago_sensors");

            // Supprimer si déjà existe
            if (admin.tableExists(tableName)) {
                System.out.println("Table exists — dropping...");
                admin.disableTable(tableName);
                admin.deleteTable(tableName);
            }

            // Créer le schema
            TableDescriptorBuilder builder = TableDescriptorBuilder
                .newBuilder(tableName);

            // Column Family 1 : avg (résultats Job 1)
            builder.setColumnFamily(
                ColumnFamilyDescriptorBuilder.newBuilder("avg".getBytes())
                    .setMaxVersions(1)
                    .build()
            );

            // Column Family 2 : anomaly (résultats Job 2)
            builder.setColumnFamily(
                ColumnFamilyDescriptorBuilder.newBuilder("anomaly".getBytes())
                    .setMaxVersions(1)
                    .build()
            );

            // Column Family 3 : monthly (résultats Job 3)
            builder.setColumnFamily(
                ColumnFamilyDescriptorBuilder.newBuilder("monthly".getBytes())
                    .setMaxVersions(1)
                    .build()
            );

            admin.createTable(builder.build());
            System.out.println("Table chicago_sensors created successfully!");

            // Lister les tables pour vérifier
            System.out.println("Tables in HBase:");
            admin.listTableNames();
        }
    }
}