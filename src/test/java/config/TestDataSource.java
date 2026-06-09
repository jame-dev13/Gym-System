package config;

public interface TestDataSource {
   String PAGINATION_ERRORS = """
           PAGE,  SIZE,   ERROR_CODE
           NULL,  10,     TYPE_MISMATCH
           0,     letter, TYPE_MISMATCH
           """;

   String ID_RESOURCE_ERRORS = """
           VALUE,   ERROR_CODE
           0,       CONSTRAINT_OPERATION
           -100,    CONSTRAINT_OPERATION
           letters, TYPE_MISMATCH
           NULL,    TYPE_MISMATCH
           """;

   String PAYLOAD_VALIDATIONS_ERRORS = """
           EMAIL,       ERROR_CODE
           @missing,     VALIDATION_OPERATION
           EMPTY,        VALIDATION_OPERATION
           NULL,         VALIDATION_OPERATION
           """;

   String BODY_FORMAT_ERRORS = """
           VALUE,  ERROR_CODE
           {NULL},   VALIDATION_OPERATION
           {EMPTY},  VALIDATION_OPERATION
           """;

   String SUBSCRIPTION_FORMAT_PAYLOAD_ERROR = """
           EMAIL,          MEMBERSHIP, ERROR_CODE
           user@mail.com,  NULL,       VALIDATION_OPERATION
           EMPTY,          ANNUAL,     VALIDATION_OPERATION
           NULL,           EMPTY,      VALIDATION_OPERATION
           """;
}
