import pandas as pd

# read excel sheets containing data and min/max values
df = pd.read_excel('C:/Users/jakub/OneDrive/Documents/NeuralNetworks/Data/Data.xlsx', na_values=["NA"], usecols = "A:I")
df2 =pd.read_excel('C:/Users/jakub/OneDrive/Documents/NeuralNetworks/Data/Min_Max.xlsx', na_values=["NA"], usecols = "A:J")

# function takes column_name, applies standardisation formula to each row,
# then replaces data with standard values
def standardise(column_name):
    global df, df2

    # establish max and min values for given column
    max = df[column_name].max()
    min = df[column_name].min()

    # save max and min values to excel
    df2.at[0, column_name] = max
    df2.at[1, column_name] = min

    # compute standardised value at every row
    for i, row in df[column_name].iteritems():
        standard_value = 0.8*((row-min)/(max-min))+0.1
        df.loc[i, column_name] = standard_value

# function takes column_name, applies inverse standardisation formula
# to each row, then reverts data with original values
def destandardise(column_name):
    global df, df2

    # establish max and min values for given column
    max = df2.loc[0, column_name]
    min = df2.loc[1, column_name]

    # compute de-standardised value at every row
    for i, standard_value in df[column_name].iteritems():
        original_value = ((standard_value-0.1)/0.8)*(max-min)+min
        df.loc[i, column_name] = original_value

# iterate over columns and apply function
for column_name in df:
    destandardise(column_name)

# write back to excel
df.to_excel('C:/Users/jakub/OneDrive/Documents/NeuralNetworks/Data/Data.xlsx', index=False)
df2.to_excel('C:/Users/jakub/OneDrive/Documents/NeuralNetworks/Data/Min_Max.xlsx', index=False)
