package data;

public interface SignUpTestData {

   String VALID_USER_JSON = """
      {
      "name": "John Doe",
      "email": "john.doe@example.com",
      "password": "SecurePassword123!",
      "authProvider": "LOCAL",
      "roles": ["USER"]
      }
      """;

   String EMPTY_ROLES_JSON = """
      {
      "name": "Jane Doe",
      "email": "jane.doe@example.com",
      "password": "SecurePassword123!",
      "authProvider": "LOCAL",
      "roles": [""]
      }
      """;

   String ADMIN_USER_JSON = """
      {
      "name": "Admin User",
      "email": "admin@example.com",
      "password": "AdminStrictPass123!",
      "authProvider": "LOCAL",
      "roles": ["ADMIN"]
      }
      """;

   String GOOGLE_PROVIDER_JSON = """
      {
      "name": "Google User",
      "email": "google.user@gmail.com",
      "password": "",
      "authProvider": "GOOGLE",
      "roles": ["USER"]
      }
      """;

   String INVALID_JSON = """
      {
      "email": "admin@example.com",
      "password": "AdminStrictPass123!",
      }
      """;
}