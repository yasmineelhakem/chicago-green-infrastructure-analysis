import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import pandas as pd

anomaly_data = pd.DataFrame({
    'Sensor': [
        'DifferentialPressure', 'SoilMoisture', 'CumulativePrecipitation',
        'Temperature', 'RelativeHumidity', 'WindSpeed', 'WindDirection'
    ],
    'VALID':   [53052, 52241, 26525,      0,     0,     0, 26527],
    'ANOMALY': [    0, 91138, 26527, 144191, 26527, 53052,     0]
})

x = range(len(anomaly_data))
width = 0.4

fig, ax = plt.subplots(figsize=(10, 6))
ax.bar([i - width/2 for i in x], anomaly_data['VALID'],
       width, label='VALID', color='#4CAF50', edgecolor='white')
ax.bar([i + width/2 for i in x], anomaly_data['ANOMALY'],
       width, label='ANOMALY', color='#F44336', edgecolor='white')

ax.set_title('Job 2 — Valid vs Anomaly Count per Sensor', fontsize=13, fontweight='bold')
ax.set_xlabel('Sensor Type')
ax.set_ylabel('Count')
ax.set_xticks(list(x))
ax.set_xticklabels(anomaly_data['Sensor'], rotation=20, ha='right')
ax.legend()

plt.tight_layout()
plt.savefig('viz-results/job2_valid_vs_anomaly.png', dpi=150, bbox_inches='tight')
print("Saved: job2_valid_vs_anomaly.png")
plt.close()