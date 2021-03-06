# coding=UTF-8
import random, requests, json, urllib, os
from flask import render_template, request, redirect, url_for, jsonify, session, redirect, url_for
from application.model import SearchForm, Krog, Review
from haversine import haversine

API_KEY = os.getenv('MAPS_API_KEY')
MAPS_EMBED_KEY = 'AIzaSyDMtS6rg17-Tr2neNR0b0RSgrF5RxmfUhQ'
GOOGLE_SEARCH = 'https://maps.googleapis.com/maps/api/place/nearbysearch/json?%s&key=%s'
GOOGLE_DETAILS = 'https://maps.googleapis.com/maps/api/place/details/json?placeid=%s&language=sv&key=%s'
GOOGLE_GEOCODE = 'http://maps.googleapis.com/maps/api/geocode/json?address=%s&sensor=false'
#Should either be this map or one with directions
GOOGLE_EMBEDDED_MAPS = 'https://www.google.com/maps/embed/v1/place?q=place_id:%s&key=%s'
GOOGLE_EMBEDDED_DIRECTIONS_MAPS = 'https://www.google.com/maps/embed/v1/directions?mode=%s&origin=%s&destination=place_id:%s&key=%s'
GOOGLE_PLACES_PHOTO = 'https://maps.googleapis.com/maps/api/place/photo?maxheight=414&photoreference=%s&key=%s'
GOOGLE_PLACES_LIST_PHOTO = 'https://maps.googleapis.com/maps/api/place/photo?maxwidth=340&photoreference=%s&key=%s'


def home():
    Helper().init_session()
    return render_template('index.html', **Helper().forms({'searchForm': SearchForm(request.form)}))


def random_page():
    print("EARLIER: %s" % Helper().init_session())
    form = SearchForm(request.form)
    print("FORMDATA: %s" % form.data)
    if form and form.searchtype.data == 'gps':
        try:
            place_id = filter_search_from_previous_results(form)

            Helper().init_session()
            session[Helper().get_user_ip()] += place_id + ';'

            return redirect('/details/' + place_id + '/' + form.latitude.data + ',' + form.longitude.data, 302)
        except Exception as e:
            print("EXCEPTION: %s" % e)
            return render_template('error.html', data='Hittade ingen krog på din sökning', **Helper().forms())
    elif form and form.searchtype.data == 'list':
        Helper().init_session()

        search_response = search_google_and_broaden_if_no_results(form)
        krog_lista = []
        for result in search_response['results']:
            dist = Helper.calculate_distance_between_locations(
                form.latitude.data,
                form.longitude.data,
                result['geometry']['location']['lat'],
                result['geometry']['location']['lng'])

            photo = None
            try:
                for photo_ref in result['photos']:
                    photo = GOOGLE_PLACES_LIST_PHOTO % (photo_ref['photo_reference'], API_KEY)
            except Exception:
                photo = '/static/img/bg2.jpeg'

            krog_lista.append(Krog(
                namn=result['name'],
                bar_types='',
                beskrivning='',
                adress=result['vicinity'],
                oppet_tider='',
                iframe_lank='',
                betyg='',
                reviews='',
                photos=photo,
                distance=dist,
                place_id=result['place_id']
            ))

        return render_template('lista.html', data=sorted(krog_lista, key=lambda x: x.distance), **Helper().forms({'searchForm': form}))
    elif form and ',' in form.stadsdel.data:
        try:
            form.latitude.data = form.stadsdel.data.split(',')[0]
            form.longitude.data = form.stadsdel.data.split(',')[1]
            print("FORM2: %s" % form.data)
            place_id = filter_search_from_previous_results(form)

            Helper().init_session()
            session[Helper().get_user_ip()] += place_id + ';'

            return redirect('/details/' + place_id + '/' + form.latitude.data + ',' + form.longitude.data, 302)
        except Exception as e:
            print("EXCEPTION: %s" % e)
            return render_template('error.html', data='Hittade ingen krog på din sökning', **Helper().forms())

    # Hell has frozen over
    return render_template('error.html', data='Nånting gick fel', **Helper().forms())


