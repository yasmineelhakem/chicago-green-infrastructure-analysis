# Chicago Environmental Sensors — Big Data Analytics Pipeline

This project implements a **complete Big Data analytics pipeline** to process and analyze environmental sensor data from the City of Chicago.

The pipeline processes sensor measurements such as soil moisture, precipitation, wind speed, humidity, and atmospheric pressure to extract meaningful statistics and detect anomalies.

## Project Overview

Environmental sensors continuously produce large volumes of measurements. Processing this data efficiently requires distributed systems capable of handling high throughput and scalable storage.

This project demonstrates a typical **Big Data workflow** composed of the following stages:

1. **Data ingestion** – Download and prepare the dataset.
2. **Distributed processing with MapReduce** – Perform batch analytics on Hadoop.
3. **Distributed processing with Spark** – Implement the same analytics using Spark for comparison.
4. **NoSQL storage with HBase** – Store processed results in a scalable column-oriented database.
5. **Visualization** – Generate charts from HBase results using Python.

The goal is to show how multiple Big Data technologies can work together in a complete analytics pipeline.

# Dataset

The dataset contains environmental measurements collected from sensors deployed in Chicago and is available from the [City of Chicago Data Portal](https://data.cityofchicago.org/Environment-Sustainable-Development/Smart-Green-Infrastructure-Monitoring-Sensors-Hist/ggws-77ih/about_data).

Each record represents a measurement taken by a sensor and contains information such as:

- Measurement title
- Sensor type
- Measurement medium
- Timestamp
- Measurement value
- Measurement unit
- Geographic coordinates (latitude and longitude)

Example sensor types include:

- Soil Moisture
- Wind Speed
- Wind Direction
- Atmospheric Pressure
- Relative Humidity
- Cumulative Precipitation

These measurements allow analysis of environmental conditions and detection of abnormal readings.

## Data Preparation

**Folder:** [`data_ingestion/`](data_ingestion/)

1. **Download the dataset** using the Python script in `data_ingestion/`:
   ```bash
   cd data_ingestion
   python download_dataset.py
   cd ..
   ```

2. **Copy the dataset to the Hadoop cluster**:
   ```bash
   docker cp data/chicago_sensor_sample.csv hadoop-master:/root/chicago_sensor_sample.csv
   ```

3. **Upload to HDFS**:
   ```bash
   # Connect to cluster and upload
   hdfs dfs -put chicago_sensor_sample.csv /chicago_sensors/
   
   # Verify the upload
   hdfs dfs -ls /chicago_sensors/
   ```

# Data Processing with MapReduce

**Folder:** [`chicago-mapreduce/`](chicago-mapreduce/)

The project implements three distributed analytics jobs:

## Job 1: Average Value per Sensor Type

The first job computes the **average measurement value for each sensor type**.

The process follows these steps:

- Read the CSV dataset from the distributed file system.
- Filter invalid records and measurements with incorrect units.
- Extract the sensor type and measurement value.
- Compute the sum and count of values per sensor type.
- Calculate the final average.

## Execution

1. **Build the MapReduce module**:
   ```bash
   cd chicago-mapreduce
   mvn clean package
   ```

2. **Copy the JAR to the cluster**:
   ```bash
   docker cp target/chicago-mapreduce-1.0-SNAPSHOT.jar hadoop-master:/root/chicago-mapreduce.jar
   ```

3. **Run Job 1 on Hadoop**:
   ```bash
   hadoop jar chicago-mapreduce.jar com.mapreduce.job1.AvgByTypeDriver \
     /chicago_sensors/chicago_sensor_sample.csv \
     /chicago_sensors/output_job1
   ```

4. **View the results**:
   ```bash
   hdfs dfs -cat /chicago_sensors/output_job1/part-r-00000
   ```

This job produces results such as:

## Job 2 — Anomaly Detection

The second job detects **anomalous sensor measurements**.

A measurement is considered anomalous if it meets one of the following conditions:

- The measurement value is negative.
- The measurement unit is invalid (for example millivolts or count).
- Certain sensors produce impossible values (for example wind speed equal to zero).

Each record is classified as either:

- **VALID**
- **ANOMALY**

The system counts the number of valid and anomalous measurements for each sensor type.

Example output:

This allows quick identification of problematic sensors or corrupted measurements.

## Job 3 — Monthly Measurement Distribution

The third job analyzes how sensor measurements are distributed over time.

For each measurement:

- The timestamp is parsed.
- The month is extracted in the format `YYYY-MM`.
- Measurements are grouped by **month and sensor type**.
- The total number of measurements is counted.

Example result:

This provides insights into sensor activity and seasonal variations.

# Spark Implementation

**Folder:** [`chicago-spark/`](chicago-spark/)

In addition to MapReduce, the project implements equivalent analytics using Spark.

Spark performs the same computations using **RDD transformations and actions**, which allows faster execution due to in-memory processing.

The Spark implementation demonstrates how the same analytics can be expressed more concisely compared to MapReduce while benefiting from improved performance.

## Spark Jobs

- **Job 1** – Computes average values per sensor type
- **Job 2** – Performs anomaly detection on measurements
- **Job 3** – Analyzes monthly measurement distributions

All three jobs produce results compatible with HBase storage.

## Execution

1. **Build the Spark module**:
   ```bash
   cd chicago-spark
   mvn clean package
   ```

2. **Copy the JAR to the cluster**:
   ```bash
   docker cp target/chicago-spark-1.0-SNAPSHOT-SPARK.jar hadoop-master:/root/chicago-spark.jar
   ```

3. **Run Job 1 with Spark**:
   ```bash
   spark-submit --class com.spark.job1.AvgByTypeSpark \
     --master local[*] \
     /root/chicago-spark.jar \
     /chicago_sensors/chicago_sensor_sample.csv \
     /chicago_sensors/spark_output_job1
   ```

4. **Run additional jobs** (Job 2 and Job 3) by replacing the class name and output paths accordingly.

# Data Storage with HBase

**Folder:** [`chicago-hbase/`](chicago-hbase/)

The results produced by the analytics jobs are stored in HBase, a distributed NoSQL database built on top of Hadoop.

A table named **`chicago_sensors`** is created to store the results.

The table contains three **column families**:

- **avg** – stores average values per sensor type
- **anomaly** – stores counts of valid and anomalous measurements
- **monthly** – stores monthly measurement counts



This design allows fast scans for specific sensor statistics or time periods.


# HBase Operations

Three Java programs manage the interaction with HBase.

### Table Creation

A program initializes the database schema by creating the `chicago_sensors` table and its column families.

### Data Insertion

Another program inserts the results produced by the analytics jobs into HBase using structured row keys.

### Data Query

A query program retrieves stored information using:

- table scans
- row key filters
- direct row access

These queries demonstrate how analytics results can be retrieved efficiently from a distributed NoSQL database.

## Setup and Execution

1. **Build the HBase module**:
   ```bash
   cd chicago-hbase
   mvn clean package
   ```

2. **Copy files to the cluster**:
   ```bash
   docker cp target/chicago-hbase-1.0-SNAPSHOT.jar hadoop-master:/root/chicago-hbase.jar
   docker cp run_hbase.sh hadoop-master:/root/run_hbase.sh
   ```

3. **Make the script executable and start HBase**:
   ```bash
   chmod +x run_hbase.sh
   ./run_hbase.sh
   ```
# Visualization

**Folder:** [`hbase-viz/`](hbase-viz/)

To make the results easier to interpret, Python scripts generate visualizations based on the processed data.

The visualizations include:

- **Average value per sensor type**
- **Distribution of valid vs anomalous measurements**
- **Monthly measurement trends**

These charts help understand sensor behavior and identify unusual patterns in the dataset.

## Execution

1. **Copy visualization scripts to the cluster**:
   ```bash
   docker cp hbase-viz hadoop-master:/root/hbase-viz
   ```

2. **Install Python dependencies** on the cluster:
   ```bash
   pip install happybase pandas matplotlib
   ```

3. **Start the HBase Thrift server**:
   ```bash
   hbase thrift start -p 9090 &
   ```

4. **Generate visualizations**:
   ```bash
   cd hbase-viz/visualizations
   python job1_avg_per_sensor.py
   python job2_valid_vs_anomaly.py
   python job3_monthly_distribution.py
   ```

5. **Copy results back to your local machine**:
   ```bash
   docker cp hadoop-master:/root/hbase-viz/viz-results ./viz-results
   ```

All visualization outputs will be saved as PNG files in the `viz-results/` directory.
# Technologies Used

This project uses several widely adopted Big Data technologies:

- **Hadoop** – distributed storage and processing framework
- **MapReduce** – batch processing model for large datasets
- **Apache Spark** – high-performance distributed data processing engine
- **HBase** – scalable column-oriented NoSQL database
- **Java** – implementation of MapReduce and HBase operations
- **Python** – data ingestion and visualization



# Conclusion

This project demonstrates how multiple Big Data technologies can be integrated into a single analytics pipeline. By combining distributed processing frameworks with scalable storage and visualization tools, the system efficiently processes large volumes of sensor data and extracts meaningful insights.

The pipeline illustrates key concepts in modern data engineering, including:

- **Distributed computation** – Processing data across multiple nodes
- **Anomaly detection** – Identifying unusual patterns in sensor readings
- **Time-series analysis** – Analyzing temporal trends in measurements
- **NoSQL data storage** – Managing structured data at scale
- **Data visualization** – Communicating insights through charts and graphs

