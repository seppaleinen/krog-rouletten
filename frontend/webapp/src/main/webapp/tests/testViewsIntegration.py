import sys, urllib2
from os import path
sys.path.append( path.dirname( path.dirname( path.abspath(__file__) ) ) )
from application import views, app
from tests import unittest, STATUS_405, STATUS_200, STATUS_404
from flask import url_for, Flask
from flask_testing import LiveServerTestCase


class IntegrationTests(LiveServerTestCase):
    def create_app(self):
        app2 = app
        app2.config['TESTING'] = True
        app2.config['LIVESERVER_PORT'] = 8943
        return app2

    def test_server_is_up_and_running(self):
        response = urllib2.urlopen(self.get_server_url())
        self.assertEqual(response.code, 200)
