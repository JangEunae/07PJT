package com.model2.mvc.web.purchase;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;
import com.model2.mvc.service.purchase.PurchaseDAO;
import com.model2.mvc.service.purchase.PurchaseService;
import com.model2.mvc.service.purchase.impl.PurchaseServiceImpl;
import com.model2.mvc.service.user.UserService;
import com.model2.mvc.service.user.impl.UserServiceImpl;


//==> 회원관리 Controller
@Controller
@RequestMapping("/purchase/*")
public class PurchaseController {
	
	///Field
	@Autowired
	@Qualifier("purchaseServiceImpl")
	private PurchaseService purchaseService;
	
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;
	
	@Autowired
	@Qualifier("userServiceImpl")
	private UserService userService;
	
	//setter Method 구현 않음
		
	public PurchaseController(){
		System.out.println(this.getClass());
	}
	
	//==> classpath:config/common.properties  ,  classpath:config/commonservice.xml 참조 할것
	//==> 아래의 두개를 주석을 풀어 의미를 확인 할것
	@Value("#{commonProperties['pageUnit']}")
	//@Value("#{commonProperties['pageUnit'] ?: 3}")
	int pageUnit;
	
	@Value("#{commonProperties['pageSize']}")
	//@Value("#{commonProperties['pageSize'] ?: 2}")
	int pageSize;
	
	
	@RequestMapping(value="addPurchase", method=RequestMethod.GET)
	public ModelAndView addPurchase(@RequestParam("prodNo") String prodNo, @RequestParam("userId") String userId) throws Exception {

		System.out.println("/purchase/addPurchase : GET");
		
		Product productVO = productService.getProduct(Integer.parseInt(prodNo));
		User userVO = userService.getUser(userId);
		
		String viewName = "forward:/purchase/addPurchaseView.jsp";
		
		ModelAndView modelAndView = new ModelAndView();
		
		modelAndView.setViewName(viewName);
		modelAndView.addObject("userVO",userVO);
		modelAndView.addObject("productVO", productVO);
		//model.addAttribute("userVO",userVO);
		//model.addAttribute("productVO", productVO);
		
		return modelAndView;
	}
	
	@RequestMapping(value="addPurchase", method=RequestMethod.POST)
	public ModelAndView addProduct( @ModelAttribute("purchase") Purchase purchaseVO , @RequestParam("userId") String userId ,@RequestParam("prodNo") String prodNo ) throws Exception {

		System.out.println("/purchase/addPurchase : POST");
		//Business Logic
		purchaseVO.setBuyer(userService.getUser(userId));
		purchaseVO.setPurchaseProd(productService.getProduct(Integer.parseInt(prodNo)));
		purchaseService.addPurchase(purchaseVO);
		
		//model.addAttribute("purchaseVO", purchaseVO);
		
		return new ModelAndView("forward:/purchase/addPurchase.jsp", "purchaseVO", purchaseVO);
	}
	
	@RequestMapping(value="getPurchase")
	public ModelAndView getPurchase( @ModelAttribute("purchase") Purchase purchaseVO, @RequestParam("tranNo") String tranNo  ) throws Exception {
		
		System.out.println("/purchase/getPurchase : GET / POST");
		//Business Logic
		purchaseVO = purchaseService.getPurchase(Integer.parseInt(tranNo));
		// Model 과 View 연결
		//model.addAttribute("purchaseVO", purchaseVO);
		
		return new ModelAndView("forward:/purchase/getPurchase.jsp", "purchaseVO", purchaseVO);
	}
	
	@RequestMapping(value="updatePurchase", method=RequestMethod.GET)
	public ModelAndView updatePurchase( @RequestParam("tranNo") String tranNo ) throws Exception{

		System.out.println("/purchase/updatePurchase : GET");
		//Business Logic
		Purchase purchaseVO = purchaseService.getPurchase(Integer.parseInt(tranNo));
	
		//model.addAttribute("purchaseVO", purchaseVO);
		
		return  new ModelAndView("forward:/purchase/updatePurchaseView.jsp", "purchaseVO", purchaseVO);
	}
	
	@RequestMapping(value="updatePurchase", method=RequestMethod.POST)
	public ModelAndView updatePurchase( @ModelAttribute("purchase") Purchase purchase ) throws Exception{

		System.out.println("/purchase/updatePurchase : POST");
		//Business Logic
		//purchase.setTranNo(Integer.parseInt("tranNo"));
		purchaseService.updatePurchase(purchase);
		//model.addAttribute("purchaseVO", purchase);

		return  new ModelAndView("forward:/purchase/getPurchase", "purchaseVO", purchase);
	}
	
	@RequestMapping(value="listPurchase")
	public ModelAndView listPurchase( @ModelAttribute("search") Search search, HttpSession session) throws Exception{
		
		System.out.println("/purchase/listPurchase : GET / POST");
		if(search.getCurrentPage() ==0 ){
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		// Business logic 수행
		
		String buyerId=((User)session.getAttribute("user")).getUserId();
		Map<String , Object> map=purchaseService.getPurchaseList(search, buyerId);
		
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		System.out.println(resultPage);
		
		String viewName = "forward:/purchase/listPurchase.jsp";
		
		ModelAndView modelAndView = new ModelAndView();
		
		modelAndView.setViewName(viewName);
		modelAndView.addObject("list", map.get("list"));
		modelAndView.addObject("resultPage", resultPage);
		modelAndView.addObject("search", search);
		// Model 과 View 연결
		//model.addAttribute("list", map.get("list"));
		//model.addAttribute("resultPage", resultPage);
		//model.addAttribute("search", search);
		
		return modelAndView;
	}
	
	@RequestMapping(value="deletePurchase", method=RequestMethod.GET )
	public ModelAndView deletePurchase( @RequestParam("tranNo") String tranNo ) throws Exception{

		System.out.println("/purchase/deletePurchase : GET");
		
		//Business Logic
		Purchase purchaseVO = purchaseService.getPurchase(Integer.parseInt(tranNo));
		purchaseService.deleteTranCode(purchaseVO);
		
		//model.addAttribute("purchaseVO", purchaseVO);

		return new ModelAndView("forward:/purchase/listPurchase", "purchaseVO", purchaseVO);
	}
	
	@RequestMapping(value="updateTranCode",  method=RequestMethod.GET)
	public ModelAndView updateTranCodeAction( @RequestParam("tranNo") String tranNo,Model model ) throws Exception{

		System.out.println("/purchase/updateTranCode : GET");
		
		//Business Logic
		Purchase purchaseVO = purchaseService.getPurchase(Integer.parseInt(tranNo));
		purchaseService.updateTranCode(purchaseVO);
		//model.addAttribute("purchaseVO", purchaseVO);
		
		return new ModelAndView("forward:/purchase/listPurchase");
	}
	
	@RequestMapping(value="updateTranCodeByProd", method=RequestMethod.GET)
	public ModelAndView updateTranCodeByProdAction( @RequestParam("prodNo") String prodNo) throws Exception{

		System.out.println("/purchase/updateTranCodeByProd");
		
		//Business Logic
		Purchase purchaseVO = purchaseService.getPurchase2(Integer.parseInt(prodNo));
		purchaseService.updateTranCode(purchaseVO);
		
		return new ModelAndView("forward:/product/listProduct?menu=manage");
	}
}