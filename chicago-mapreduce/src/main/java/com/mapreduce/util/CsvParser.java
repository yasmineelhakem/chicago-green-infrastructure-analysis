package com.mapreduce.util;

// Handles quoted CSV fields like: "UI Labs, Bioswale","Water Level",42.5
// Standard split(",") would break on commas inside quotes

public class CsvParser {

    public static String[] parse(String line) {

        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

    }

    // Column indices for chicago_sensor_sample.csv
    public static final int MEASUREMENT_TITLE    = 0;
    public static final int MEASUREMENT_TYPE     = 2;
    public static final int MEASUREMENT_MEDIUM   = 3;
    public static final int MEASUREMENT_TIME     = 4;
    public static final int MEASUREMENT_VALUE    = 5;
    public static final int UNITS                = 6;
    public static final int DATA_STREAM_ID       = 9;
    public static final int RESOURCE_ID          = 10;
    public static final int LATITUDE             = 13;
    public static final int LONGITUDE            = 14;
} 