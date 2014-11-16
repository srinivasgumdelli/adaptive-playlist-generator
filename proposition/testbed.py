from flask import Flask, jsonify

app = Flask(__name__)

@app.route("/")
def return_json():
    return jsonify({'song_name': 'test.mp3', 'duration': 10})

if __name__ == "__main__":
    app.run(debug=True)
