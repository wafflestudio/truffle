insert into `apps` (id, name, phase, api_key, slack_channel) values
(1, 'testApp', 'development', 'test-api-key', 'project-truffle');

insert into `exceptions` (id, app_id, class_name, elements, hash_code, status)values
(1, 1, 'TestApplication', '', 0, 0);

insert into `exception_events` (exception_id, message) values
(1, 'This is test.'),
(1, 'This is test2.');