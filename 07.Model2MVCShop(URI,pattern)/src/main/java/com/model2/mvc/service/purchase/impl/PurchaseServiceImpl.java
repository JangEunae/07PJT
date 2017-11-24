package com.model2.mvc.service.purchase.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.purchase.PurchaseDAO;
import com.model2.mvc.service.purchase.PurchaseService;
import com.model2.mvc.service.user.UserDao;

@Service("purchaseServiceImpl")
public class PurchaseServiceImpl implements PurchaseService{
	
	
	@Autowired
	@Qualifier("purchaseDaoImpl")
	private PurchaseDAO purchaseDAO;
	
	public void setPurchaseDao(PurchaseDAO purchaseDAO) {
		this.purchaseDAO = purchaseDAO;
	}
	
	public PurchaseServiceImpl() {
		System.out.println(this.getClass());
	}

	public void addPurchase(Purchase purchase) throws Exception {
		purchaseDAO.addPurchase(purchase);
	}

	
	public Purchase getPurchase(int tranNo) throws Exception {
		return purchaseDAO.getPurchase(tranNo);
	}

	public Purchase getPurchase2(int prodNo) throws Exception {
		return purchaseDAO.getPurchase2(prodNo);
	}
		
	public Map<String, Object> getPurchaseList(Search search, String buyerId) throws Exception {
		List<Purchase> list= purchaseDAO.getPurchaseList(search, buyerId);
		int totalCount = purchaseDAO.getTotalCount(buyerId);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list );
		map.put("totalCount", new Integer(totalCount));
		
		return map;
	}

	
	public Map<String, Object> getSaleList(Search search) throws Exception {
		return null;
	}

	
	public void updatePurchase(Purchase purchase) throws Exception {
		System.out.println("¼­ºñ½º?"+purchase);
		purchaseDAO.updatePurchase(purchase);
	}

	
	public void updateTranCode(Purchase purchase) throws Exception {
		purchaseDAO.updateTranCode(purchase);
	}

	public void deleteTranCode(Purchase purchase) throws Exception{
		purchaseDAO.deleteTranCode(purchase);
	}
	

}
