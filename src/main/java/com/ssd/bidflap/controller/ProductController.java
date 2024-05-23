package com.ssd.bidflap.controller;

import com.ssd.bidflap.domain.Product;
import com.ssd.bidflap.repository.ProductRepository;
import com.ssd.bidflap.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/product/register")
    public String productRegisterForm(){
        return "thyme/product/RegisterProduct";
    }

    @PostMapping("/product/register")
    public String productRegisterPro (Product product, RedirectAttributes redirectAttributes) throws Exception{
        Long productId= productService.getProductId(product);
        redirectAttributes.addAttribute("id", productId);
        return "redirect:/product/view";
    }

    @GetMapping("/product/view")
    public String productView(Model model, Long id){
        model.addAttribute("product", productService.productView(id));
        return "thyme/product/ViewProduct";
    }

    @PostMapping("/product/delete/{id}")
    public String productDelete(@PathVariable Long id){

        productService.productDelete(id);

        return "redirect:/";
    }

    @GetMapping("/product/modify/{id}")
    public String productModify(@PathVariable Long id, Model model){

        model.addAttribute("product", productService.productView(id));
        return "thyme/product/ModifyProduct";
    }

    @PostMapping("product/update/{id}")
    public String productUpdate(@PathVariable Long id, Product product, RedirectAttributes redirectAttributes) throws Exception{
        Product productTemp = productService.productView(id);
        productTemp.setTitle(product.getTitle());
        productTemp.setDescription(product.getDescription());
        productTemp.setPrice(product.getPrice());
        productTemp.setCategory(product.getCategory());

        productService.registerProduct(productTemp);

//        Long productId = productService.getProductId(product);
//        redirectAttributes.addAttribute("id", productId);

        return "redirect:/product/view?id=" + id;
    }
    @GetMapping("/product/list")
    public String productList(Model model, @RequestParam(value="keyword", required = false) String keyword) {
        List<Product> products;
        if (keyword != null && !keyword.isEmpty()) {
            products = productService.searchByTitle(keyword);
        } else {
            products = productService.getAllProducts();
        }
        model.addAttribute("products", products);
        return "thyme/product/ProductViewList";
    }
}
