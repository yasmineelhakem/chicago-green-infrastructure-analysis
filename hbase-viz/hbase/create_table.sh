#!/bin/bash

echo "Creating HBase table chicago_sensors with column families: avg, anomaly, monthly"

hbase shell << 'EOF'
# Drop table if already exists
disable 'chicago_sensors'
drop 'chicago_sensors'

# Create table with 3 column families
create 'chicago_sensors',
  {NAME => 'avg',     VERSIONS => 1},
  {NAME => 'anomaly', VERSIONS => 1},
  {NAME => 'monthly', VERSIONS => 1}


# Verify
list
describe 'chicago_sensors'
exit
EOF

echo "Table created successfully"