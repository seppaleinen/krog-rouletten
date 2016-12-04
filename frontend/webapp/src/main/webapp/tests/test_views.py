# coding=UTF-8
import sys, urllib2, mock, os, time, requests_mock, json
from os import path
from application import views, app
from tests import STATUS_405, STATUS_200, STATUS_404
from flask_testing import TestCase, LiveServerTestCase
from selenium import webdriver
from selenium.webdriver.support.ui import Select
sys.path.append( path.dirname( path.dirname( path.abspath(__file__) ) ) )


class UnitTests(TestCase):
    def create_app(self):
        os.environ['BACKEND_URL'] = 'http://localhost:10082'
        app = views.app
        app.config['TESTING'] = True
        return app

    @mock.patch('application.logic.requests')
    def test_index_page(self, mocked):
        result = self.client.get('/')
        self.assertEquals(STATUS_200, result.status)
        self.assert_template_used('index.html')

    @mock.patch('application.logic.requests')
    def test_krog_page(self, mocked):
        result = self.client.post('/krog/random')
        self.assertEquals(STATUS_200, result.status)
        self.assert_template_used('krog.html')

    @mock.patch('application.logic.requests')
    def test_admin_page(self, mocked):
        result = self.client.get('/admin')
        self.assertEquals(STATUS_200, result.status)
        self.assert_template_used('admin.html')
        #mocked.get.assert_called_with('http://localhost:10082/find/all/approved')

    @mock.patch('application.logic.requests')
    def test_save_user_krog(self, mocked):
        result = self.client.post('/krog/save')
        self.assertEquals(STATUS_200, result.status)
        self.assert_template_used('index.html')
        #mocked.get.assert_called_with('http://localhost:10082/find/all/approved')

    @mock.patch('application.logic.requests')
    def test_admin_unapproved(self, mocked):
        result = self.client.get('/admin/unapproved')
        self.assertEquals(STATUS_200, result.status)
        self.assert_template_used('admin.html')
        #mocked.get.assert_called_with('http://localhost:10082/find/all/approved')

    @mock.patch('application.logic.requests')
    def test_admin_krog_save(self, mocked):
        result = self.client.post('/admin/krog/save')
        self.assertEquals(STATUS_200, result.status)
        self.assert_template_used('admin.html')
        #mocked.get.assert_called_with('http://localhost:10082/find/all/approved')


class SeleniumTests(LiveServerTestCase):
    def create_app(self):
        app.config['TESTING'] = True
        os.environ['BACKEND_URL'] = 'http://localhost:10082'
        # Default port is 5000
        app.config['LIVESERVER_PORT'] = 8944
        return app

    @requests_mock.mock()
    def test_create_user_krog_and_approve_from_admin(self, mocked_requests):
        driver = webdriver.PhantomJS('phantomjs')
        driver.set_window_size(1120, 550)
        driver.get(self.get_server_url())
        Helper.snapshot(driver, '01_index.png')

        driver.find_element_by_id("nykrog").click()
        time.sleep(1)
        Helper.snapshot(driver, '02_ny_krog_popup.png')

        driver.find_element_by_id("namn").send_keys("namn")
        driver.find_element_by_id("user_krog_adress").send_keys("adress")
        driver.find_element_by_id("beskrivning").send_keys("beskrivning")
        driver.find_element_by_id("user_krog_submit").click()

        Helper.snapshot(driver, '03_after_user_krog_submit.png')

        driver.find_element_by_id("adminpage").click()
        self.assertEquals('http://localhost:8944/admin', driver.current_url)
        self.assertEqual("Krogrouletten", driver.title)

        Helper.snapshot(driver, '04_after_click_admin.png')

        response = "[{" \
                   "u'bar_typ': None, " \
                   "u'stadsdel': u'None', " \
                   "u'intrade': u'None', " \
                   "u'betyg': u'None', " \
                   "u'beskrivning': u'beskrivning', " \
                   "u'namn': u'namn', " \
                   "u'approved': False, " \
                   "u'oppet_tider': None, " \
                   "u'iframe_lank': u'None', " \
                   "u'adress': u'adress', " \
                   "u'id': u'57696a7abee83aa6e93bfc09', " \
                   "u'hemside_lank': u'None'}]"
        mocked_requests.get('http://localhost:10082/find/all/unapproved', text=response)

        driver.find_element_by_id("visa_ej_godkanda").click()
        time.sleep(1)

        Helper.snapshot(driver, '05_after_click_visa_ej_godkanda.png')

        #approvables = driver.find_elements_by_name("approve")
        #self.assertTrue(approvables, 'Should find one row')
        #approvables[0].click()

        driver.quit()


class Helper(object):
    @staticmethod
    def snapshot(driver, filename):
        current_dir = os.path.dirname(__file__)
        driver.save_screenshot(current_dir + "/screenshots/" + filename)

    @staticmethod
    def print_pagesource(driver):
        print(driver.page_source)
