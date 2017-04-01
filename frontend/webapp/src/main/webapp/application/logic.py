# coding=UTF-8
import random, requests, json, urllib, os
from flask import render_template, request, redirect, url_for, jsonify, session
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
GOOGLE_PLACES_PHOTO = 'https://maps.googleapis.com/maps/api/place/photo?maxheight=414&photoreference=%s&key=%s'


def home():
    Helper().init_session()
    return render_template('index.html', **Helper().forms({'searchForm': SearchForm(request.form)}))


def random_page():
    print("EARLIER: %s" % Helper().init_session())
    form = SearchForm(request.form)
    print("FORMDATA: %s" % form.data)
    if form and form.searchtype.data == 'gps':
        print("GPS")
        try:
            krog = get_result_from_google(form)

            Helper().init_session()
            session[Helper().get_user_ip()] += krog.namn + ';'

            return render_template('krog.html', data=krog, **Helper().forms({'searchForm': form}))
        except Exception as e:
            print("EXCEPTION: %s" % e)
            return render_template('error.html', data='Hittade ingen krog på din sökning', **Helper().forms())
    elif form and form.searchtype.data == 'list':
        Helper().init_session()

        search_response = get_search_response_from_google(form)
        krog_lista = []
        for result in search_response['results']:
            krog_lista.append(Krog(
                namn=result['name'],
                bar_types='',
                beskrivning='',
                adress=result['vicinity'],
                oppet_tider='',
                iframe_lank='',
                betyg='',
                reviews='',
                photos='',
                place_id=result['place_id']
            ))
        return render_template('lista.html', data=krog_lista, **Helper().forms({'searchForm': form}))
    elif form and form.adress.data:
        print("ADRESS")
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
                krog = get_result_from_google(form)

                Helper().init_session()
                session[Helper().get_user_ip()] += krog.namn + ';'

                return render_template('krog.html', data=krog, **Helper().forms({'searchForm': form}))
            except Exception as e:
                print("EXCEPTION: %s" % e)
                return render_template('error.html', data='Hittade ingen krog på din sökning', **Helper().forms())
    elif form and ',' in form.stadsdel.data:
        print("HEJ: " + form.stadsdel.data)
        try:
            form.latitude.data = form.stadsdel.data.split(',')[0]
            form.longitude.data = form.stadsdel.data.split(',')[1]
            print("FORM2: %s" % form.data)
            krog = get_result_from_google(form)

            Helper().init_session()
            session[Helper().get_user_ip()] += krog.namn + ';'

            return render_template('krog.html', data=krog, **Helper().forms({'searchForm': form}))
        except Exception as e:
            print("EXCEPTION: %s" % e)
            return render_template('error.html', data='Hittade ingen krog på din sökning', **Helper().forms())

    #Hell has frozen over
    return render_template('error.html', data='Nånting gick fel', **Helper().forms())


def get_search_response_from_google(form):
    search_params = ''
    search_params += 'location=' + str(form.latitude.data) + ',' + str(form.longitude.data) + '&'
    search_params += 'radius=' + str(form.distance.data) + '&'
    search_params += 'type=bar'
    search_response = requests.get(GOOGLE_SEARCH % (search_params, API_KEY)).json()
    print("API_KEY: %s" % API_KEY)

    print("SEARCHRESPONSE: %s" % search_response)

    if search_response['status'] != 'OK' or not search_response['results']:
        raise Exception("No results or wrong statuscode: %s" % search_response['status'])
    else:
        return search_response


def get_details_response_from_google(place_id):
    krog = None

    if place_id:
        details_response = requests.get(GOOGLE_DETAILS % (place_id, API_KEY)).json()

        print("DETAILS %s" % details_response)

        reviews = []
        try:
            for review in details_response['result']['reviews']:
                if review['text']:
                    reviews.append(Review(author_name=review['author_name'], comment=review['text']))
        except Exception:
            print("No reviews.. Ignoring")

        photos = []
        try:
            for photo in details_response['result']['photos']:
                photos.append(GOOGLE_PLACES_PHOTO % (photo['photo_reference'], API_KEY))
        except Exception:
            photos.append('/static/img/bg2.jpeg')

        bar_types = details_response['result']['types']
        # Remove point_of_interest and establishment from bartypes, as they're not really that interesting..
        if 'point_of_interest' in bar_types: bar_types.remove('point_of_interest')
        if 'establishment' in bar_types: bar_types.remove('establishment')

        try:
            opening_hours = details_response['result']['opening_hours']['weekday_text']
        except Exception:
            opening_hours = []

        try:
            rating = details_response['result']['rating']
        except Exception:
            rating = "N/A"

        krog = Krog(
            namn=details_response['result']['name'],
            bar_types=bar_types,
            beskrivning=details_response['result']['name'],
            adress=details_response['result']['formatted_address'],
            oppet_tider=opening_hours,
            iframe_lank=(GOOGLE_EMBEDDED_MAPS % (place_id, MAPS_EMBED_KEY)),
            betyg=rating,
            reviews=reviews,
            photos=photos
        )
    return krog


def get_result_from_google(form):
    search_response = get_search_response_from_google(form)

    # Remove earlier search results from new search to remove duplicates
    result_list_without_earlier = []
    for result in search_response['results']:
        if not result['name'] in session[Helper().get_user_ip()].split(';'):
            result_list_without_earlier.append(result)

    # If there are any results after trimming duplicates, else redo with duplicates
    if result_list_without_earlier:
        random_search_response = random.choice(result_list_without_earlier)
    else:
        print("Already been through all")
        random_search_response = random.choice(search_response['results'])

    details_params = random_search_response['place_id']

    return get_details_response_from_google(details_params)


def admin():
    return render_template('admin.html', kroglista=Helper.get_krog_list(), **Helper().forms())


def settings():
    form = SearchForm(request.form)
    print("FORMDATA: %s" % form.data)

    return render_template('settings.html', **Helper().forms({'searchForm': form}))


def unapproved():
    return render_template('admin.html', kroglista=Helper.get_unapproved_krog_list(), **Helper().forms())


def upload_csv():
    csv_file = request.files['file']
    if csv_file and '.csv' in csv_file.filename:
        file_ = {'file': ('file', csv_file)}
        try:
            requests.post(backend_url + '/save/csv', files=file_)
            return render_template('admin.html', kroglista=Helper.get_krog_list(), **Helper().forms(
                {'adminKrogForm': AdminKrogForm(request.form)})
            )
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


def details(place_id):
    krog = get_details_response_from_google(place_id)
    return render_template('krog.html', data=krog, **Helper().forms())


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

    @staticmethod
    def get_user_ip():
        # Try to get HTTP_X_REAL_IP, if not available, try get environment variable REMOTE_ADDR else get variable remote_addr
        # Due to different levels of proxies.
        return request.environ.get('HTTP_X_REAL_IP', request.environ.get('REMOTE_ADDR', request.remote_addr))

    @staticmethod
    def init_session():
        user_ip = Helper().get_user_ip()
        if user_ip not in session:
            session[user_ip] = ''
        return session[user_ip]


