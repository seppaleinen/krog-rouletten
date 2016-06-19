import sys, urllib2, mock, os
from os import path
sys.path.append( path.dirname( path.dirname( path.abspath(__file__) ) ) )
from application import views, app
from tests import STATUS_405, STATUS_200, STATUS_404
from flask_testing import TestCase, LiveServerTestCase
from selenium import webdriver


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
        self.assertTrue('<a class="navbar-brand logo lobster" href="/">Krogrouletten</a>' in result.data)
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
        result = self.client.post('/krog/random')
        self.assertEquals(STATUS_200, result.status)
        self.assertTrue('<a class="navbar-brand logo lobster" href="/">Krogrouletten</a>' in result.data)
        #mocked.get.assert_called_with('http://localhost:10080/find/random', params={'location': 'value1'})
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
        self.assertTrue('<a class="navbar-brand logo lobster" href="/">Krogrouletten</a>' in result.data)
        self.assertTrue('Ny krog' in result.data)
        mocked.get.assert_called_with('http://localhost:10080/find/all/approved')
        self.assert_template_used('admin.html')

"""
class MyTest(LiveServerTestCase):

    def create_app(self):
        app.config['TESTING'] = True
        # Default port is 5000
        app.config['LIVESERVER_PORT'] = 8944
        return app

    @mock.patch('application.logic.requests')
    def test_server_is_up_and_running(self, mocked):
        os.environ['BACKEND_URL'] = 'http://localhost:10081'
        response = urllib2.urlopen(self.get_server_url())
        self.assertEqual(response.code, 200)

    def test_selenium(self):
        binary = os.getenv('phantomjs.binary')
        if binary is not None:
            driver = webdriver.PhantomJS(executable_path=binary)
            driver.set_window_size(1120, 550)
            driver.get("https://duckduckgo.com/")
            driver.find_element_by_id('search_form_input_homepage').send_keys("realpython")
            driver.find_element_by_id("search_button_homepage").click()
            print driver.current_url
            driver.quit()
        else:
            raise Exception('Cant find phantomjs.binary')
"""


