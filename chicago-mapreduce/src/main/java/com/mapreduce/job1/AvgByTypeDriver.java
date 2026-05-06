package com.mapreduce.job1;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class AvgByTypeDriver {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: AvgByTypeDriver <input> <output>");
            System.exit(1);
        }

        // create haddop configuration
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Average Value per Sensor Type");

        // tells hadoop where the compiled code is 
        job.setJarByClass(AvgByTypeDriver.class);
        
        job.setMapperClass(AvgByTypeMapper.class);
        job.setReducerClass(AvgByTypeReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);

        job.setNumReduceTasks(2);  // 2 reducers = parallel processing

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
