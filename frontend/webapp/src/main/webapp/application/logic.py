# coding=UTF-8
import random, requests, json, urllib, os
from flask import render_template, request, redirect, url_for, jsonify
from application.model import ManualForm, SearchForm, UserKrogForm
from flask.ext import excel


backend_url = os.getenv('BACKEND_URL', 'http://localhost:10080')


def home():
    return render_template('index.html', **Helper().forms())


def random_page():
    form = SearchForm(request.form)
    print("FORMDATA: %s" % form.data)
    if form and not form.adress.data:
        try:
            krog = requests.post(backend_url + '/find/random', json=form.data).json()
            return render_template('krog.html', data=krog, **Helper().forms({'searchForm':form}))
        except ValueError:
            return render_template('error.html', data='Hittade ingen krog på din sökning', **Helper().forms())
    elif form and form.adress.data:
        adress = urllib.quote(form.adress.data.encode('utf8'))
        result = requests.get('http://maps.googleapis.com/maps/api/geocode/json?address=%s&sensor=false' % adress)
        if result.status_code == 200:
            lat = result.json()

            # I'm a lazy and inefficient bastard.. Take the last location from list
            for place in lat['results']:
                form.adress.data = place['formatted_address']
                form.latitude.data = place['geometry']['location']['lat']
                form.longitude.data = place['geometry']['location']['lng']
                print("ADRESS:%s LAT:%s LNG%s" % (form.adress.data, form.latitude.data, form.longitude.data))

            try:
                krog = requests.post(backend_url + '/find/random', json=form.data).json()
                return render_template('krog.html', data=krog, **Helper().forms({'searchForm':form}))
            except Exception:
                return render_template('error.html', data='Hittade ingen krog på din sökning', **Helper().forms())
    #Hell has frozen over
    return render_template('error.html', data='Nånting gick fel', **Helper().forms())


def admin():
    return render_template('admin.html', kroglista=Helper.get_krog_list(), **Helper().forms())


def unapproved():
    return render_template('admin.html', kroglista=Helper.get_unapproved_krog_list(), **Helper().forms())


def upload_csv():
    file = request.files['file']
    if file and '.csv' in file.filename:
        file_ = {'file': ('file', file)}
        try:
            requests.post(backend_url + '/save/csv', files=file_)
            return render_template('admin.html', kroglista=Helper.get_krog_list(), **Helper().forms({'adminKrogForm': ManualForm(request.form)}))
        except Exception:
            return render_template('error.html', data='Nånting gick fel', **Helper().forms())
    else:
        return render_template('admin.html', kroglista=Helper.get_krog_list(), **Helper().forms({'adminKrogForm':ManualForm(request.form)}))


def save_krog():
    form = ManualForm(request.form)
    if request.method == 'POST' and form.validate():
        requests.post(backend_url + '/save', json=form.data)
    return render_template('admin.html', kroglista=Helper.get_krog_list(), **Helper().forms({'adminKrogForm':form}))


def update():
    form = ManualForm(request.form)
    if request.form.get('update'):
        if request.method == 'POST':
            requests.post(backend_url + '/update', json=form.data)
        return redirect(url_for('admin'))
    elif request.form.get('delete'):
        requests.delete(backend_url + '/delete/krog', json=form.data)
        return redirect(url_for('admin'))
    elif request.form.get('approve'):
        form.approved.data = True
        requests.post(backend_url + '/update', json=form.data)
        return redirect(url_for('admin'))


def export_csv():
    kroglist = requests.get(backend_url + '/export/csv').json()
    data = [
        ['id', 'namn', 'adress', 'oppet_tider', 'bar_typ', 'stadsdel', 'beskrivning', 'betyg', 'hemside_lank', 'intrade', 'iframe_lank', 'approved']
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
                     krog['iframe_lank'],
                     krog['approved']
        ])

    response = excel.make_response_from_array(data, 'csv')
    response.headers["Content-Disposition"] = "attachment; filename=export.csv"
    response.headers["Content-type"] = "text/csv"
    return response


def user_profile():
    return render_template('user_profile.html', **Helper().forms())


def bpm():
    return render_template('bpm.html', **Helper().forms())


def test1233():
    return render_template('test1233.html', **Helper().forms())


def error():
    return render_template('error.html', data='DET GICK FEL', **Helper().forms())


def user_krog_save():
    userKrogForm = UserKrogForm(request.form)
    # Either form is valid, then close popup,
    # otherwise re-render popup and keep open
    print("Saving krog %s" % userKrogForm.data)
    if request.method == 'POST' and userKrogForm.validate():
        print('POST')
        try:
            requests.post(backend_url + '/save', json=userKrogForm.data)
        except Exception:
            pass
        return render_template('index.html', **Helper().forms({'userKrogForm': userKrogForm}))
    else:
        return render_template('index.html', **Helper().forms({'userKrogForm': userKrogForm}))


class Helper(object):
    def forms(self, kwargs={}):
        forms = {'searchForm': SearchForm(), 'userKrogForm': UserKrogForm(), 'adminKrogForm': ManualForm()}
        forms.update(kwargs)
        return forms

    @staticmethod
    def get_krog_list():
        try:
            return requests.get("%s/find/all/approved" % backend_url).json()
        except Exception:
            return []

    @staticmethod
    def get_unapproved_krog_list():
        try:
            return requests.get("%s/find/all/unapproved" % backend_url).json()
        except Exception:
            return []
