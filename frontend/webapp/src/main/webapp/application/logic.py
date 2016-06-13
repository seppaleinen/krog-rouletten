import random, requests, json
from flask import render_template, request, redirect, url_for, jsonify
from application.model import ManualForm, SearchForm, Objekt
from flask.ext import excel


def home():
    return render_template('index.html', form=SearchForm())


def random_page(backend_url):
    krog = None
    form = SearchForm(request.args)
    if form:
        print('FORM')
        print("request.form %s" % request.args)
        print("form.data %s" % json.dumps(form.data))
        print("form.data %s" % form.data)
        try:
            krog = requests.post(backend_url + '/find/random', json=json.dumps(form.data)).json()
        except ValueError:
            pass
        return render_template('krog.html', data=krog)
    else:
        print('ARGS')
        print(json.dumps(request.args))
        try:
            krog = requests.get(backend_url + '/find/random', json=json.dumps(request.args)).json()
        except ValueError:
            pass
        return render_template('krog.html', data=krog)


def get_gps_from_address(backend_url):
    adress = request.form['adress']
    result = requests.get('http://maps.googleapis.com/maps/api/geocode/json?address=%s&sensor=false' % adress)
    if result.status_code == 200:
        lat = result.json()

        latitude = None
        longitude = None

        for place in lat['results']:
            adress = place['formatted_address']
            latitude = place['geometry']['location']['lat']
            longitude = place['geometry']['location']['lng']

        arguments = 'longitude:' + str(longitude) + ',latitude=' + str(latitude) + ',distance=8'
        try:
            krog_response = requests.get(backend_url + '/find/random', json=json.dumps(arguments))
            return render_template('krog.html', data=krog_response.json())
        except ValueError:
            return render_template('index.html')
    else:
        return render_template('index.html')


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
    # Either form is valid, then close popup,
    # otherwise re-render popup and keep open
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
    try:
        return requests.get(backend_url + '/find/all').json()
    except Exception:
        return []
