import sys, urllib2, mock, os
from os import path
sys.path.append( path.dirname( path.dirname( path.abspath(__file__) ) ) )
from application import views, app
from tests import STATUS_405, STATUS_200, STATUS_404
from flask_testing import TestCase


class HomeUnitTests(TestCase):
    def create_app(self):
        app = views.app
        os.environ['BACKEND_URL'] = 'http://localhost:10080'
        app.config['TESTING'] = True
        return app

    @mock.patch('application.logic.requests')
    def test_home_get(self, mocked):
        os.environ['BACKEND_URL'] = 'http://localhost:10080'
        result = self.client.get('/')
        self.assertEquals(STATUS_200, result.status)
        self.assertTrue('class="logo lobster"><a href="/">Krogrouletten</a></div>' in result.data)
        self.assert_template_used('index.html')

    def test_home_post_not_allowed(self):
        result = self.client.post('/')
        self.assertEquals(STATUS_405, result.status)

    def test_home_put_not_allowed(self):
        result = self.client.put('/')
        self.assertEquals(STATUS_405, result.status)

    def test_home_delete_not_allowed(self):
        result = self.client.delete('/')
        self.assertEquals(STATUS_405, result.status)


class KrogUnitTests(TestCase):
    def create_app(self):
        app = views.app
        os.environ['BACKEND_URL'] = 'http://localhost:10080'
        app.config['TESTING'] = True
        return app

    @mock.patch('application.logic.requests')
    def test_home_get(self, mocked):
        os.environ['BACKEND_URL'] = 'http://localhost:10080'
        result = self.client.get('/krog/random')
        self.assertEquals(STATUS_200, result.status)
        self.assertTrue('class="logo lobster"><a href="/">Krogrouletten</a></div>' in result.data)
        mocked.get.assert_called_with('http://localhost:10080/find/random', params={'location': 'value1'})
        self.assert_template_used('krog.html')

    def test_home_post_not_allowed(self):
        result = self.client.post('/')
        self.assertEquals(STATUS_405, result.status)

    def test_home_put_not_allowed(self):
        result = self.client.put('/')
        self.assertEquals(STATUS_405, result.status)

    def test_home_delete_not_allowed(self):
        result = self.client.delete('/')
        self.assertEquals(STATUS_405, result.status)



class AdminUnitTests(TestCase):
    def create_app(self):
        app = views.app
        os.environ['BACKEND_URL'] = 'http://localhost:10080'
        app.config['TESTING'] = True
        return app

    @mock.patch('application.logic.requests')
    def test_admin_get(self, mocked):
        os.environ['BACKEND_URL'] = 'http://localhost:10080'
        result = self.client.get('/admin')
        self.assertEquals(STATUS_200, result.status)
        self.assertTrue('class="logo lobster"><a href="/">Krogrouletten</a></div>' in result.data)
        self.assertTrue('Ny krog' in result.data)
        mocked.get.assert_called_with('http://localhost:10080/find/all')
        self.assert_template_used('admin.html')


class PopupUnitTests(TestCase):
    def create_app(self):
        app = views.app
        os.environ['BACKEND_URL'] = 'http://localhost:10080'
        app.config['TESTING'] = True
        return app

    def test_popup_get(self):
        response = self.client.get('/admin/popup')
        self.assertEquals(STATUS_200, response.status)
        self.assertFalse('class="logo lobster"><a href="/">Krogrouletten</a></div>' in response.data)
        self.assertTrue('Skicka' in response.data)
        self.assert_template_used('krog_popup.html')

