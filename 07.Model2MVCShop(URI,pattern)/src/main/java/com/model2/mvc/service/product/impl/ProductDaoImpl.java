package com.model2.mvc.service.product.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductDAO;

@Repository("productDaoImpl")
public class ProductDaoImpl implements ProductDAO{

	
	@Autowired
	@Qualifier("sqlSessionTemplate")
	private SqlSession sqlSession;
	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}
	
	
	public ProductDaoImpl() {
		System.out.println(this.getClass());	
	}

	@Override
	public void addProduct(Product product) throws Exception {
		
		product.setManuDate((product.getManuDate()).replace("-", ""));
		
		sqlSession.insert("ProductMapper.addProduct", product);
	}

	@Override
	public Product getProduct(int prodNo) throws Exception {
		return sqlSession.selectOne("ProductMapper.getProduct", prodNo);
	}

	@Override
	public List<Product> getProductList(Search search) throws Exception {
		
		String arr[] = {};
		String singlePrice = null;
		String doublePrice = null;
		int length = 0;
		if (search.getSearchCondition() != null) {
			if (search.getSearchCondition().equals("2")) {
				arr = (search.getSearchKeyword()).split("~");
				length = arr.length;
				if(length >= 2) {
					singlePrice = arr[0];
					doublePrice = arr[1];
				}else {
					singlePrice = arr[0];
				}	
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("search", search );
		map.put("length", length );
		map.put("singlePrice", singlePrice);
		map.put("doublePrice", doublePrice);
		
		return sqlSession.selectList("ProductMapper.getProductList", map);
	}

	@Override
	public void updateProduct(Product product) throws Exception {
		
		sqlSession.update("ProductMapper.updateProduct", product);
	}

	@Override
	public int getTotalCount(Search search) throws Exception {
		
		String arr[] = {};
		String singlePrice = null;
		String doublePrice = null;
		int length = 0;
		if (search.getSearchCondition() != null) {
			if (search.getSearchCondition().equals("2")) {
				arr = (search.getSearchKeyword()).split("~");
				length = arr.length;
				if(length >= 2) {
					singlePrice = arr[0];
					doublePrice = arr[1];
				}else {
					singlePrice = arr[0];
				}	
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("search", search );
		map.put("length", length );
		map.put("singlePrice", singlePrice);
		map.put("doublePrice", doublePrice);
		
		
		return sqlSession.selectOne("ProductMapper.getTotalCount", map);
	}


}
