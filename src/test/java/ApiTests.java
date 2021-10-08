import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class ApiTests {

    @Test
    void listOfUsersTest(){
        when().
                get("https://reqres.in/api/unknown").
                then().
                body("data.findAll { it.year < 2002 }.name", hasItems("fuchsia rose", "cerulean"));

    }

  /*  void singleUserTest() {
        given()
                .get("https://reqres.in/api/users/2")
                .then()
                .statusCode(200)
                .body("data.first_name", is("Janet"));


    }
*/
}
