import joblib
import pandas as pd

# Carrega o modelo One-Class SVM previamente treinado
modelo_ocsvm = joblib.load('modelo_ocsvm.pkl')

# Carrega o novo conjunto de dados 
novo_conjunto_de_dados = pd.read_csv('dados_csv/or3.csv', usecols=[0, 1, 2], names=['Dwell Time', 'Pressure Difference', 'Area'])

# Substitui os valores iguais a zero por NaN e preencha com a média dos valores de pressão
novo_conjunto_de_dados['Pressure Difference'] = novo_conjunto_de_dados['Pressure Difference'].replace(0, float('nan'))
media_pressao = novo_conjunto_de_dados['Pressure Difference'].mean()
novo_conjunto_de_dados['Pressure Difference'].fillna(media_pressao, inplace=True)

# Faz previsões usando o modelo One-Class SVM
previsoes_novo = modelo_ocsvm.predict(novo_conjunto_de_dados)

# Adiciona as previsões ao DataFrame do novo conjunto de dados
novo_conjunto_de_dados['Anomaly'] = previsoes_novo  # -1 para anomalias, 1 para dados normais

# Determina se cada ponto de dados é "intruso" ou não
novo_conjunto_de_dados['Resultado'] = ['Intruso' if anomalia == -1 else 'Normal' for anomalia in previsoes_novo]

# Verifica a maioria das previsões
contagem_intruso = novo_conjunto_de_dados['Resultado'].value_counts().get('Intruso', 0)
contagem_normal = novo_conjunto_de_dados['Resultado'].value_counts().get('Normal', 0)

# Define o resultado final com base na maioria
if contagem_intruso > contagem_normal:
    resultado_final = 'Intruso'
else:
    resultado_final = 'Nao intruso'
# Loop para exibir o resultado de cada dado
for index, row in novo_conjunto_de_dados.iterrows():
    if row['Anomaly'] == -1:
        print(f"Dados: {row['Dwell Time']}, {row['Pressure Difference']}, {row['Area']}, Resultado: Anomalia")
    else:
        print(f"Dados: {row['Dwell Time']}, {row['Pressure Difference']}, {row['Area']}, Resultado: Normal")


# Exibe o resultado final
print(f"Resultado Final: {resultado_final}")

