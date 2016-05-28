import sys, urllib2, mock, os
from os import path
sys.path.append( path.dirname( path.dirname( path.abspath(__file__) ) ) )
from application import views, app
from tests import unittest, STATUS_405, STATUS_200, STATUS_404
from flask import url_for, Flask
from flask_testing import TestCase


class HomeUnitTests(unittest.TestCase):
    def setUp(self):
        views.app.config['TESTING'] = True
        self.app = views.app.test_client()
        with views.app.test_request_context():
            self.url = url_for('home')

    def tearDown(self):
        self.app = None

    @mock.patch('application.logic.requests')
    def test_home_get(self, mocked):
        os.environ['BACKEND_URL'] = 'http://localhost:10080'
        result = self.app.get(self.url)
        #self.assertEquals(result.json, dict(success=True))
        self.assertEquals(STATUS_200, result.status)
        self.assertTrue('class="logo shadow">Krogrouletten</div>' in result.data)
        mocked.get.assert_called_with('http://localhost:10080/find/random')

    def test_home_post_not_allowed(self):
        result = self.app.post(self.url)
        self.assertEquals(STATUS_405, result.status)
        #self.assertEquals(result.json, dict(success=True))

    def test_home_put_not_allowed(self):
        result = self.app.put(self.url)
        self.assertEquals(STATUS_405, result.status)

    def test_home_delete_not_allowed(self):
        result = self.app.delete(self.url)
        self.assertEquals(STATUS_405, result.status)


class AdminUnitTests(unittest.TestCase):
    def setUp(self):
        os.environ['BACKEND_URL'] = 'http://localhost:10080'
        views.app.config['TESTING'] = True
        self.app = views.app.test_client()

    def tearDown(self):
        self.app = None

    @mock.patch('application.logic.requests')
    def test_admin_get(self, mocked):
        os.environ['BACKEND_URL'] = 'http://localhost:10080'
        result = self.app.get('/admin')
        self.assertEquals(STATUS_200, result.status)
        self.assertTrue('class="logo shadow">Krogrouletten</div>' in result.data)
        self.assertTrue('Ny krog' in result.data)
        mocked.get.assert_called_with('http://localhost:10080/find/all')


class PopupUnitTests(unittest.TestCase):
    def setUp(self):
        os.environ['BACKEND_URL'] = 'http://localhost:10080'
        views.app.config['TESTING'] = True
        self.app = views.app.test_client()

    def tearDown(self):
        self.app = None

    def test_popup_get(self):
        result = self.app.get('/admin/popup')
        self.assertEquals(STATUS_200, result.status)
        self.assertFalse('class="logo shadow">Krogrouletten</div>' in result.data)
        self.assertTrue('Skicka' in result.data)
