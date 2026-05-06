import happybase
import pandas as pd

def connect_hbase(host='localhost', port=9090):
    connection = happybase.Connection(host, port=port)
    connection.open()
    print(f"Connected to HBase at {host}:{port}")
    return connection


def read_averages(connection):
    table = connection.table('chicago_sensors')
    data = []

    for key, row in table.scan(row_prefix=b'avg#'):
        sensor = key.decode().replace('avg#', '')
        value  = float(row[b'avg:value'].decode())
        data.append({'Sensor': sensor, 'Average': value})

    return pd.DataFrame(data)


def read_anomalies(connection):
    table = connection.table('chicago_sensors')
    data = {}

    for key, row in table.scan(row_prefix=b'anomaly#'):
        parts  = key.decode().split('#')
        sensor = parts[1]
        status = parts[2]
        count  = int(row[b'anomaly:count'].decode())

        if sensor not in data:
            data[sensor] = {'VALID': 0, 'ANOMALY': 0}

        data[sensor][status] = count

    rows = [
        {'Sensor': s, 'VALID': v['VALID'], 'ANOMALY': v['ANOMALY']}
        for s, v in data.items()
    ]

    return pd.DataFrame(rows)


def read_monthly(connection):
    table = connection.table('chicago_sensors')
    data = []

    for key, row in table.scan(row_prefix=b'monthly#'):
        parts  = key.decode().split('#')
        month  = parts[1]
        sensor = parts[2]
        count  = int(row[b'monthly:count'].decode())

        data.append({
            'Month': month,
            'Sensor': sensor,
            'Count': count
        })

    return pd.DataFrame(data)