package data;

public interface SignInTestData {

   String JSON_VALID = """
      {"email": "test@example.com", "password": "password123"}""";

   String JSON_INVALID = """
      {"email": "", "password": ""}""";
}
