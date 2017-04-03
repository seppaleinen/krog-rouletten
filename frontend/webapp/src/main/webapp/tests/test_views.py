# coding=UTF-8
import sys, urllib2, mock, os, time, requests_mock, json
from os import path
from application import views, app
from tests import STATUS_405, STATUS_200, STATUS_404
from flask_testing import TestCase
sys.path.append( path.dirname( path.dirname( path.abspath(__file__) ) ) )


class UnitTests(TestCase):
    def create_app(self):
        app = views.app
        app.config['TESTING'] = True
        return app

    def test_index_page(self):
        result = self.client.get('/')
        self.assertEquals(STATUS_200, result.status)
        self.assert_template_used('index.html')

    @mock.patch('application.logic.requests')
    def test_details_page_without_location(self, mocked):
        file = os.path.join(os.path.dirname(__file__), 'google_details.txt')
        with open(file, 'r') as content_file:
            content = content_file.read()

        mocked.return_value = mock.MagicMock(status_code=200, response=json.dumps(content))

        result = self.client.get('/details/place_id')
        self.assertEquals(STATUS_200, result.status)
        self.assert_template_used('krog.html')
        # self.assertTrue('Berns' in result.data, result.data)
        # self.assertTrue('Näckströmsgatan 8, Stockholm' in result.data)
        mocked.get.assert_called_with(u'https://maps.googleapis.com/maps/api/place/details/json?placeid=place_id&language=sv&key=AIzaSyBlK6_BqAG_JDwcuyBBt1xL9jIpRMYIb8M')

    @mock.patch('application.logic.requests')
    def test_details_page_with_location(self, mocked):
        result = self.client.get('/details/place_id/1.2,1.3')
        self.assertEquals(STATUS_200, result.status)
        self.assert_template_used('krog.html')
        # self.assertEquals('asd', result.data)
        mocked.get.assert_called_with(u'https://maps.googleapis.com/maps/api/place/details/json?placeid=place_id&language=sv&key=AIzaSyBlK6_BqAG_JDwcuyBBt1xL9jIpRMYIb8M')

