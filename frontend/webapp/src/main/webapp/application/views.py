# coding=UTF-8
from application import app, logic


@app.route('/')
def home():
    return logic.home()


@app.route('/krog/random', methods=['POST'])
def random_page():
    return logic.random_page()


@app.route('/admin')
def admin():
    return logic.admin()


@app.route('/settings')
def settings():
    return logic.settings()


@app.route('/admin/unapproved')
def unapproved():
    return logic.unapproved()


@app.route('/profile')
def profile():
    return logic.profile()


@app.route('/error/<error_msg>')
def error(error_msg):
    return logic.error(error_msg)


@app.route('/details/<place_id>')
def details(place_id):
    return logic.details(place_id)
