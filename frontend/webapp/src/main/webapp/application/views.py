# -*- coding: utf-8 -*-
from application import app, logic
import os


BACKEND = os.getenv('BACKEND_URL', 'http://localhost:10080')


@app.route('/')
def home():
    return logic.home(BACKEND)


@app.route('/admin')
def admin():
    return logic.admin(BACKEND)


@app.route('/admin/uploadFile', methods=['POST'])
def upload_csv():
    return logic.upload_csv(BACKEND)


@app.route('/admin/submit', methods=['POST'])
def submit_input():
    return logic.submit_input(BACKEND)

@app.route('/admin/submit', methods=['PUT'])
def update():
    return logic.update(BACKEND)


@app.route('/admin/export', methods=['GET'])
def export_csv():
    return logic.export_csv(BACKEND)


@app.route('/admin/popup', methods=['GET'])
def popup():
    return logic.popup()
