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

The dataset contains environmental measurements collected from sensors deployed in Chicago.

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

# Data Processing with MapReduce

The project implements three distributed analytics jobs:

## Job 1: Average Value per Sensor Type

The first job computes the **average measurement value for each sensor type**.

The process follows these steps:

- Read the CSV dataset from the distributed file system.
- Filter invalid records and measurements with incorrect units.
- Extract the sensor type and measurement value.
- Compute the sum and count of values per sensor type.
- Calculate the final average.

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

In addition to MapReduce, the project implements equivalent analytics using Spark.

Spark performs the same computations using **RDD transformations and actions**, which allows faster execution due to in-memory processing.

The Spark implementation demonstrates how the same analytics can be expressed more concisely compared to MapReduce while benefiting from improved performance.

## Spark Jobs

- **Job 1** – Computes average values per sensor type
- **Job 2** – Performs anomaly detection on measurements
- **Job 3** – Analyzes monthly measurement distributions

All three jobs produce results compatible with HBase storage.


# Data Storage with HBase

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


# Visualization

To make the results easier to interpret, Python scripts generate visualizations based on the processed data.

The visualizations include:

- **Average value per sensor type**
- **Distribution of valid vs anomalous measurements**
- **Monthly measurement trends**

These charts help understand sensor behavior and identify unusual patterns in the dataset.


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

