# coding=UTF-8
import sys, urllib2, mock, os, time
from os import path
sys.path.append( path.dirname( path.dirname( path.abspath(__file__) ) ) )
from application import views, app
from tests import STATUS_405, STATUS_200, STATUS_404
from flask_testing import TestCase, LiveServerTestCase
from selenium import webdriver
from selenium.webdriver.support.ui import Select
import requests_mock


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


class IndexPageTest(LiveServerTestCase):
    def create_app(self):
        app.config['TESTING'] = True
        os.environ['BACKEND_URL'] = 'http://localhost:1008q'
        # Default port is 5000
        app.config['LIVESERVER_PORT'] = 8944
        return app

    @mock.patch('application.logic.requests')
    def test_selenium_homepage(self, mocked_requests):
        driver = webdriver.PhantomJS('phantomjs')
        driver.set_window_size(1120, 550)
        driver.get(self.get_server_url())
        mocked_requests.return_value = []

        self.assertEquals('http://localhost:8944/', driver.current_url)

        driver.find_element_by_id("primary_collapse").click()
        Select(driver.find_element_by_id("distance")).select_by_visible_text("300m")
        driver.find_element_by_id("slumpaGPS").click()
        self.assertEqual("Krogrouletten", driver.title)
        #self.assertEqual(u"Hittade ingen krog på din sökning", driver.find_element_by_css_selector("div.container.bodycontainer > div.container > div").text)

        driver.quit()


class AdminPageTest(LiveServerTestCase):
    def create_app(self):
        app.config['TESTING'] = True
        os.environ['BACKEND_URL'] = 'http://localhost:10080'
        # Default port is 5000
        app.config['LIVESERVER_PORT'] = 8944
        self.verificationErrors = []
        return app

    @requests_mock.mock()
    def test_selenium_homepage(self, mocked_requests):
        driver = webdriver.PhantomJS('phantomjs')
        driver.set_window_size(1120, 550)
        driver.get(self.get_server_url())
        snapshot(driver, '01_index.png')

        driver.find_element_by_link_text("Ny Krog").click()
        time.sleep(1)
        snapshot(driver, '02_ny_krog_popup.png')

        driver.find_element_by_id("namn").clear()
        driver.find_element_by_id("namn").send_keys("namn")
        driver.find_element_by_id("adress").clear()
        driver.find_element_by_id("adress").send_keys("adress")
        driver.find_element_by_id("beskrivning").clear()
        driver.find_element_by_id("beskrivning").send_keys("beskrivning")
        driver.find_element_by_id("user_krog_submit").click()
        snapshot(driver, '03_after_user_krog_submit.png')

        driver.find_element_by_link_text("Admin").click()
        self.assertEquals('http://localhost:8944/admin', driver.current_url)
        self.assertEqual("Krogrouletten", driver.title)

        snapshot(driver, '04_after_click_admin.png')

        response = "[{u'bar_typ': None, u'stadsdel': u'None', u'intrade': u'None', u'betyg': u'None', u'beskrivning': u'beskrivning', u'namn': u'namn', u'approved': False, u'oppet_tider': None, u'iframe_lank': u'None', u'adress': u'adress', u'id': u'57696a7abee83aa6e93bfc09', u'hemside_lank': u'None'}]"
        mocked_requests.get('http://localhost:10080/find/all/unapproved', json=response)
        driver.find_element_by_id("visa_ej_godkanda").click()
        time.sleep(1)

        snapshot(driver, '05_after_click_visa_ej_godkanda.png')

        for element in driver.find_elements_by_name("approve"):
            element.click()

        driver.quit()


def snapshot(driver, filename):
    current_dir = os.path.dirname(__file__)
    driver.save_screenshot(current_dir + "/screenshots/" + filename)

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