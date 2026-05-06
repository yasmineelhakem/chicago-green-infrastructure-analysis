import matplotlib
matplotlib.use('Agg')

import matplotlib.pyplot as plt
import numpy as np
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from hbase.hbase_reader import connect_hbase, read_anomalies


# create output directory
os.makedirs("viz-results", exist_ok=True)

# connect to HBase
connection = connect_hbase()

# read anomaly data
df = read_anomalies(connection)

print("Job 2 data from HBase:")
print(df)

x = np.arange(len(df))
width = 0.35

fig, axes = plt.subplots(1, 2, figsize=(16,6))


# Grouped bar chart
axes[0].bar(x - width/2, df['VALID'], width,
            label='VALID',
            color='#4CAF50',
            edgecolor='white')

axes[0].bar(x + width/2, df['ANOMALY'], width,
            label='ANOMALY',
            color='#F44336',
            edgecolor='white')

axes[0].set_title("Valid vs Anomaly Count per Sensor\n(Read from HBase)",
                  fontsize=12, fontweight="bold")

axes[0].set_xticks(x)
axes[0].set_xticklabels(df['Sensor'], rotation=25, ha='right')
axes[0].set_ylabel("Count")
axes[0].legend()


# Pie chart (global anomaly percentage)
total_valid = df['VALID'].sum()
total_anomaly = df['ANOMALY'].sum()

axes[1].pie(
    [total_valid, total_anomaly],
    labels=["VALID", "ANOMALY"],
    colors=["#4CAF50", "#F44336"],
    autopct="%1.1f%%",
    startangle=90,
    explode=(0,0.05)
)

axes[1].set_title(
    "Global Data Quality (All Sensors)",
    fontsize=12,
    fontweight="bold"
)


plt.tight_layout()

plt.savefig(
    "viz-results/job2_anomaly_analysis.png",
    dpi=150,
    bbox_inches="tight"
)

print("Saved: job2_anomaly_analysis.png")

plt.close()

connection.close()