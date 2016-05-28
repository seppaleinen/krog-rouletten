import random, requests
from flask import render_template, request, redirect, url_for
from application.model import ManualForm, Objekt
from flask.ext import excel


def mocked_random():
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

    return random.choice(list)


def home(backend_url):
    if 'localhost2' in backend_url:
        return render_template('index.html', data=mocked_random())
    else:
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


def submit_input(backend_url):
    form = ManualForm(request.form)
    if request.method == 'POST' and form.validate():
        requests.post(backend_url + '/save', json=form.data)
        return render_template('admin.html', form=form, closePopup='close')
    else:
        return render_template('krog_popup.html', form=form, closePopup=None)


def update(backend_url):
    form = ManualForm(request.form)
    if request.method == 'POST':
        requests.post(backend_url + '/update', json=form.data)
    return redirect(url_for('admin'))


def export_csv(backend_url):
    kroglist = requests.get(backend_url + '/export/csv').json()
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


def popup():
    return render_template('krog_popup.html', form=ManualForm())


def get_krog_list(backend_url):
    return requests.get(backend_url + '/find/all').json()
