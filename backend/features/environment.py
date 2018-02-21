from application import views
from webtest import TestApp

class MyProxyHack(object):
    def __init__(self, app):
        self.app = app
    def __call__(self, environ, start_response):
        environ['REMOTE_ADDR'] = environ.get('REMOTE_ADDR', '127.0.0.1')
        return self.app(environ, start_response)


def before_feature(context, feature):
    app = views.app
    app.testing = True
    app.config['TESTING'] = True
    app.wsgi_app = MyProxyHack(app.wsgi_app)
    app.config['PRESERVE_CONTEXT_ON_EXCEPTION'] = False
    context.client = TestApp(app)
