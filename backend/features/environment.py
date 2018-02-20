import tempfile
from application import app


def before_feature(context, feature):
    app.testing = True
    app.config['TESTING'] = True
    app.config['PRESERVE_CONTEXT_ON_EXCEPTION'] = False
    context.client = app.test_client()


