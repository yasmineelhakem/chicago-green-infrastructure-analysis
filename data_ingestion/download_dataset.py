import requests
import pandas as pd

url = "https://data.cityofchicago.org/api/v3/views/ggws-77ih/query.json"

limit = 50000
offset = 0

all_data = []

while True:
    params = {
        "$limit": limit,
        "$offset": offset
    }

    response = requests.get(url, params=params)
    data = response.json()

    if not data:
        break

    all_data.extend(data)
    offset += limit

df = pd.DataFrame(all_data)
df.to_csv("../data/chicago_sensor_data.csv", index=False)