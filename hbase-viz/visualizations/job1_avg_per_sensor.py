import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import pandas as pd

avg_data = pd.DataFrame({
    'Sensor': [
        'DifferentialPressure\n(pascals)',
        'SoilMoisture\n(%)',
        'CumulativePrecipitation\n(inches)',
        'AtmosphericPressure\n(pascals)'
    ],
    'Average': [54793.98, 38.14, 8.87, 99628.52]
})

colors = ['#2196F3', '#4CAF50', '#FF9800', '#9C27B0']

fig, ax = plt.subplots(figsize=(8, 5))
ax.bar(avg_data['Sensor'], avg_data['Average'], color=colors, edgecolor='white', linewidth=0.8)
ax.set_title('Job 1 — Average Value per Sensor Type', fontsize=13, fontweight='bold')
ax.set_xlabel('Sensor Type')
ax.set_ylabel('Average Value')
ax.tick_params(axis='x', rotation=15)

plt.tight_layout()
plt.savefig('viz-results/job1_avg_per_sensor.png', dpi=150, bbox_inches='tight')
print("Saved: job1_avg_per_sensor.png")
plt.close()