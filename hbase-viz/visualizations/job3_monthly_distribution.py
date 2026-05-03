import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import pandas as pd

monthly_data = pd.DataFrame({
    'Month': ['2018-03'] * 5 + ['2018-04'] * 5,
    'Sensor': [
        'CumulativePrecipitation', 'DifferentialPressure',
        'SoilMoisture', 'WindDirection', 'WindSpeed'
    ] * 2,
    'Count': [
        5016, 10032, 9882, 5016, 10032,
        21509, 43020, 42359, 21511, 43020
    ]
})

pivot = monthly_data.pivot(index='Month', columns='Sensor', values='Count')

fig, ax = plt.subplots(figsize=(10, 6))
pivot.plot(kind='bar', ax=ax, colormap='tab10', edgecolor='white', linewidth=0.5)

ax.set_title('Job 3 — Monthly Valid Measurements per Sensor', fontsize=13, fontweight='bold')
ax.set_xlabel('Month')
ax.set_ylabel('Number of Valid Measurements')
ax.tick_params(axis='x', rotation=0)
ax.legend(title='Sensor Type', bbox_to_anchor=(1.05, 1), loc='upper left')

plt.tight_layout()
plt.savefig('viz-results/job3_monthly_distribution.png', dpi=150, bbox_inches='tight')
print("Saved: job3_monthly_distribution.png")
plt.close()