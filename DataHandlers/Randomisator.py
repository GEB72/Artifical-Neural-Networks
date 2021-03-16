import pandas as pd
import random

df = pd.read_excel("C:/Users/jakub/OneDrive/Documents/NeuralNetworks/Data/Random.xlsx", na_values=["NA"], usecols = "A:D")

def generate_values():
    # iterate over max number of parameters for largest Neural Network
    # generate random values and write to dataframe
    for i in range(1, 168):
        df.loc[i, "Input-Hidden Weights"] = random.uniform(-0.25, 0.25)
    for j in range(1, 16):
        df.loc[j, "Hidden-Output Weights"] = random.uniform(-0.25, 0.25)
    for k in range(1, 16):
        df.loc[k, "Hidden Biases"] = random.uniform(-0.25, 0.25)
    df.loc[1, "Output Biases"] = random.uniform(-0.25, 0.25)

    # write dataframe to excel sheet
    df.to_excel('C:/Users/jakub/OneDrive/Documents/NeuralNetworks/Data/Random.xlsx', index=False)

generate_values()
