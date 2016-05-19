package se.panok.music.integrationtests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import se.panok.music.Application;

import com.fasterxml.jackson.core.JsonProcessingException;

@Profile("MusicControllerTest")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { Application.class,
		CoverArtClientThrowingExceptionMusicControllerITConfig.class })
@ComponentScan
@WebAppConfiguration
@IntegrationTest({ "server.port:0" })
public class CoverArtClientThrowingExceptionMusicControllerIT {

	private static final String URL = "http://localhost:%d/music/v1/artist/%s";

	private MockMvc mockMvc;

	@Value("${local.server.port}")
	private int port;

	@Autowired
	private WebApplicationContext wac;

	@Before
	public void setup() throws JsonProcessingException, IOException,
			URISyntaxException {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testThis() throws Exception {
		final String mbid = "5b11f4ce-a62d-471e-81fc-a69a8278c7da";
		final int albumLength = 25;
		this.mockMvc
				.perform(
						get(String.format(URL, port, mbid)).contentType(
								MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andDo(print())
				.andExpect(jsonPath("$.mbid").value(mbid))
				.andExpect(jsonPath("$.albums.length()").value(albumLength));
		;
	}
}
