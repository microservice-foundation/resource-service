package com.epam.training.microservicefoundation.resourceservice.api;

import com.epam.training.microservicefoundation.resourceservice.repository.resourcedatabase.PostgresExtension;
import com.epam.training.microservicefoundation.resourceservice.repository.s3storage.CloudStorageExtension;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.nio.file.Files;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(value = {PostgresExtension.class, CloudStorageExtension.class})
@Sql(value = {"/sql/drop-schema.sql", "/sql/create-schema.sql"})
@Sql(value = "/sql/data.sql")
class ResourceControllerTest {
    private MockMvc mockMvc;

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void shouldSaveResource() throws Exception {
        final File songFile = ResourceUtils.getFile("classpath:files/mpthreetest.mp3");

        MockMultipartFile multipartFile = new MockMultipartFile("multipartFile", songFile.getName(),
                "audio/mpeg", Files.readAllBytes(songFile.toPath()));
        ResultActions result = mockMvc
                        .perform(MockMvcRequestBuilders.multipart("/api/v1/resources")
                        .file(multipartFile));

        result.andExpect(status().isCreated());
        result.andExpect(content().contentType(MediaType.APPLICATION_JSON));
        result.andExpect(jsonPath("$.id", is(6)));
    }

    @Test
    void shouldThrowValidationExceptionWhenSaveResourceWithInvalidType() throws Exception {

        final File songFile = ResourceUtils.getFile("classpath:files/mpthreetest.mp3");

        MockMultipartFile multipartFile = new MockMultipartFile("multipartFile", songFile.getName(),
                MediaType.APPLICATION_JSON_VALUE, Files.readAllBytes(songFile.toPath()));
        ResultActions result = mockMvc
                        .perform(MockMvcRequestBuilders.multipart("/api/v1/resources")
                        .file(multipartFile));

        result.andExpect(status().isBadRequest());
        result.andExpect(jsonPath("$.message", is("Invalid request")));
    }

    @Test
    void shouldThrowValidationExceptionWhenSaveResourceWithEmptyFile() throws Exception {

        final File songFile = ResourceUtils.getFile("classpath:files/mpthreetest.mp3");

        MockMultipartFile multipartFile = new MockMultipartFile("multipartFile", songFile.getName(),
                MediaType.APPLICATION_JSON_VALUE, new byte[0]);

        ResultActions result = mockMvc
                        .perform(MockMvcRequestBuilders.multipart("/api/v1/resources")
                        .file(multipartFile));

        result.andExpect(status().isBadRequest());
        result.andExpect(jsonPath("$.message", is("Invalid request")));
    }

    @Test
    void shouldDeleteResourceByIds() throws Exception {
        String[] ids = {"1", "2"};
        ResultActions result = mockMvc
                        .perform(MockMvcRequestBuilders.delete("/api/v1/resources").param("id", ids));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$[*].id", contains(Integer.valueOf(ids[0]), Integer.valueOf(ids[1]))));
    }

    @Test
    void shouldThrowValidationExceptionWhenDeleteResourceByIds() throws Exception {
        ResultActions result = mockMvc
                .perform(MockMvcRequestBuilders.delete("/api/v1/resources").param("id", new String[200]));

        result.andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowValidationExceptionWhenDeleteResourceByNegativeIds() throws Exception {
        ResultActions result = mockMvc
                .perform(MockMvcRequestBuilders.delete("/api/v1/resources").param("id", "-1", "-3L"));

        result.andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowValidationExceptionWhenDeleteResourceByNonexistentIds() throws Exception {
        ResultActions result = mockMvc
                .perform(MockMvcRequestBuilders.delete("/api/v1/resources").param("id", "10", "1245"));

        result.andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetResource() throws Exception {
        // upload a file
        final File songFile = ResourceUtils.getFile("classpath:files/mpthreetest2.mp3");

        MockMultipartFile multipartFile = new MockMultipartFile("multipartFile", songFile.getName(),
                "audio/mpeg", Files.readAllBytes(songFile.toPath()));


        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/resources/")
                        .file(multipartFile))
                .andReturn();
        // get the file
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/resources/{id}",
                getValueOf("id", mvcResult.getResponse().getContentAsString())));

        result.andExpect(status().isOk());
        result.andExpect(content().contentType(multipartFile.getContentType()));
    }

    @Test
    void shouldThrowExceptionWhenGetResourceWithNonexistentId() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/resources/{id}",
                122_394_734_929L));

        result.andExpect(status().isNotFound());
    }

    private String getValueOf(String field, String jsonSource) throws JSONException {
        return new JSONObject(jsonSource).get(field).toString();
    }
}
