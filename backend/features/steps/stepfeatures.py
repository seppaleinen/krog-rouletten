from behave import given, when, then
from hamcrest import assert_that, contains_string, instance_of, equal_to
import os.path, json, mock


def mocked_requests_get(*args, **kwargs):
    class MockResponse:
        def __init__(self, json_data, status_code):
            self.json_data = json_data
            self.status_code = status_code

        def json(self):
            return self.json_data

    _file = os.path.join(os.path.dirname(__file__), '../resources/google_details.txt')
    with open(_file, 'r') as content_file:
        google_details_content = content_file.read()

    _file = os.path.join(os.path.dirname(__file__), '../resources/google_search.txt')
    with open(_file, 'r') as content_file:
        google_search_content = content_file.read()

    print(args[0])
    if 'https://maps.googleapis.com/maps/api/place/nearbysearch/' in args[0]:
        return MockResponse(json.loads(google_search_content), 200)
    else:
        return MockResponse(json.loads(google_details_content), 200)


@given('google endpoint is mocked')
def given_mocked_endpoint(context):
    response = None


@given('requestdata is {request_data}')
def given_mocked_endpoint(context, request_data):
    context.request_data = request_data


@when('calling GET on "{endpoint}"')
def get_to_endpoint(context, endpoint):
    with mock.patch('application.logic.requests.get', side_effect=mocked_requests_get, autospec=True) as mocked:
        context.response = context.client.get(endpoint, expect_errors=True)
        context.mock = mocked


@when('calling POST on "{endpoint}"')
def post_to_endpoint(context, endpoint):
    with mock.patch('application.logic.requests.get', side_effect=mocked_requests_get, autospec=True) as mocked:
        context.response = context.client.post(endpoint, context.request_data, expect_errors=True)
        context.mock = mocked


@then('"{expected_status}" should be the status')
def expected_response_status(context, expected_status):
    assert_that(context.response.status, equal_to(expected_status))


@then('these endpoints should have been called')
def expected_response_status(context):
    for row in context.table:
        context.mock.assert_called_with(row['url'])


@then('"{expectedText}" should be in body')
def expected_body(context, expectedText):
    body = str(json.loads(context.response.json))
    assert_that(body, instance_of(str), "%s must be of type str" % body)
    assert_that(body, contains_string(expectedText), "%s should contain %s" % (body, expectedText))


