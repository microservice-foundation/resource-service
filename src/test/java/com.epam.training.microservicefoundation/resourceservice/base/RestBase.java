package com.epam.training.microservicefoundation.resourceservice.base;

import com.epam.training.microservicefoundation.resourceservice.api.ResourceController;
import com.epam.training.microservicefoundation.resourceservice.api.ResourceExceptionHandler;
import com.epam.training.microservicefoundation.resourceservice.domain.ResourceNotFoundException;
import com.epam.training.microservicefoundation.resourceservice.domain.ResourceRecord;
import com.epam.training.microservicefoundation.resourceservice.service.ResourceService;
import io.restassured.config.EncoderConfig;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.config.RestAssuredMockMvcConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//@ExtendWith(value = {PostgresExtension.class, CloudStorageExtension.class})
//@TestPropertySource(locations = "classpath:application.yaml")
@ExtendWith({MockitoExtension.class, SpringExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT) // https://stackoverflow.com/questions/42947613/how-to-resolve-unneccessary-stubbing-exception
@ContextConfiguration(classes = RestBase.RestBaseConfiguration.class)
public abstract class RestBase {
    private ResourceController resourceController;
    @Autowired
    ResourceExceptionHandler resourceExceptionHandler;
    @MockBean
    ResourceService service;

    @BeforeEach
    public void setup() throws FileNotFoundException {
        when(service.getById(123L)).thenReturn(new InputStreamResource(new FileInputStream(ResourceUtils.getFile(
                "classpath:files/mpthreetest.mp3"))));

        when(service.getById(1999L)).thenThrow(new ResourceNotFoundException("Resource with id=1999 not found"));
        when(service.save(any())).thenReturn(new ResourceRecord(1L));

        resourceController = new ResourceController(service);
        EncoderConfig encoderConfig = new EncoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false);
        RestAssuredMockMvc.config = new RestAssuredMockMvcConfig().encoderConfig(encoderConfig);
        RestAssuredMockMvc.standaloneSetup(MockMvcBuilders.standaloneSetup(resourceController)
                .setControllerAdvice(resourceExceptionHandler));
    }

    @TestConfiguration
    static class RestBaseConfiguration {

        @Bean
        ResourceExceptionHandler resourceExceptionHandler() {
            return new ResourceExceptionHandler();
        }
    }
}
