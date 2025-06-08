package ru.yandex.practicum.filmorate;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.controllers.UserController;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTests {

    String userJsonCreate = "{"
            + "\"login\":\"abobaLogin\","
            + "\"name\":\"Aboba User\","
            + "\"email\":\"aboba@mail.ru\","
            + "\"birthday\":\"1946-08-20\""
            + "}";

    @Autowired
    private UserController userController;
    @Autowired
    private MockMvc mockMvc;

    static Stream<String> invalidUserJsonData() {
        return Stream.of(
                "{"
                        + "\"login\":\"aboba Login\","
                        + "\"name\":\"Aboba User\","
                        + "\"email\":\"aboba@mail.ru\","
                        + "\"birthday\":\"1946-08-20\""
                        + "}", // Неверный логин

                "{"
                        + "\"login\":\"abobaLogin\","
                        + "\"name\":\"Aboba User\","
                        + "\"email\":\"mail.ru\","
                        + "\"birthday\":\"1946-08-20\""
                        + "}", // Неверный email

                "{"
                        + "\"login\":\"abobaLogin\","
                        + "\"name\":\"Aboba User\","
                        + "\"email\":\"mail.ru\","
                        + "\"birthday\":\"2446-08-20\""
                        + "}" // Неправильное день рождения
        );
    }

    @BeforeEach
    public void beforeEach() {
        userController.getUsers().clear();
    }

    @Test
    public void should_create_user() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userJsonCreate))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.login").value("abobaLogin"))
                .andExpect(jsonPath("$.name").value("Aboba User"))
                .andExpect(jsonPath("$.email").value("aboba@mail.ru"))
                .andExpect(jsonPath("$.birthday").value("1946-08-20"));
    }

    @Test
    public void should_create_user_with_empty_name() throws Exception {
        String jsonCreate = "{"
                + "\"login\":\"abobaLogin\","
                + "\"name\":\"\","
                + "\"email\":\"aboba@mail.ru\","
                + "\"birthday\":\"1946-08-20\""
                + "}";

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonCreate))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.login").value("abobaLogin"))
                .andExpect(jsonPath("$.name").value("abobaLogin"))
                .andExpect(jsonPath("$.email").value("aboba@mail.ru"))
                .andExpect(jsonPath("$.birthday").value("1946-08-20"));
    }

    @ParameterizedTest
    @MethodSource("invalidUserJsonData")
    public void should_not_create_user_with_json_error(String json) throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().is4xxClientError());
    }

    @Test
    public void should_not_create_user_with_same_email() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userJsonCreate));
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userJsonCreate))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void should_update_user() throws Exception {

        String userJsonUpdate = "{"
                + "\"id\":1,"
                + "\"login\":\"abobaLoginUpdate\","
                + "\"name\":\"Aboba User Update\","
                + "\"email\":\"abobaUpdate@mail.ru\","
                + "\"birthday\":\"1956-08-20\""
                + "}";

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userJsonCreate));
        mockMvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON).content(userJsonUpdate))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.login").value("abobaLoginUpdate"))
                .andExpect(jsonPath("$.name").value("Aboba User Update"))
                .andExpect(jsonPath("$.email").value("abobaUpdate@mail.ru"))
                .andExpect(jsonPath("$.birthday").value("1956-08-20"));

    }

    @Test
    public void should_not_find_id_update_user() throws Exception {

        String userJsonUpdate = "{"
                + "\"id\":666,"
                + "\"login\":\"abobaLoginUpdate\","
                + "\"name\":\"Aboba User Update\","
                + "\"email\":\"abobaUpdate@mail.ru\","
                + "\"birthday\":\"1956-08-20\""
                + "}";

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userJsonCreate));
        mockMvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON).content(userJsonUpdate))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void should_get_all_users() throws Exception {
        String userJsonCreate2 = "{"
                + "\"login\":\"abobaLogin2\","
                + "\"name\":\"Aboba User2\","
                + "\"email\":\"aboba2@mail.ru\","
                + "\"birthday\":\"1956-08-20\""
                + "}";

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userJsonCreate));
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userJsonCreate2));

        MvcResult result = mockMvc.perform(get("/users")).andReturn();

        JsonElement expectedJson = JsonParser.parseString("[{"
                + "\"id\":1,"
                + "\"login\":\"abobaLogin\","
                + "\"name\":\"Aboba User\","
                + "\"email\":\"aboba@mail.ru\","
                + "\"birthday\":\"1946-08-20\""
                + "},{"
                + "\"id\":2,"
                + "\"login\":\"abobaLogin2\","
                + "\"name\":\"Aboba User2\","
                + "\"email\":\"aboba2@mail.ru\","
                + "\"birthday\":\"1956-08-20\""
                + "}]");

        assertEquals(expectedJson, JsonParser.parseString(result.getResponse().getContentAsString()));
    }


}
