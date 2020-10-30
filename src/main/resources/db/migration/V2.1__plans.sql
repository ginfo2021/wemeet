INSERT INTO settings (setting_key, setting_value) VALUES('default.update.location','true');

INSERT INTO settings (setting_key, setting_value) VALUES('default.plan.name','FREE');

INSERT INTO settings (setting_key, setting_value) VALUES('default.plan.code','DEFAULT_FREE_PLAN');

INSERT INTO plan (code, name) VALUES('DEFAULT_FREE_PLAN', 'FREE');

INSERT INTO feature_limit (plan_code, daily_swipe_limit, daily_message_limit, update_location) VALUES('DEFAULT_FREE_PLAN', '1', '1',0 );