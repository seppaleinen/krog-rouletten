import random, requests
from flask import render_template, request, redirect, url_for
from application.model import ManualForm, Objekt
from flask.ext import excel


def home(backend_url):
    krog = requests.get(backend_url + '/find/random').json()
    return render_template('index.html', data=krog)


def admin(backend_url):
    return render_template('admin.html', form=ManualForm(), kroglista=get_krog_list(backend_url))


def upload_csv(backend_url):
    file = request.files['file']
    if file and '.csv' in file.filename:
        file_ = {'file': ('file', file)}
        requests.post(backend_url + '/save/csv', files=file_)
        return render_template('admin.html', form=ManualForm(request.form), kroglista=get_krog_list(backend_url))
    else:
        return render_template('admin.html', form=ManualForm(request.form), kroglista=get_krog_list(backend_url))


def save_krog(backend_url):
    form = ManualForm(request.form)
    if request.method == 'POST' and form.validate():
        requests.post(backend_url + '/save', json=form.data)
        return render_template('admin.html', form=form, closePopup='close')
    else:
        return render_template('krog_popup.html', form=form, closePopup=None)


def update(backend_url):
    form = ManualForm(request.form)
    if request.form.get('update'):
        if request.method == 'POST':
            requests.post(backend_url + '/update', json=form.data)
        return redirect(url_for('admin'))
    elif request.form.get('delete'):
        requests.delete(backend_url + '/delete/krog', json=form.data)
        return redirect(url_for('admin'))


def export_csv(backend_url):
    kroglist = requests.get(backend_url + '/export/csv').json()
    data = [
        ['id', 'namn', 'adress', 'oppet_tider', 'bar_typ', 'stadsdel', 'beskrivning', 'betyg', 'hemside_lank', 'intrade', 'iframe_lank']
    ]
    for krog in kroglist:
        print(krog)
        data.append([
                     krog['id'],
                     krog['namn'],
                     krog['adress'],
                     krog['oppet_tider'],
                     krog['bar_typ'],
                     krog['stadsdel'],
                     krog['beskrivning'],
                     krog['betyg'],
                     krog['hemside_lank'],
                     krog['intrade'],
                     krog['iframe_lank']])

    response = excel.make_response_from_array(data, 'csv')
    response.headers["Content-Disposition"] = "attachment; filename=export.csv"
    response.headers["Content-type"] = "text/csv"
    return response


def popup():
    return render_template('krog_popup.html', form=ManualForm())


def get_krog_list(backend_url):
    return requests.get(backend_url + '/find/all').json()
