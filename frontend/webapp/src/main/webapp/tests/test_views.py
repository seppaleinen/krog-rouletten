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
        mocked.get.assert_called_with('http://localhost:10080/find/all')
        self.assert_template_used('admin.html')


class IndexPageTest(LiveServerTestCase):
    def create_app(self):
        app.config['TESTING'] = True
        os.environ['BACKEND_URL'] = 'http://localhost:10080'
        # Default port is 5000
        app.config['LIVESERVER_PORT'] = 8944
        return app

    def test_selenium_homepage(self):
        driver = webdriver.PhantomJS('phantomjs')
        driver.set_window_size(1120, 550)
        driver.get(self.get_server_url())
        driver.find_element_by_id("slumpaGPS").click()
        print driver.current_url
        self.assertEquals('http://localhost:8944/', driver.current_url)
        driver.quit()


class AdminPageTest(LiveServerTestCase):
    def create_app(self):
        app.config['TESTING'] = True
        os.environ['BACKEND_URL'] = 'http://localhost:10080'
        # Default port is 5000
        app.config['LIVESERVER_PORT'] = 8944
        return app

    def test_selenium_homepage(self):
        driver = webdriver.PhantomJS('phantomjs')
        driver.set_window_size(1120, 550)
        driver.get(self.get_server_url() + '/admin')
        self.assertTrue(driver.find_element_by_id("krog_2").is_displayed())
        #print driver.page_source
        self.assertEquals('http://localhost:8944/admin', driver.current_url)
        driver.quit()


"""
def test_selenium(self):
        driver = webdriver.PhantomJS('phantomjs')
        driver.set_window_size(1120, 550)
        driver.get("https://duckduckgo.com/")
        driver.find_element_by_id('search_form_input_homepage').send_keys("realpython")
        driver.find_element_by_id("search_button_homepage").click()
        print driver.current_url
        self.assertEquals('https://duckduckgo.com/?q=realpython&ia=web', driver.current_url)
        driver.quit()
"""