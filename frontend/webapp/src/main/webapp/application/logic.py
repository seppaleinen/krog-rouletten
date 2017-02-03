# coding=UTF-8
import random, requests, json, urllib, os
from flask import render_template, request, redirect, url_for, jsonify
from application.model import AdminKrogForm, SearchForm, UserKrogForm, Krog, Review
from flask.ext import excel

backend_url = os.getenv('BACKEND_URL', 'http://localhost:10080')
API_KEY = os.getenv('MAPS_API_KEY')
MAPS_EMBED_KEY = 'AIzaSyDMtS6rg17-Tr2neNR0b0RSgrF5RxmfUhQ'
GOOGLE_SEARCH = 'https://maps.googleapis.com/maps/api/place/nearbysearch/json?%s&key=%s'
GOOGLE_DETAILS = 'https://maps.googleapis.com/maps/api/place/details/json?placeid=%s&key=%s'
GOOGLE_GEOCODE = 'http://maps.googleapis.com/maps/api/geocode/json?address=%s&sensor=false'
#Should either be this map or one with directions
GOOGLE_EMBEDDED_MAPS = 'https://www.google.com/maps/embed/v1/place?q=place_id:%s&key=%s'
GOOGLE_PLACES_PHOTO = 'https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photoreference=%s&key=%s'


def home():
    return render_template('index.html', **Helper().forms())


def random_page():
    form = SearchForm(request.form)
    print("FORMDATA: %s" % form.data)
    if form and not form.adress.data:
        try:
            krog = get_result_from_google(form.latitude.data, form.longitude.data, form.distance.data)

            return render_template('krog.html', data=krog, **Helper().forms({'searchForm':form}))
        except ValueError:
            return render_template('error.html', data='Hittade ingen krog på din sökning', **Helper().forms())
    elif form and form.adress.data:
        try:
            adress = urllib.quote(form.adress.data.encode('utf8'))
        except AttributeError: #if urllib.quote doesn't exist, it's python3, try urllib.parse.quote
            adress = urllib.parse.quote(form.adress.data.encode('utf8'))
        result = requests.get(GOOGLE_GEOCODE % adress)
        if result.status_code == 200:
            lat = result.json()

            # I'm a lazy and inefficient bastard.. Take the last location from list
            for place in lat['results']:
                form.adress.data = place['formatted_address']
                form.latitude.data = place['geometry']['location']['lat']
                form.longitude.data = place['geometry']['location']['lng']
                print("ADRESS:%s LAT:%s LNG:%s" % (form.adress.data, form.latitude.data, form.longitude.data))

            try:
                krog = get_result_from_google(form.latitude.data, form.longitude.data, form.distance.data)
                return render_template('krog.html', data=krog, **Helper().forms({'searchForm':form}))
            except Exception as e:
                print("EXCEPTION: %s" % e)
                return render_template('error.html', data='Hittade ingen krog på din sökning', **Helper().forms())
    #Hell has frozen over
    return render_template('error.html', data='Nånting gick fel', **Helper().forms())


def get_result_from_google(lat, lng, distance):
    search_params = ''
    search_params += 'location=' + str(lat) + ',' + str(lng) + '&'
    search_params += 'radius=' + str(distance) + '&'
    search_params += 'type=bar'
    search_response = requests.get(GOOGLE_SEARCH % (search_params, API_KEY)).json()

    krog = None
    if not search_response['results']:
        raise Exception('No results')

    random_search_response = random.choice(search_response['results'])
    details_params = random_search_response['place_id']

    # print("RANDOM %s" % random_search_response)

    if details_params:
        details_response = requests.get(GOOGLE_DETAILS % (details_params, API_KEY)).json()

        # print("DETAILS %s" % details_response)

        reviews = []
        for review in details_response['result']['reviews']:
            if review['text']:
                reviews.append(Review(author_name=review['author_name'], comment=review['text']))

        krog = Krog(
            namn=details_response['result']['name'],
            bar_types=details_response['result']['types'],
            beskrivning='beskrivning',
            adress=details_response['result']['formatted_address'],
            oppet_tider='öppet',
            iframe_lank=(GOOGLE_EMBEDDED_MAPS % (details_params, MAPS_EMBED_KEY)),
            betyg=details_response['result']['rating'],
            reviews=reviews
        )

    return krog


def admin():
    return render_template('admin.html', kroglista=Helper.get_krog_list(), **Helper().forms())


def settings():
    return render_template('settings.html', **Helper().forms())


def unapproved():
    return render_template('admin.html', kroglista=Helper.get_unapproved_krog_list(), **Helper().forms())


def upload_csv():
    file = request.files['file']
    if file and '.csv' in file.filename:
        file_ = {'file': ('file', file)}
        try:
            requests.post(backend_url + '/save/csv', files=file_)
            return render_template('admin.html', kroglista=Helper.get_krog_list(), **Helper().forms({'adminKrogForm': AdminKrogForm(request.form)}))
        except Exception:
            return render_template('error.html', data='Nånting gick fel', **Helper().forms())
    else:
        return render_template('admin.html', kroglista=Helper.get_krog_list(), **Helper().forms({'adminKrogForm': AdminKrogForm(request.form)}))


def save_krog():
    form = AdminKrogForm(request.form)
    if request.method == 'POST' and form.validate():
        #If admin saves krog, automatically approve..
        form.approved.data = True
        requests.post(backend_url + '/save', json=form.data)
    return render_template('admin.html', kroglista=Helper.get_krog_list(), **Helper().forms({'adminKrogForm':form}))


def update():
    form = AdminKrogForm(request.form)
    if request.form.get('update'):
        if request.method == 'POST':
            form.approved.data = True
            requests.post(backend_url + '/update', json=form.data)
        return redirect(url_for('admin'))
    elif request.form.get('delete'):
        requests.delete(backend_url + '/delete/krog', json=form.data)
        return redirect(url_for('admin'))
    elif request.form.get('approve'):
        form.approved.data = True
        requests.post(backend_url + '/update', json=form.data)
        return redirect(url_for('unapproved'))


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


def profile():
    return render_template('profile.html', **Helper().forms())


def error(error_msg):
    return render_template('error.html', data=error_msg, **Helper().forms())


def user_krog_save():
    userKrogForm = UserKrogForm(request.form)
    # Either form is valid, then close popup,
    # otherwise re-render popup and keep open
    print("Saving krog %s" % userKrogForm.data)
    if request.method == 'POST' and userKrogForm.validate():
        print('POST')
        try:
            userKrogForm.approved.data = False
            requests.post(backend_url + '/save', json=userKrogForm.data)
        except Exception:
            pass
        return render_template('index.html', **Helper().forms({'userKrogForm': userKrogForm}))
    else:
        return render_template('index.html', **Helper().forms({'userKrogForm': userKrogForm}))


class Helper(object):
    @staticmethod
    def forms(kwargs={}):
        forms = {'searchForm': SearchForm(), 'userKrogForm': UserKrogForm(), 'adminKrogForm': AdminKrogForm()}
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
