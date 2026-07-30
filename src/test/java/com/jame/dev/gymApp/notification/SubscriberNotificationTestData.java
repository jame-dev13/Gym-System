package com.jame.dev.gymApp.notification;

public interface SubscriberNotificationTestData {

   String UUID_RESOURCE_ERRORS = """
           VALUE,    ERROR_CODE
           0,        TYPE_MISMATCH
           -100,     TYPE_MISMATCH
           letters,  TYPE_MISMATCH
           NULL,     TYPE_MISMATCH
           """;

   String NOTIFICATION_PAYLOAD_VALIDATIONS_ERRORS = """
           SUBSCRIPTION_ID, RANGE_DAYS, ERROR_CODE
           NULL,            3,          VALIDATION_OPERATION
           1,               0,          VALIDATION_OPERATION
           1,               8,          VALIDATION_OPERATION
           """;

   String NOTIFICATION_UPDATE_PAYLOAD_VALIDATIONS_ERRORS = """
           RANGE_DAYS, ERROR_CODE
           NULL,       VALIDATION_OPERATION
           0,          VALIDATION_OPERATION
           8,          VALIDATION_OPERATION
           """;

   String BODY_FORMAT_ERRORS = """
           VALUE,  ERROR_CODE
           {NULL},   VALIDATION_OPERATION
           {EMPTY},  VALIDATION_OPERATION
           """;
}
