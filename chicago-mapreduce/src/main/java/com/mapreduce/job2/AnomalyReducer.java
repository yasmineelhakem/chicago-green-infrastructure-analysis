package com.mapreduce.job2;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;

public class AnomalyReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException {

        int total = 0;
        for (IntWritable val : values) {
            total += val.get();
        }

        // Émettre : "SensorType|VALID" → 1250
        //           "SensorType|ANOMALY" → 340
        context.write(key, new IntWritable(total));
    }
}