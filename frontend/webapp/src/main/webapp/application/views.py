from application import app
from flask import render_template

BACKEND = 'http://localhost:10080'

@app.route("/")
def home():
    return render_template('index.html')
