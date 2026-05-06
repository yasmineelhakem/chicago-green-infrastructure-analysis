import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from hbase.hbase_reader import connect_hbase, read_averages

os.makedirs('viz-results', exist_ok=True)

connection = connect_hbase()
df = read_averages(connection)

fig, ax = plt.subplots(figsize=(10,6))

bars = ax.bar(df['Sensor'], df['Average'])

ax.set_title('Average Value per Sensor Type')
ax.set_xlabel('Sensor Type')
ax.set_ylabel('Average Value')

plt.tight_layout()
plt.savefig('viz-results/job1_avg_per_sensor.png', dpi=150)

connection.close()