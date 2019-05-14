package com.capgemini.tdddemo.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.capgemini.tdddemo.entity.Product;
import com.capgemini.tdddemo.service.ProductService;

import antlr.collections.List;

@RestController
public class ProductController {
	private static final Logger logger = LogManager.getLogger(ProductController.class);

	@Autowired
	private ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@PostMapping("/product")
	public @ResponseBody String createProduct() {
		// create a new Product
		Product product = new Product(101, "kapil", 100, 1);

		productService.save(product);
		return "success";
	}

	@GetMapping("/product")
	public @ResponseBody List getAll() {
		return productService.getAll();
	}

	@GetMapping("/product/{id}")
	public @ResponseBody Optional<Product> getById(@PathVariable int id) {
		return productService.findById(id);
	}

	@PutMapping("/product/{id}")
	public ResponseEntity<?> updateProduct(@RequestBody Product product, @PathVariable Integer id,
			@RequestHeader("If-Match") Integer ifMatch) {
		logger.info("Updating product with id: {}, name: {}, quantity: {}", id, product.getName(),
				product.getQuantity());

		// Get the existing product
		Optional<Product> existingProduct = productService.findById(id);

		return existingProduct.map(p -> {
			// Compare the etags
			logger.info("Product with ID: " + id + " has a version of " + p.getVersion() + ". Update is for If-Match: "
					+ ifMatch);

			// Update the product
			p.setName(product.getName());
			p.setQuantity(product.getQuantity());
			p.setVersion(p.getVersion() + 1);

			logger.info("Updating product with ID: " + p.getId() + " -> name=" + p.getName() + ", quantity="
					+ p.getQuantity() + ", version=" + p.getVersion());

			try {
				// Update the product and return an ok response
				if (productService.save(p) != null) {
					return ResponseEntity.ok().location(new URI("/product/" + p.getId()))
							.eTag(Integer.toString(p.getVersion())).body(p);
				} else {
					return ResponseEntity.notFound().build();
				}
			} catch (URISyntaxException e) {
				// An error occurred trying to create the location URI, return an error
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}

		}).orElse(ResponseEntity.notFound().build());
	}
}
