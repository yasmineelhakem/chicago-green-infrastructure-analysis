#!/bin/bash

echo "Inserting Job 1 data (Averages)"

hbase shell << 'EOF'
put 'chicago_sensors', 'avg#AtmosphericPressure',     'avg:value', '99628.52'
put 'chicago_sensors', 'avg#DifferentialPressure',    'avg:value', '9962.84'
put 'chicago_sensors', 'avg#SoilMoisture',            'avg:value', '38.135'
put 'chicago_sensors', 'avg#CumulativePrecipitation', 'avg:value', '8.865'
exit
EOF

echo "Inserting Job 2 data (Anomalies)"

hbase shell << 'EOF'
put 'chicago_sensors', 'anomaly#DifferentialPressure#VALID',      'anomaly:count', '53052'
put 'chicago_sensors', 'anomaly#WindDirection#VALID',             'anomaly:count', '26527'
put 'chicago_sensors', 'anomaly#CumulativePrecipitation#VALID',   'anomaly:count', '26525'
put 'chicago_sensors', 'anomaly#CumulativePrecipitation#ANOMALY', 'anomaly:count', '26527'
put 'chicago_sensors', 'anomaly#SoilMoisture#VALID',              'anomaly:count', '52241'
put 'chicago_sensors', 'anomaly#SoilMoisture#ANOMALY',            'anomaly:count', '91138'
put 'chicago_sensors', 'anomaly#Temperature#ANOMALY',             'anomaly:count', '144191'
put 'chicago_sensors', 'anomaly#RelativeHumidity#ANOMALY',        'anomaly:count', '26527'
put 'chicago_sensors', 'anomaly#WindSpeed#ANOMALY',               'anomaly:count', '53052'
exit
EOF

echo "Inserting Job 3 data (Monthly)"

hbase shell << 'EOF'
put 'chicago_sensors', 'monthly#2018-03#CumulativePrecipitation', 'monthly:count', '5016'
put 'chicago_sensors', 'monthly#2018-03#DifferentialPressure',    'monthly:count', '10032'
put 'chicago_sensors', 'monthly#2018-03#SoilMoisture',            'monthly:count', '9882'
put 'chicago_sensors', 'monthly#2018-03#WindDirection',           'monthly:count', '5016'
put 'chicago_sensors', 'monthly#2018-03#WindSpeed',               'monthly:count', '10032'
put 'chicago_sensors', 'monthly#2018-04#CumulativePrecipitation', 'monthly:count', '21509'
put 'chicago_sensors', 'monthly#2018-04#DifferentialPressure',    'monthly:count', '43020'
put 'chicago_sensors', 'monthly#2018-04#SoilMoisture',            'monthly:count', '42359'
put 'chicago_sensors', 'monthly#2018-04#WindDirection',           'monthly:count', '21511'
put 'chicago_sensors', 'monthly#2018-04#WindSpeed',               'monthly:count', '43020'
exit
EOF

echo "All data inserted successfully"