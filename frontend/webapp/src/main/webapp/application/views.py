# coding=UTF-8
from application import app, logic
import os


@app.route('/')
def home():
    return logic.home()


@app.route('/krog/random', methods=['POST'])
def random_page():
    return logic.random_page()


@app.route('/krog/save', methods=['POST'])
def user_krog_save():
    return logic.user_krog_save()


@app.route('/admin')
def admin():
    return logic.admin()


@app.route('/admin/krog/save', methods=['POST'])
def save_krog():
    return logic.save_krog()


@app.route('/admin/krog/update', methods=['POST'])
def update():
    return logic.update()


@app.route('/admin/uploadcsv', methods=['POST'])
def upload_csv():
    return logic.upload_csv()


@app.route('/admin/exportcsv', methods=['GET'])
def export_csv():
    return logic.export_csv()


@app.route('/user_profile')
def user_profile():
    return logic.user_profile()


@app.route('/bpm')
def bpm():
    return logic.bpm()


@app.route('/test1233')
def test1233():
    return logic.test1233()


@app.route('/error')
def error():
    return logic.error()
