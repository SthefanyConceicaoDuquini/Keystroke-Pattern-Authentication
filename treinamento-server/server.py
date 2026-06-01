from flask import Flask, request, send_file
import subprocess
import joblib
import os

app = Flask(__name__)

modelo_ocsvm = joblib.load('modelo_ocsvm.pkl')

@app.route('/receber_dados', methods=['POST'])
def receber_dados():
    try:
        dados_csv = request.form.get('dados')
        if dados_csv:
            with open('dados_csv/meus_dados.csv', 'a') as arquivo:
                arquivo.write(dados_csv)
            print("Dados recebidos e salvos com sucesso!")
            return "Dados recebidos com sucesso!", 200
        else:
            print("Nenhum dado recebido.")
            return "Nenhum dado recebido.", 400
    except Exception as e:
        print("Erro ao receber dados:", str(e))
        return "Erro ao receber dados.", 500

@app.route('/download_modelo', methods=['GET'])
def download_modelo():
    try:
        if modelo_ocsvm is not None:
            modelo_ocsvm_filename = 'modelo_ocsvm.pmml'
            joblib.dump(modelo_ocsvm, modelo_ocsvm_filename)
            path = os.path.abspath(modelo_ocsvm_filename)
            print("Caminho do arquivo:", path)
            return send_file(path, as_attachment=True, download_name='modelo_ocsvm.pmml')
        else:
            print("Modelo não encontrado")
            return "Modelo não encontrado", 404
    except Exception as e:
        print("Erro na rota '/download_modelo':", str(e))
        return str(e), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5001)

