# Keystroke Dynamics Messaging App

This repository contains a prototype authentication system based on keystroke dynamics. It combines:

- an Android messaging app with a custom on-screen keyboard
- a Python/Flask backend for collecting typing data
- a One-Class SVM model used to learn a user's typing profile and detect anomalies

The main idea is to capture how a user types, not only what they type. The app records behavioral signals such as key press duration, pressure variation, and touch area, then sends these samples to the server so a behavioral model can be trained and updated.

## Project Structure

```text
.
├── messager/            # Android application
└── treinamento-server/  # Flask server, training script, and verification script
```

## Main Components

### Android app (`messager`)

The Android application includes:

- a basic messaging-style interface
- a custom keyboard implemented in Java
- local password setup on first launch
- keystroke data capture during typing
- CSV export of captured typing samples
- HTTP upload of typing data to the backend
- periodic model download from the server

### Backend (`treinamento-server`)

The Python backend includes:

- a Flask server for receiving typing data
- a training script using `OneClassSVM`
- a verification script for testing new samples against the trained model
- a serialized model file (`modelo_ocsvm.pkl`)

## How It Works

1. The user opens the Android app and sets a password on first use.
2. The custom keyboard captures behavioral typing features for each key press:
   - dwell time
   - pressure difference
   - touch area
3. The app periodically exports collected samples to CSV.
4. The CSV data is sent to the Flask server through the `/receber_dados` endpoint.
5. The server stores the incoming samples in `treinamento-server/dados_csv/meus_dados.csv`.
6. The training script uses those samples to train a One-Class SVM model.
7. The app can download the trained model from `/download_modelo`.
8. The verification script can be used to classify a new dataset as normal behavior or possible intrusion.

## Technologies Used

### Mobile

- Android SDK
- Java
- Kotlin Gradle configuration
- RecyclerView
- OkHttp
- Retrofit

### Backend / Machine Learning

- Python
- Flask
- pandas
- scikit-learn
- joblib

## Android Requirements

- Android Studio
- Android SDK 33
- Minimum SDK 24

## Python Requirements

Install the backend dependencies manually, since this repository does not include a `requirements.txt` file:

```bash
pip install flask pandas scikit-learn joblib
```

## Running the Backend

Open a terminal in `treinamento-server` and start the Flask server:

```bash
cd treinamento-server
python server.py
```

The server runs on:

```text
http://0.0.0.0:5001
```

Available endpoints:

- `POST /receber_dados` - receives CSV typing samples in the `dados` form field
- `GET /download_modelo` - downloads the current trained model

## Training the Model

After collecting enough user typing data in `dados_csv/meus_dados.csv`, run:

```bash
cd treinamento-server
python treinamento.py
```

This script:

- loads the collected samples
- replaces `0` values in pressure difference with the column mean
- trains a `OneClassSVM`
- saves the trained model as `modelo_ocsvm.pkl`
- prints validation results using the training data itself

## Verifying New Samples

To test a new dataset against the trained model:

```bash
cd treinamento-server
python verifica.py
```

The script currently reads:

```text
dados_csv/or3.csv
```

It prints the prediction for each row and a final result indicating whether the sample set is closer to normal behavior or an intrusion profile.

## Running the Android App

1. Open `messager` in Android Studio.
2. Sync the Gradle project.
3. Connect a device or start an emulator.
4. Build and run the app.

On the first launch, the user is asked to create a local password. After that, the main screen shows a simple messaging interface with a custom keyboard.

## Important Configuration Notes

The Android app now centralizes backend URLs in:

- `messager/app/src/main/java/com/example/messager/ApiConfig.java`

By default, the app uses the Android emulator host alias:

```text
http://10.0.2.2:5001
```

If you run the app on a physical Android device, replace `10.0.2.2` in `ApiConfig.java` with the IP address of the machine running the Flask server on the same network.

## Current Limitations

- There is no `requirements.txt` for the Python environment.
- Backend address configuration is centralized but still code-based.
- The verification dataset path is fixed in `verifica.py`.
- The app downloads a file named `modelo_ocsvm.pmml`, but the backend serializes the model with `joblib`.
- Some networking and model-related classes appear to be experimental or partially integrated.
- The included validation in `treinamento.py` uses the same data that was used for training.

## Suggested Improvements

- add a `requirements.txt` or `pyproject.toml`
- move server URLs to a configuration file or build config
- create a more robust API contract between app and backend
- improve model evaluation with separate training and test datasets
- integrate on-device inference more clearly
- add automated tests for both Android and backend modules

## Repository Purpose

This project is a proof of concept for behavioral authentication using keystroke dynamics in a mobile messaging context. It can be used as a foundation for academic work, experimentation, or future improvements in mobile user authentication.
