package guru.qa.tests;

import com.github.javafaker.Faker;
import guru.qa.models.User;
import guru.qa.models.UserData;
import io.restassured.RestAssured;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

import static io.restassured.RestAssured.*;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;


public class ApiTests {
    Faker faker = new Faker();
    Date date = new Date();
    SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");

    private String name = faker.name().firstName();
    private String job = faker.job().position();
    private String email = faker.internet().emailAddress();
    private String password = faker.internet().password();

    JSONObject userData = new JSONObject()
            .put("name", this.name)
            .put("job", this.job);

    @BeforeEach
    void beforeEach() {
        RestAssured.baseURI = "https://reqres.in/api";
    }

    @Test
    void listOfUsersTest() {
        UserData listUser = given()
                .get("/users?page=2")
                .then()
                .statusCode(200)
                .extract().response().as(UserData.class);

        Optional<User> first = listUser
                .getUsers()
                .stream()
                .findFirst();
        assertThat(first.get().getFullName()).isEqualTo("Michael Lawson");
        assertThat(first.get().getEmail()).contains("@reqres.in");

        Optional<User> ten = listUser
                .getUsers()
                .stream()
                .filter(user -> user.getId() == 10).findFirst();
        assertThat(ten.get().getFullName()).isEqualTo("Byron Fields");
        assertThat(ten.get().getEmail()).contains("@reqres.in");
    }

    @Test
    void listOfResourceTest() {
        get("/unknown")
                .then()
                .statusCode(200)
                .body("data.findAll { it.year < 2002 }.name", hasItems("fuchsia rose", "cerulean"));
    }

    @Test
    void singleUserTest() {
        get("/users/2")
                .then()
                .statusCode(200)
                .body("data.first_name", is("Janet"));
    }

    @Test
    void createUserTest() {
        given()
                .contentType(JSON)
                .body(this.userData.toString())
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .body("name", equalTo(name))
                .body("job", equalTo(job))
                .body("id", notNullValue());
    }

    @Test
    void negativeRegisterTest() {
        JSONObject email = new JSONObject()
                .put("email", this.email);

        String response = given()
                .contentType(JSON)
                .body(email.toString())
                .when()
                .post("/register")
                .then()
                .statusCode(400)
                .extract().response().jsonPath().getString("error");

        assertThat(response).isEqualTo("Missing password");
    }

    @Test
    void updateUserTest() {
        this.formatDate.setTimeZone(TimeZone.getTimeZone("UTC"));

        String response = given()
                .contentType(JSON)
                .body(this.userData.toString())
                .when()
                .put("users/2")
                .then()
                .statusCode(200)
                .extract().response().jsonPath().getString("updatedAt");
        System.out.println(response);

        assertThat(response).contains(formatDate.format(date));
    }

    @Test
    void deleteTest() {
        delete("/users/2")
                .then()
                .statusCode(204);
    }

}
