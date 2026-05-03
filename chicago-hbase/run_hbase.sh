#!/bin/bash

JAR=/root/chicago-hbase.jar

echo "Create Table"
java -cp $JAR com.hbase.CreateTable

echo "Insert Data"
java -cp $JAR com.hbase.InsertData

echo "Query Data"
java -cp $JAR com.hbase.QueryData

echo "HBase complete"