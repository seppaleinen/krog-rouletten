# coding=UTF-8
from application import app, logic
from flask import jsonify


@app.route('/krog/random', methods=['POST'])
def random_page():
    return logic.random_page()


@app.route('/error/<error_msg>')
def error(error_msg):
    resp = jsonify({'error': error_msg})
    resp.status_code = 500
    return resp


#@app.route('/details/<place_id>', defaults={'location': None}, methods=['POST'])
#@app.route('/details/<place_id>/<location>', methods=['POST'])
#def details(place_id, location):
#    return logic.details(place_id, location)
