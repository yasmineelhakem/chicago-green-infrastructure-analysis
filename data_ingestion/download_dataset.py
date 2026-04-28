import pandas as pd
from sodapy import Socrata

APP_TOKEN = "yuheXUQbJk5vPTYhaVcQYkEG8"

client = Socrata("data.cityofchicago.org", APP_TOKEN, timeout=120)

# Grab just 500k rows to start
chunk_size = 50000
offset = 0
all_records = []
target = 500_000

while offset < target:
    print(f"Fetching {offset:,} / {target:,}...")
    results = client.get("ggws-77ih", limit=chunk_size, offset=offset)

    if not results:
        break

    all_records.extend(results)
    offset += chunk_size

    if len(results) < chunk_size:
        break

df = pd.DataFrame.from_records(all_records)
df.to_csv("../data/chicago_sensor_sample.csv", index=False)
print(f"Done {len(df):,} rows, {df.shape[1]} columns")
