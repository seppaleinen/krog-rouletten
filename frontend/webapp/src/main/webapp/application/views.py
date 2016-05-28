# -*- coding: utf-8 -*-
from application import app
from flask import render_template, request, Response, jsonify, redirect, url_for, make_response
import random, requests, json
from wtforms import Form, TextField, validators
import os
from flask.ext import excel


BACKEND = os.getenv('BACKEND_URL', 'http://localhost:10080')


class Objekt(object):
    def __init__(self):
        self.namn = None
        self.adress = None
        self.karta = None
        self.beskrivning = None


@app.route('/')
def home():
    kellys = Objekt()
    kellys.namn = 'Kellys'
    kellys.adress = 'Folkungagatan 49, 116 22 Stockholm'
    kellys.iframe_lank = 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2036.1076553101873!2d18.072471615934845!3d59.314459281653626!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x465f77fa6e38fd49%3A0x76f561b83359a005!2sKellys!5e0!3m2!1sen!2sse!4v1456504153773'
    kellys.beskrivning = 'Rockbar'

    arken = Objekt()
    arken.namn = 'Gota ark'
    arken.adress = 'Medborgarplatsen 25, 118 72 Stockholm'
    arken.iframe_lank = 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2036.0389246761372!2d18.069153315934884!3d59.315606981654064!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x465f77fabedd14f1%3A0x5f2231305d9933ed!2zR8O2dGEgQXJr!5e0!3m2!1sen!2sse!4v1456513979498'
    arken.beskrivning = 'Sunkhak'

    list = [kellys, arken]

    objekt = random.choice(list)

    if 'localhost2' in BACKEND:
        return render_template('index.html', data=objekt)
    else:
        krog = requests.get(BACKEND + '/find/random').json()
        return render_template('index.html', data=krog)


@app.route('/admin')
def admin():
    return render_template('admin.html', form=ManualForm(), kroglista=getKrogList())


@app.route('/admin/uploadFile', methods=['POST'])
def uploadCsv():
    file = request.files['file']
    if file and '.csv' in file.filename:
        print(file.readable())
        file_ = {'file': ('file', file)}
        response = requests.post(BACKEND + '/save/csv', files=file_)
        data = response.content.decode('utf-8')
        #session[request.environ['REMOTE_ADDR']] = data
        return render_template('admin.html', form=ManualForm(request.form), kroglista=getKrogList())
    else:
        return render_template('admin.html', form=ManualForm(request.form), kroglista=getKrogList())


def getKrogList():
    return requests.get(BACKEND + '/find/all').json()


class ManualForm(Form):
    namn = TextField('Namn', [validators.Required()])
    adress = TextField('Adress', [validators.Required()])
    oppetTider = TextField('Oppettider')
    barTyp = TextField('Bartyp')
    stadsdel = TextField('Stadsdel')
    beskrivning = TextField('Beskrivning')
    betyg = TextField('Betyg')
    hemside_lank = TextField('Hemside Lank')
    intrade = TextField('Intrade')
    iframe_lank = TextField('Iframe lank', [validators.Required()])


@app.route('/admin/submit', methods=['POST'])
def submitInput():
    form = ManualForm(request.form)
    if request.method == 'POST' and form.validate():
        requests.post(BACKEND + '/save', json=form.data)
        return render_template('admin.html', form=form, closePopup='close')
    else:
        return render_template('krog_popup.html', form=form, closePopup=None)

@app.route('/admin/submit', methods=['PUT'])
def update():
    form = ManualForm(request.form)
    if request.method == 'POST':
        requests.post(BACKEND + '/update', json=form.data)
    return redirect(url_for('admin'))
    #return render_template('admin.html', form=form, kroglista=getKrogList())


@app.route('/admin/export', methods=['GET'])
def exportCSV():
    kroglist = requests.get(BACKEND + '/export/csv').json()
    data = [
        ["namn","adress","oppet_tider","bar_typ", 'stadsdel', 'beskrivning', 'betyg', 'hemside_lank', 'intrade', 'iframe_lank']
    ]
    for krog in kroglist:
        print(krog)
        data.append([krog['namn'],
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

@app.route('/admin/popup', methods=['GET'])
def popup():
    return render_template('krog_popup.html', form=ManualForm())
