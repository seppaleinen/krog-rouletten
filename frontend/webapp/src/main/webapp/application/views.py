# coding=UTF-8
from application import app, logic


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


@app.route('/settings')
def settings():
    return logic.settings()


@app.route('/admin/unapproved')
def unapproved():
    return logic.unapproved()


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


@app.route('/profile')
def profile():
    return logic.profile()


@app.route('/error/<error_msg>')
def error(error_msg):
    return logic.error(error_msg)


@app.route('/details/<place_id>')
def details(place_id):
    return logic.details(place_id)
