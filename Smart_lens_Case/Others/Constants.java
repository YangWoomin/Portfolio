package com.yangproject.iot.Others;

/**
 * Created by 우민 on 2016-06-04.
 */
public class Constants {
    public static final int LENS_MAX_NUMBER = 3;
    public static final int ALARM_SERVICE_NUMBER = 2;
    public static final String SLEEP_TIME = "sleep_time";
    public static final String LENS = "lens";
    public static final String LENS_NAME = "lens_name";
    public static final String LENS_EXPIRATION_DATE = "lens_expiration_date";
    public static final String LENS_LAST_WASH_DATE = "lens_last_wash_date";
    /*public static final String RENEW_SLEEP_TIME_INTENT_FILTER = "renew_sleep_time";
    public static final String RENEW_DATABASE_TO_SERVICE = "renew_database_to_service";*/
    public static final String RENEW_DATABASE_TO_ACTIVITY = "renew_database_to_activity";
    public static final String SLEEP_TIME_INTENT_FILTER = "sleep_time_alarm";
    public static final String EXPIRATION_INTENT_FILTER = "expiration_alarm";
    public static final String RECOMMENDED_TIME_INTENT_FILTER = "recommended_time_alarm";
    public static final String COMMAND_WASHING_INTENT_FILTER = "command_washing";
    public static final long WASH_PERIOD = 172800000;
    public static final long RECOMMENDED_PERIOD = 120000; // 2minutes, 28800000 : 8hours

    public static final String MQTT_SERVER =  "tcp://192.168.168.101:1883"; // "tcp://192.168.0.:1883"; // "tcp://218.150.182.14:1883";
    public static final String TRY_MQTT_CONNECTION = "try_mqtt_connection";
    public static final String MQTT_TOPIC_0 = "lens/client_state";
    public static final String MQTT_TOPIC_1 = "lens/button_state";
    public static final String MQTT_TOPIC_2 = "lens/lens_state";
    public static final String MQTT_TOPIC_3 = "lens/lens_active";
    public static final long MQTT_CONNECTION_PERIOD = 60000; // 600000
}
