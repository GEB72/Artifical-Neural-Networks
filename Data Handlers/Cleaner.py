import pandas as pd

# read excel spreadsheet as data, open bin file
df = pd.read_excel('C:/Users/jakub/OneDrive/Documents/Neural Networks/Data/Data.xlsx', na_values=["NA"], usecols = "A:I")
bin = open("C:/Users/jakub/OneDrive/Documents/Neural Networks/Bin.txt", "a")

# define array to establish what validation type for each column
columns = ["AREA", "BFIHOST", "FARL", "FPEXT", "LDP", "PROPWET", "RMED-1D", "SAAR", "Index flood"]
non_negative = ["AREA", "LDP", "RMED-1D", "SAAR", "Index flood"]
ratio = ["BFIHOST", "FARL", "FPEXT", "PROPWET"]

# function takes column_name values and tries to parse to float
# if parse raises ValueError drop the row
def non_numeric_validation(column_name):
    global df
    j = 0
    for _, row in df[column_name].iteritems():
        try:
            float(row)
            j += 1
        except ValueError:
            df = df.drop(df.index[j])
            bin.write(column_name + "\t" + str(j+2) + "\t" + str(row) + "\n")
            pass

# function takes column_name values and checks if it between 0 and 1
# if not drop row
def ratio_validation(column_name):
    global df
    j = 0
    for _, row in df[column_name].iteritems():
        if row < 0 or row > 1:
            df = df.drop(df.index[j])
            bin.write(column_name + "\t" + str(j+2) + "\t" + str(row) + "\n")
            pass
        j += 1

# function takes column_name values and checks if it greater than 0
# if not drop row
def non_negative_validation(column_name):
    global df
    j = 0
    for _, row in df[column_name].iteritems():
        if row < 0:
            df = df.drop(df.index[j])
            bin.write(column_name + "\t" + str(j+2) + "\t" + str(row) + "\n")
            pass
        j += 1

# iterate over columns that require non-negative values
for column_name in non_negative:
    non_numeric_validation(column_name)
    non_negative_validation(column_name)

# iterate over columns that require ratio values
for column_name in ratio:
    non_numeric_validation(column_name)
    ratio_validation(column_name)

# write back to excel
df.to_excel('C:/Users/jakub/OneDrive/Documents/Neural Networks/Data/Data.xlsx', index=False)
