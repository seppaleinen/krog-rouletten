# coding=UTF-8
import sys, mock, os, time, requests_mock, json
from os import path
from application import views, app, model
from tests import STATUS_405, STATUS_200, STATUS_404
from flask_testing import TestCase
sys.path.append( path.dirname( path.dirname( path.abspath(__file__) ) ) )

def mocked_requests_get(*args, **kwargs):
    class MockResponse:
        def __init__(self, json_data, status_code):
            self.json_data = json_data
            self.status_code = status_code

        def json(self):
            return self.json_data

    _file = os.path.join(os.path.dirname(__file__), 'google_details.txt')
    with open(_file, 'r') as content_file:
        google_details_content = content_file.read()

    _file = os.path.join(os.path.dirname(__file__), 'google_search.txt')
    with open(_file, 'r') as content_file:
        google_search_content = content_file.read()

    if 'https://maps.googleapis.com/maps/api/place/nearbysearch/' in args[0]:
        return MockResponse(json.loads(google_search_content), 200)
    else:
        return MockResponse(json.loads(google_details_content), 200)


class UnitTests(TestCase):
    def create_app(self):
        app = views.app
        app.config['TESTING'] = True
        app.config['PRESERVE_CONTEXT_ON_EXCEPTION'] = False
        return app

    def test_index_page(self):
        result = self.client.get('/')
        self.assertEquals(STATUS_200, result.status)
        self.assert_template_used('index.html')

    def test_settings_page(self):
        result = self.client.get('/settings')
        self.assertEquals(STATUS_200, result.status)
        self.assert_template_used('settings.html')
        self.assertTrue('Sök inställningar' in result.data.decode('utf-8'))

    def test_error_page(self):
        result = self.client.get('/error/error_msg')
        self.assertEquals(STATUS_200, result.status)
        self.assert_template_used('error.html')
        page = result.data.decode('utf-8')
        self.assertTrue('error_msg' in page)

    @mock.patch('application.logic.requests.get', side_effect=mocked_requests_get)
    def test_details_page_without_location(self, mocked_get):
        result = self.client.get('/details/place_id')
        self.assertEquals(STATUS_200, result.status)
        self.assert_template_used('krog.html')
        page = result.data.decode('utf-8')
        self.assertTrue('Berns' in page, page)
        self.assertTrue('Näckströmsgatan 8, 111 47 Stockholm, Sweden' in page, page)
        self.assertTrue('Bonyo Buogha' in page)

        mocked_get.assert_called_with(u'https://maps.googleapis.com/maps/api/place/details/json?placeid=place_id&language=sv&key=None')

    @mock.patch('application.logic.requests.get', side_effect=mocked_requests_get)
    def test_details_page_with_location(self, mocked_get):
        result = self.client.get('/details/place_id/1.2,1.3')
        self.assertEquals(STATUS_200, result.status)
        self.assert_template_used('krog.html')
        page = result.data.decode('utf-8')
        self.assertTrue('Berns' in page, page)
        self.assertTrue('Näckströmsgatan 8, 111 47 Stockholm, Sweden' in page)
        self.assertTrue('Bonyo Buogha' in page, page)

        mocked_get.assert_called_with(u'https://maps.googleapis.com/maps/api/place/details/json?placeid=place_id&language=sv&key=None')

    @mock.patch('application.logic.requests.get', side_effect=mocked_requests_get)
    def test_search_gps(self, mocked_get):
        result = self.client.post(
            '/krog/random',
            data = dict(latitude="1.1", longitude="2.2", searchtype="gps"),
            follow_redirects=False)

        self.assertEquals(302, result.status_code)

        mocked_get.assert_called_with(u'https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=1.1,2.2&radius=500&type=bar&key=None')

    @mock.patch('application.logic.requests.get', side_effect=mocked_requests_get)
    def test_search_lista(self, mocked_get):
        result = self.client.post(
            '/krog/random',
            data = dict(latitude="1.1", longitude="2.2", searchtype="list"),
            follow_redirects=False)

        self.assertEquals(200, result.status_code)
        self.assert_template_used('lista.html')

        mocked_get.assert_called_with(u'https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=1.1,2.2&radius=500&type=bar&key=None')

    def test_form(self):
        form = model.SearchForm()
        form.longitude.searchtype = 'gps'
        form.longitude.data = '1.1'
        form.latitude.data = '1.1'
        form.bar_typ.data='bar'
        form.distance.data='500'
        self.assertTrue(form.validate(), form.errors)

