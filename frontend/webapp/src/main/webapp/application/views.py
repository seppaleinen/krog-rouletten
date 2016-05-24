# -*- coding: utf-8 -*-
from application import app
from flask import render_template, request, Response, jsonify
import random, requests, json
from wtforms import Form, TextField
import os


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
    kellys.karta = 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2036.1076553101873!2d18.072471615934845!3d59.314459281653626!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x465f77fa6e38fd49%3A0x76f561b83359a005!2sKellys!5e0!3m2!1sen!2sse!4v1456504153773'
    kellys.beskrivning = 'Rockbar'

    arken = Objekt()
    arken.namn = 'Gota ark'
    arken.adress = 'Medborgarplatsen 25, 118 72 Stockholm'
    arken.karta = 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2036.0389246761372!2d18.069153315934884!3d59.315606981654064!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x465f77fabedd14f1%3A0x5f2231305d9933ed!2zR8O2dGEgQXJr!5e0!3m2!1sen!2sse!4v1456513979498'
    arken.beskrivning = 'Sunkhak'

    list = [kellys, arken]

    objekt = random.choice(list)
    return render_template('index.html', data=objekt)


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
    namn = TextField('Namn')
    adress = TextField('Adress')
    oppetTider = TextField('Oppettider')
    barTyp = TextField('Bartyp')
    stadsdel = TextField('Stadsdel')
    beskrivning = TextField('Beskrivning')
    betyg = TextField('Betyg')
    hemsideLank = TextField('Hemside Lank')
    intrade = TextField('Intrade')
    iframeLank = TextField('Iframe lank')


@app.route('/admin/submit', methods=['POST'])
def submitInput():
    form = ManualForm(request.form)
    if request.method == 'POST':
        requests.post(BACKEND + '/save', json=form.data)
    return render_template('admin.html', form=form, kroglista=getKrogList())