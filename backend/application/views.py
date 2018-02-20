# coding=UTF-8
from application import app, logic
from flask_api import FlaskAPI, status, exceptions


@app.route('/krog/random', methods=['POST'])
def random_page():
    return logic.random_page()


@app.route('/error/<error_msg>')
def error(error_msg):
    raise exceptions.APIException(error_msg)


@app.route('/details/<place_id>', defaults={'location': None})
@app.route('/details/<place_id>/<location>')
def details(place_id, location):
    return logic.details(place_id, location)
