package com.mapreduce.util;

// Handles quoted CSV fields like: "UI Labs, Bioswale","Water Level",42.5
// Standard split(",") would break on commas inside quotes

public class CsvParser {

    public static String[] parse(String line) {

        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

    }

    // Column indices for chicago_sensor_sample.csv
    public static final int MEASUREMENT_TITLE   = 0;
    public static final int MEASUREMENT_TYPE    = 1;  
    public static final int MEASUREMENT_MEDIUM  = 2;  
    public static final int MEASUREMENT_TIME    = 3;  
    public static final int MEASUREMENT_VALUE   = 4;  
    public static final int UNITS               = 5;  
    public static final int UNITS_ABBREVIATION  = 6;
    public static final int MEASUREMENT_PERIOD  = 7;
    public static final int DATA_STREAM_ID      = 8;
    public static final int RESOURCE_ID         = 9;
    public static final int MEASUREMENT_ID      = 10;
    public static final int RECORD_ID           = 11;
    public static final int LATITUDE            = 12; 
    public static final int LONGITUDE           = 13; 
} 