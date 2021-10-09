package guru.qa.tests;

import com.sun.tools.javac.util.List;
import guru.qa.models.UserData;
import guru.qa.models.User;
import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.Map;
import com.github.javafaker.Faker;

import guru.qa.models.UserData;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ApiTests {
    Faker faker = new Faker();
    Date date = new Date();
    SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");

    private String name = faker.name().firstName();
    private String job = faker.job().position();
    private String email = faker.internet().emailAddress();

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
    void listOfResourceTest(){
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
}
