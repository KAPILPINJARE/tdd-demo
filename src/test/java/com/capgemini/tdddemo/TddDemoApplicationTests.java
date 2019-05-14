package com.capgemini.tdddemo;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;

import com.capgemini.tdddemo.entity.Product;
import com.capgemini.tdddemo.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TddDemoApplicationTests {

	@MockBean
	private ProductService service;

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("POST /product - Success")
	void testCreateProduct() throws Exception {
		Product product = new Product(101, "kapil", 100, 1);

		mockMvc.perform(post("/product")).andExpect(content().string("success")).andExpect(status().isOk())
				.andDo(print());
	}

	@Test
	@DisplayName("GET /product - Success")
	void testGetAll() throws Exception {

		mockMvc.perform(get("/product")).andExpect(status().isOk()).andDo(print());
	}

	@Test

	@DisplayName("GET /product/101 - Success")
	void testGetById() throws Exception {
		mockMvc.perform(get("/product/101")).andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT /product/1 - Success")
	void testupdate() throws Exception {

		Product putProduct = new Product("Product Name", 10);
		Product mockProduct = new Product(1, "Product Name", 10, 1);
		doReturn(Optional.of(mockProduct)).when(service).findById(1);
		doReturn(mockProduct).when(service).save(any());

		mockMvc.perform(put("/product/{id}", 1).contentType(MediaType.APPLICATION_JSON).header(HttpHeaders.IF_MATCH, 1)
				.content(asJsonString(putProduct)))

				// Validate the response code and content type
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))

				// Validate the headers
				.andExpect(header().string(HttpHeaders.ETAG, "\"2\""))
				.andExpect(header().string(HttpHeaders.LOCATION, "/product/1"))

				// Validate the returned fields
				.andExpect(jsonPath("$.id", is(1))).andExpect(jsonPath("$.name", is("Product Name")))
				.andExpect(jsonPath("$.quantity", is(10))).andExpect(jsonPath("$.version", is(2)));

	}

	static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

/*
 * @Test
 * 
 * @DisplayName("GET /product/1 - Found") public void testGetproductByIdFound()
 * throws Exception { //setup our mock service Product mockProduct = new
 * Product(1, "Product Name", 10, 1);
 * doReturn(Optional.of(mockProduct)).when(service).findById(1);
 * 
 * 
 * when(service.findById(1)).thenReturn(mockProduct); //execute the GET request
 * mockMvc.perform(get("/product/{id}", 1))
 * .andDo(print()).andExpect(status().isOk())
 * .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
 * 
 * .andExpect(jsonPath("$.id", is(1))) .andExpect(jsonPath("$.name",
 * is("Product Name"))) .andExpect(jsonPath("$.quantity", is(10)))
 * .andExpect(jsonPath("$.version", is(1))); }
 */