def search_google_and_broaden_if_no_results(form):
    distance = form.hidden_distance.data if form.hidden_distance.data else form.distance.data
    search_params = ''
    search_params += 'location=' + str(form.latitude.data) + ',' + str(form.longitude.data) + '&'
    search_params += 'radius=' + str(distance) + '&'
    search_params += 'type=bar'
    search_response = requests.get(GOOGLE_SEARCH % (search_params, API_KEY)).json()
    print("API_KEY: %s" % API_KEY)

    print("SEARCHRESPONSE: %s" % search_response)

    if search_response['status'] == 'OK' and search_response['results']:
        return search_response
    elif search_response['status'] == 'ZERO_RESULTS': # If no results, increase distance and search again
        form.distance.data = int(form.distance.data) + 500
        return search_google_and_broaden_if_no_results(form)
    else:
        raise Exception("Wrong statuscode: %s" % search_response['status'])


def get_details_response_from_google(place_id, location=None):
    krog = None

    if place_id:
        details_response = requests.get(GOOGLE_DETAILS % (place_id, API_KEY)).json()

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

        dist = None
        if location:
            lat=location.split(',')[0]
            lng=location.split(',')[1]

            dist = Helper.calculate_distance_between_locations(
                lat,
                lng,
                details_response['result']['geometry']['location']['lat'],
                details_response['result']['geometry']['location']['lng'])
            mode = 'transit' if 'km' in dist else 'walking'
            iframe_lank = (GOOGLE_EMBEDDED_DIRECTIONS_MAPS % (mode, lat + ',' + lng,place_id, MAPS_EMBED_KEY))
        else:
            iframe_lank = (GOOGLE_EMBEDDED_MAPS % (place_id, MAPS_EMBED_KEY))

        krog = Krog(
            namn=details_response['result']['name'],
            bar_types=bar_types,
            beskrivning=details_response['result']['name'],
            adress=details_response['result']['formatted_address'],
            oppet_tider=opening_hours,
            iframe_lank=iframe_lank,
            betyg=rating,
            reviews=reviews,
            photos=photos,
            place_id=details_response['result']['place_id'],
            distance=dist
        )
    return krog


def filter_search_from_previous_results(form):
    search_response = search_google_and_broaden_if_no_results(form)

    # Remove earlier search results from new search to remove duplicates
    result_list_without_earlier = []
    for result in search_response['results']:
        if not result['place_id'] in session[Helper().get_user_ip()].split(';'):
            result_list_without_earlier.append(result)

    # If there are any results after trimming duplicates, else redo with duplicates
    if result_list_without_earlier:
        random_search_response = random.choice(result_list_without_earlier)
    else:
        print("Already been through all")
        random_search_response = random.choice(search_response['results'])

    return random_search_response['place_id']


def admin():
    return render_template('admin.html', kroglista=[], **Helper().forms())


def settings():
    return render_template('settings.html', **Helper().forms({'searchForm': SearchForm(request.form)}))


def error(error_msg):
    return render_template('error.html', data=error_msg, **Helper().forms())


def details(place_id, location):
    krog = get_details_response_from_google(place_id, location)
    return render_template('krog.html', data=krog, **Helper().forms())


class Helper(object):
    @staticmethod
    def forms(kwargs={}):
        forms = {'searchForm': SearchForm()}
        forms.update(kwargs)
        return forms

    @staticmethod
    def get_user_ip():
        # Try to get HTTP_X_REAL_IP, if not available,
        # try get environment variable REMOTE_ADDR else get variable remote_addr
        # Due to different levels of proxies.
        return request.environ.get('HTTP_X_REAL_IP', request.environ.get('REMOTE_ADDR', request.remote_addr))

    @staticmethod
    def init_session():
        user_ip = Helper().get_user_ip()
        if user_ip not in session:
            session[user_ip] = ''
        return session[user_ip]

    @staticmethod
    def calculate_distance_between_locations(user_lat, user_lng, bar_lat, bar_lng):
        user_loc = (float(user_lat), float(user_lng))
        bar_loc = (float(bar_lat), float(bar_lng))
        # Calculates distance between points rounded down to 1 decimal in km
        distance_in_km = "{0:.1f}".format(haversine(user_loc, bar_loc))
        # Returns e.g km if over one 1 km distance, otherwise in meters
        return distance_in_km + 'km' if float(distance_in_km) > 1 else str((float(distance_in_km) * 1000)) + 'm'



