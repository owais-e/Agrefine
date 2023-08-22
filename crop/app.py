from flask import Flask,request,jsonify
import pickle
import pandas as pd
import numpy as np
import sklearn

model = pickle.load(open('model.pkl', 'rb'))

app = Flask(__name__)


@app.route('/')
def home():
    return "hello world"


@app.route('/crop', methods=['POST'])
def crop():
    N = request.form.get('nitrogen')
    P = request.form.get('phosphorus')
    K = request.form.get('potassium')
    temperature = request.form.get('temperature')
    ph = request.form.get('ph')
    rainfall = request.form.get('rainfall')

    data = np.array([[N, P, K, temperature, ph, rainfall]])

    result = model.predict(data)[0]

    return jsonify({'suggested_crop': result})


@app.route('/fertilizer', methods=['POST'])
def fertilizer():
    N = request.form.get('fertNitrogen')
    P = request.form.get('fertPhosphorus')
    K = request.form.get('fertPotassium')
    crop = request.form.get('cropName')

    data = pd.read_csv('fertilizer_suggestion.csv')

    grouped_data = data.groupby('Crop')
    df = grouped_data.get_group(crop)

    min_N = df['N'].min()
    min_P = df['P'].min()
    min_K = df['K'].min()

    max_N = df['N'].max()
    max_P = df['P'].max()
    max_K = df['K'].max()

    if (max_N > N and N > min_N):
        nitrogen = 0
    else:
        if (N > max_N):
            nitrogen = N - max_N
        else:
            nitrogen = N - min_N

    if (max_K > K and K > min_K):
        potassium = 0
    else:
        if (K > max_K):
            potassium = K - max_K
        else:
            potassium = K - min_K

    if (max_P > P and P > min_P):
        phosphorus = 0
    else:
        if (P > max_P):
            phosphorus = P - max_P
        else:
            phosphorus = P - min_P

    npk_dict = {
        abs(nitrogen): "nit",
        abs(potassium): "pot",
        abs(phosphorus): "pho"
    }

    x = max(npk_dict.keys())
    lowest_one = npk_dict[x]

    if nitrogen == 0 and phosphorus == 0 and potassium == 0:
        result = 'all good'
    else:
        if lowest_one == 'nit':
            if nitrogen > 0:
                result = 'NHigh'
            else:
                result = "NLow"
        if lowest_one == 'pot':
            if potassium > 0:
                result = 'kHigh'
            else:
                result = "kLow"
        if lowest_one == 'pho':
            if phosphorus > 0:
                result = 'PHigh'
            else:
                result = "PLow"

    return jsonify({'suggested_fertilizer': result})


if __name__ == '__main__':
     app.run(host='0.0.0.0', port=80, debug=True)
    #app.run(debug=True)