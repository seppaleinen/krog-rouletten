# -*- coding: utf-8 -*-
from application import app, logic
import os


BACKEND = os.getenv('BACKEND_URL', 'http://localhost:10080')


@app.route('/')
def home():
    return logic.home()


@app.route('/krog/random')
def random_page():
    return logic.random_page(BACKEND)


@app.route('/admin')
def admin():
    return logic.admin(BACKEND)


@app.route('/admin/uploadFile', methods=['POST'])
def upload_csv():
    return logic.upload_csv(BACKEND)


@app.route('/admin/save', methods=['POST'])
def save_krog():
    return logic.save_krog(BACKEND)


@app.route('/admin/update', methods=['POST'])
def update():
    return logic.update(BACKEND)


@app.route('/admin/export', methods=['GET'])
def export_csv():
    return logic.export_csv(BACKEND)


@app.route('/admin/popup', methods=['GET'])
def popup():
    return logic.popup()
