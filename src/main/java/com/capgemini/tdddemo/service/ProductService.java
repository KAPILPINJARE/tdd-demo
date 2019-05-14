package com.capgemini.tdddemo.service;

import java.util.Optional;

import com.capgemini.tdddemo.entity.Product;

import antlr.collections.List;

public interface ProductService {

	public Optional<Product> findById(Integer id);

	public Product save(Product product);

	public List getAll();
}
