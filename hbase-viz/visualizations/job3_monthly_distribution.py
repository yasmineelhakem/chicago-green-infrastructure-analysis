import matplotlib
matplotlib.use('Agg')

import matplotlib.pyplot as plt
import pandas as pd
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from hbase.hbase_reader import connect_hbase, read_monthly


# create output directory
os.makedirs("viz-results", exist_ok=True)

# connect to HBase
connection = connect_hbase()

# read monthly data
df = read_monthly(connection)

print("Job 3 data from HBase:")
print(df)


pivot = df.pivot(index="Month", columns="Sensor", values="Count")

fig, axes = plt.subplots(1, 2, figsize=(16,6))


# Grouped bar chart
pivot.plot(
    kind="bar",
    ax=axes[0],
    colormap="tab10",
    edgecolor="white",
    linewidth=0.5
)

axes[0].set_title(
    "Monthly Valid Measurements per Sensor\n(Read from HBase)",
    fontsize=12,
    fontweight="bold"
)

axes[0].set_xlabel("Month")
axes[0].set_ylabel("Number of Valid Measurements")
axes[0].tick_params(axis="x", rotation=0)

axes[0].legend(
    title="Sensor",
    bbox_to_anchor=(1.05, 1),
    loc="upper left",
    fontsize=8
)


# Line chart: evolution over time
for sensor in pivot.columns:
    axes[1].plot(
        pivot.index,
        pivot[sensor],
        marker="o",
        linewidth=2,
        markersize=8,
        label=sensor
    )

axes[1].set_title(
    "Sensor Activity Evolution\nMarch → April 2018",
    fontsize=12,
    fontweight="bold"
)

axes[1].set_xlabel("Month")
axes[1].set_ylabel("Measurement Count")

axes[1].legend(
    title="Sensor",
    bbox_to_anchor=(1.05, 1),
    loc="upper left",
    fontsize=8
)

axes[1].grid(True, alpha=0.3)


plt.tight_layout()

plt.savefig(
    "viz-results/job3_monthly_distribution.png",
    dpi=150,
    bbox_inches="tight"
)

print("Saved: job3_monthly_distribution.png")

plt.close()

connection.close()