import urllib2, os
from flask import Flask
from flask_testing import LiveServerTestCase
from application import app
from selenium import webdriver


class MyTest(LiveServerTestCase):

    def create_app(self):
        app.config['TESTING'] = True
        # Default port is 5000
        app.config['LIVESERVER_PORT'] = 8944
        return app

    def test_server_is_up_and_running(self):
        response = urllib2.urlopen(self.get_server_url())
        self.assertEqual(response.code, 200)

    def test_(self):
        binary = os.getenv('phantomjs.binary')
        driver = webdriver.PhantomJS(executable_path=binary)
        driver.set_window_size(1120, 550)
        driver.get("https://duckduckgo.com/")
        driver.find_element_by_id('search_form_input_homepage').send_keys("realpython")
        driver.find_element_by_id("search_button_homepage").click()
        print driver.current_url
        driver.quit()



