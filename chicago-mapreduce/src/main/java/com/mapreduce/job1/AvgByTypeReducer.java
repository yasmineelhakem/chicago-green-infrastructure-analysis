package com.mapreduce.job1;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;

public class AvgByTypeReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

    @Override
    public void reduce(Text key, Iterable<DoubleWritable> values, Context context)
            throws IOException, InterruptedException {

        double sum = 0;
        long count = 0;

        // iterate over all values for a key
        for (DoubleWritable val : values) {
            sum += val.get();
            count++;
        }

        double average = sum / count;
        context.write(key, new DoubleWritable(average));

    }
}