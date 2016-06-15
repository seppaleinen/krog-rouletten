import random, requests, json
from flask import render_template, request, redirect, url_for, jsonify
from application.model import ManualForm, SearchForm, Objekt
from flask.ext import excel


def home():
    return render_template('index.html', form=SearchForm())

def user_profile():
    return render_template('user_profile.html', form=SearchForm())

def bpm():
    return render_template('bpm.html', form=SearchForm())

def test1233():
    return render_template('test1233.html', form=SearchForm())

def random_page(backend_url):
    krog = None
    form = SearchForm(request.form)
    print("FORMDATA: %s" % form.data)
    if form and not form.adress.data:
        try:
            krog = requests.post(backend_url + '/find/random', json=form.data).json()
        except ValueError:
            pass
        return render_template('krog.html', data=krog, form=form)
    elif form and form.adress.data:
        adress = request.form['adress'].replace(" ", "%20")
        result = requests.get('http://maps.googleapis.com/maps/api/geocode/json?address=%s&sensor=false' % adress)
        if result.status_code == 200:
            lat = result.json()

            # I'm a lazy and inefficient bastard.. Take the last location from list
            for place in lat['results']:
                form.adress = place['formatted_address']
                form.latitude = place['geometry']['location']['lat']
                form.longitude = place['geometry']['location']['lng']
                print("ADRESS:%s LAT:%s LNG%s" % (form.adress, form.latitude, form.longitude))

            try:
                krog = requests.post(backend_url + '/find/random', json=form.data).json()
                return render_template('krog.html', data=krog, form=form)
            except ValueError:
                return render_template('index.html', form=form)
    #Hell has frozen over
    return render_template('index.html', form=form)


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
