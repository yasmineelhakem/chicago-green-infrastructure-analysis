#!/bin/bash

echo " Scan all averages (Job 1) "
hbase shell << 'EOF'
scan 'chicago_sensors', {STARTROW => 'avg', STOPROW => 'avg~'}
exit
EOF

echo "Scan all anomalies (Job 2) "
hbase shell << 'EOF'
scan 'chicago_sensors', {STARTROW => 'anomaly', STOPROW => 'anomaly~'}
exit
EOF

echo "Scan monthly 2018-03 (Job 3)"
hbase shell << 'EOF'
scan 'chicago_sensors', {STARTROW => 'monthly#2018-03', STOPROW => 'monthly#2018-03~'}
exit
EOF

echo "Scan monthly 2018-04 (Job 3) "
hbase shell << 'EOF'
scan 'chicago_sensors', {STARTROW => 'monthly#2018-04', STOPROW => 'monthly#2018-04~'}
exit
EOF

echo "Count total rows "
hbase shell << 'EOF'
count 'chicago_sensors'
exit
EOF