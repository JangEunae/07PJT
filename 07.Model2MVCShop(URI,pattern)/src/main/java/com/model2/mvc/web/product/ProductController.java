package com.model2.mvc.web.product;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.purchase.PurchaseService;
import com.model2.mvc.service.purchase.impl.PurchaseServiceImpl;


//==> 회원관리 Controller
@Controller
@RequestMapping("/product/*")
public class ProductController {
	
	///Field
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;
	
	@Autowired
	@Qualifier("purchaseServiceImpl")
	private PurchaseService purchaseService;
	
	//setter Method 구현 않음
		
	public ProductController(){
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
	
	
	@RequestMapping(value="addProduct", method=RequestMethod.POST )
	public String addProduct( @ModelAttribute("product") Product product ,HttpServletRequest request,  Model model ) throws Exception {

		System.out.println("/product/addProduct : POST");
		//Business Logic
		
		if(FileUpload.isMultipartContent(request)) {
			String temDir = "C:\\Users\\bitcamp\\git\\07PJT01\\07.Model2MVCShop(URI,pattern)\\WebContent\\images\\uploadFiles\\";
		
		DiskFileUpload fileUpload = new DiskFileUpload();
		fileUpload.setRepositoryPath(temDir);
		fileUpload.setSizeMax(1024*1024*10);
		fileUpload.setSizeThreshold(1024*100);
		
		if(request.getContentLength()<fileUpload.getSizeMax()) {
			//Product product = new Product();
			StringTokenizer token = null;
			
			List filetemList = fileUpload.parseRequest(request);
			int Size = filetemList.size();
			for(int i = 0; i<Size; i++) {
				FileItem fileItem = (FileItem)filetemList.get(i);
				if(fileItem.isFormField()) {
					if(fileItem.getFieldName().equals("manuDate")) {
						token = new StringTokenizer(fileItem.getString("euc-kr"),"-");
						String manuDate = token.nextToken()+token.nextToken()+token.nextToken();
						product.setManuDate(manuDate);
						}
					else if(fileItem.getFieldName().equals("prodName"))
						product.setProdName(fileItem.getString("euc-kr"));
					else if(fileItem.getFieldName().equals("prodDetail"))
						product.setProdDetail(fileItem.getString("euc-kr"));
					else if(fileItem.getFieldName().equals("price"))
						product.setPrice(Integer.parseInt(fileItem.getString("euc-kr")));
					}else {
						if(fileItem.getSize()>0) {
							int idx = fileItem.getName().lastIndexOf("\\");
							if(idx == -1) {
								idx = fileItem.getName().lastIndexOf("/");
							}
							String fileName = fileItem.getName().substring(idx+1);
							product.setFileName(fileName);
							try {
								File uploaded = new File(temDir, fileName);
								fileItem.write(uploaded);
							}catch (IOException e) {
								System.out.println(e);
							}
						}else {
							product.setFileName("../../images/empty.GIF");
						}
					}
				}
			productService.addProduct(product);
			model.addAttribute("productVO", product);
			}else {
				int overSize = (request.getContentLength()/1000000);
				System.out.println("<script>alert('파일의 크기는 1MB까지 입니다. 올리신 파일 용량은"+overSize+"MB입니다.');");
				System.out.println("history.back();</script>");
			}
		}else {
			System.out.println("인코딩타입이 mulpart/form-data가 아닙니다.");
			}
		return "forward:/product/addProduct.jsp";
	}
	
	@RequestMapping(value="getProduct")
	public String getProduct( @RequestParam("prodNo") String prodNo , Model model, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		
		System.out.println("/product/getProduct : GET / POST");
		//Business Logic
		Product product = productService.getProduct(Integer.parseInt(prodNo));
		// Model 과 View 연결
		model.addAttribute("productVO", product);
		
		String str = "";
		Cookie[] cookies = request.getCookies();
		//System.out.println(cookies.length);
		
		if (cookies!=null && cookies.length > 0) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				//System.out.println(cookie.getName());
				if (cookie.getName().equals("history")) {
					str = cookie.getValue();
					//System.out.println(i);
				}
			}
		}
		str += prodNo+",";
		
		Cookie cookie = new Cookie("history", str);
		
		response.addCookie(cookie);
		
		
		return "forward:/product/getProduct.jsp";
	}
	
	@RequestMapping(value="updateProduct", method=RequestMethod.GET)
	public String updateProduct( @RequestParam("prodNo") String prodNo , Model model ) throws Exception{

		System.out.println("/product/updateProduct : GET");
		//Business Logic
		Product productVO = productService.getProduct(Integer.parseInt(prodNo));
		
		Purchase purchaseVO = purchaseService.getPurchase2(Integer.parseInt(prodNo));
		// Model 과 View 연결
		model.addAttribute("productVO", productVO);
		model.addAttribute("purchaseVO", purchaseVO);
		
		return "forward:/product/updateProductView.jsp";
	}
	
	@RequestMapping(value="updateProduct", method=RequestMethod.POST)
	public String updateProduct( @ModelAttribute("product") Product product, Model model ) throws Exception{

		System.out.println("/product/updateProduct : POST");
		
		//Business Logic
		productService.updateProduct(product);
		
		model.addAttribute("productVO", product);

		return "forward:/product/getProduct?menu=manage";
	}
	
	@RequestMapping(value="listProduct")
	public String listProduct( @ModelAttribute("search") Search search, Model model , HttpServletRequest request) throws Exception{
		
		System.out.println("/product/listProduct : GET / POST");
		if(search.getCurrentPage() ==0 ){
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		// Business logic 수행
		Map<String , Object> map=productService.getProductList(search);
		
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		System.out.println(resultPage);
		System.out.println("상품"+search);
		// Model 과 View 연결
		model.addAttribute("list", map.get("list"));
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("search", search);
		
		return "forward:/product/listProduct.jsp";
	}
}