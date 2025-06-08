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
import ru.yandex.practicum.filmorate.controllers.FilmController;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class FilmControllerTests {

    String filmJsonCreate = """
            {"name": "Aboba Film",
             "description": "Aboba description",
             "releaseDate": "2000-01-01",
             "duration": 100
            }""";
    @Autowired
    private FilmController filmController;
    @Autowired
    private MockMvc mockMvc;

    static Stream<String> invalidFilmJsonData() {
        return Stream.of(
                """
                                {
                                    "name": "",
                                    "description": "Aboba description",
                                    "releaseDate": "2000-01-01",
                                    "duration": 100
                                }
                        """, // Неверный название фильма
                """
                                {
                                    "name": "Aboba Film",
                                    "description": "Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль.
                                   Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов.\s
                                   о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.",
                                  "releaseDate": "2000-01-01",
                                  "duration": 100
                                }
                        """, // Слишком длинное описание
                """
                                {
                                    "name": "Aboba Film",
                                    "description": "Aboba description",
                                    "releaseDate": "1890-03-25",
                                    "duration": 100
                                }
                        """, // Неправильная дата
                """
                                {
                                    "name": "Aboba Film",
                                    "description": "Aboba description",
                                    "releaseDate": "2000-01-01",
                                    "duration": -100
                                }
                        """ // Неправильная продолжительность фильма
        );
    }

    @BeforeEach
    public void beforeEach() {
        filmController.getFilms().clear();
    }

    @Test
    public void should_create_film() throws Exception {
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(filmJsonCreate))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Aboba Film"))
                .andExpect(jsonPath("$.description").value("Aboba description"))
                .andExpect(jsonPath("$.releaseDate").value("2000-01-01"))
                .andExpect(jsonPath("$.duration").value(100));
    }

    @ParameterizedTest
    @MethodSource("invalidFilmJsonData")
    public void should_not_create_film_with_json_error(String json) throws Exception {
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void should_update_film() throws Exception {

        String filmJsonUpdate = """
                {
                    "id": 1,
                    "name": "Aboba Film Update",
                    "description": "Aboba description Update",
                    "releaseDate": "2001-01-01",
                    "duration": 50
                  }""";

        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(filmJsonCreate));
        mockMvc.perform(put("/films").contentType(MediaType.APPLICATION_JSON).content(filmJsonUpdate))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Aboba Film Update"))
                .andExpect(jsonPath("$.description").value("Aboba description Update"))
                .andExpect(jsonPath("$.releaseDate").value("2001-01-01"))
                .andExpect(jsonPath("$.duration").value(50));
    }

    @Test
    public void should_not_find_id_update_film() throws Exception {

        String filmJsonUpdate = """
                {
                    "id": 666,
                    "name": "Aboba Film",
                    "description": "Aboba description",
                    "releaseDate": "2001-01-01",
                    "duration": 50
                  }""";

        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(filmJsonCreate));
        mockMvc.perform(put("/films").contentType(MediaType.APPLICATION_JSON).content(filmJsonUpdate))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void should_get_all_films() throws Exception {
        String filmJsonCreate2 = """
                {
                    "name": "Aboba Film 2",
                    "description": "Aboba description 2",
                    "releaseDate": "2002-01-01",
                    "duration": 50
                }""";

        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(filmJsonCreate));
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(filmJsonCreate2));

        MvcResult result = mockMvc.perform(get("/films")).andReturn();

        JsonElement expectedJson = JsonParser.parseString("""
                       [
                           {
                               "id": 1,
                               "name": "Aboba Film",
                               "description": "Aboba description",
                               "releaseDate": "2000-01-01",
                               "duration": 100
                          },
                         {
                              "id": 2,
                              "name": "Aboba Film 2",
                              "description": "Aboba description 2",
                              "releaseDate": "2002-01-01",
                              "duration": 50
                        }
                   ]
                """);

        assertEquals(expectedJson, JsonParser.parseString(result.getResponse().getContentAsString()));
    }

}
