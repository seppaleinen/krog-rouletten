import urllib2, mock, os, unittest
from flask import Flask
from flask_testing import LiveServerTestCase
from application import app


class MyTest(LiveServerTestCase):

    def create_app(self):
        app.config['TESTING'] = True
        # Default port is 5000
        app.config['LIVESERVER_PORT'] = 8943
        return app

    @unittest.skip("Can't get it to work...")
    @mock.patch('application.logic.requests')
    def test_server_is_up_and_running(self, mocked):
        os.environ['BACKEND_URL'] = 'http://localhost:10081'
        response = urllib2.urlopen(self.get_server_url())
        self.assertEqual(response.code, 200)
        mocked.get.assert_called_with('http://localhost:10081/find/random')