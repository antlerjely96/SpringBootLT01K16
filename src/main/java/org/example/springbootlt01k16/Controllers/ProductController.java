package org.example.springbootlt01k16.Controllers;

import org.example.springbootlt01k16.Services.ProductService;
import org.example.springbootlt01k16.entities.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    public ProductService productService;

    @GetMapping
    public List<Product> getAllProduct(){
        return productService.getAllProduct();
    }

    @GetMapping("/{Id}")
    public Product getProductById(@PathVariable Long Id){
        return productService.getProductById(Id);
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Product createProduct(
            @ModelAttribute Product product,
            @RequestParam(value = "imageFile", required = false) MultipartFile file
            ){
        return productService.createProduct(product, file);
    }

    @PutMapping(value = "/{Id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Product updateProduct(
            @ModelAttribute Product product,
            @PathVariable Long Id,
            @RequestParam(value = "imageFile", required = false) MultipartFile file
    ){
        return productService.updateProduct(product, Id, file);
    }

    @DeleteMapping("/{Id}")
    public void deleteProduct(@PathVariable Long Id){
        productService.deleteProduct(Id);
        return;
    }
}
