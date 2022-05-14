import pyarrow.parquet as pq

dff = pq.read_table(
    source="output-logs_part-00000-40f94a59-cf27-4ea3-97c0-7a5db0759f0e-c000.snappy.parquet").to_pandas()

import pandas as pd

filename = "output-logs_part-00000-40f94a59-cf27-4ea3-97c0-7a5db0759f0e-c000.snappy.parquet"
df = pd.read_parquet(filename)

df.style
