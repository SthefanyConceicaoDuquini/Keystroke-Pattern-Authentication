import pandas as pd
from sklearn.svm import OneClassSVM
import joblib

# Carrega os dados do arquivo CSV do usuário original
dados_usuario_original = pd.read_csv('dados_csv/meus_dados.csv', usecols=[0, 1, 2], names=['Dwell Time', 'Pressure Difference', 'Area'])

# Substitui os valores iguais a zero por NaN
dados_usuario_original['Pressure Difference'] = dados_usuario_original['Pressure Difference'].replace(0, float('nan'))

# Preenchw os valores NaN com a média dos valores de pressão
media_pressao = dados_usuario_original['Pressure Difference'].mean()
dados_usuario_original['Pressure Difference'].fillna(media_pressao, inplace=True)

# Divide os dados em recursos (X)
X = dados_usuario_original[['Dwell Time', 'Pressure Difference', 'Area']]

# Cria o modelo One-Class SVM
modelo_ocsvm = OneClassSVM(nu=0.01)
# Treina o modelo apenas com os dados do usuário original
modelo_ocsvm.fit(X)

# Salva o modelo treinado em um arquivo
modelo_ocsvm_filename = 'modelo_ocsvm.pkl'
joblib.dump(modelo_ocsvm, modelo_ocsvm_filename)
print(f"Modelo One-Class SVM treinado e salvo em {modelo_ocsvm_filename}")

# Valida o modelo utilizando os próprios dados do usuário original
resultados_validacao = modelo_ocsvm.predict(X)

# Imprime os resultados da validação
print("Resultados da Validação do Modelo:")
print(resultados_validacao)

# Calcula a acurácia
acuracia = (resultados_validacao == 1).sum() / len(resultados_validacao)

# Imprime a acurácia
print(f"Acurácia do Modelo: {acuracia * 100:.2f}%")

