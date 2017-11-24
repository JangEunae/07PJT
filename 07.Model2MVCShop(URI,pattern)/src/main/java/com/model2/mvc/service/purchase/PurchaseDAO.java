package com.model2.mvc.service.purchase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.user.UserService;
import com.model2.mvc.service.user.impl.UserServiceImpl;

public interface PurchaseDAO {
	
	public void addPurchase(Purchase purchase) throws Exception ;
	
	public Purchase getPurchase(int tranNo) throws Exception;
	
	public Purchase getPurchase2(int prodNo) throws Exception ;
	
	public List<Purchase> getPurchaseList(Search search, String buyerId) throws Exception;

	public List<Purchase> getSaleList(Search search, String buyerId) throws Exception;
	
	public void updatePurchase(Purchase purchase) throws Exception ;
	
	public void updateTranCode(Purchase purchase) throws Exception ;
	
	public void deleteTranCode(Purchase purchase) throws Exception;
	
	public int getTotalCount(String string) throws Exception ;
	// 게시판 currentPage Row 만  return 
}
