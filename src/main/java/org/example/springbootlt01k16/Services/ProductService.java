package org.example.springbootlt01k16.Services;

import org.example.springbootlt01k16.Repositories.BrandRepository;
import org.example.springbootlt01k16.Repositories.CategoryRepository;
import org.example.springbootlt01k16.Repositories.ProductRepository;
import org.example.springbootlt01k16.entities.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    @Autowired
    public ProductRepository productRepository;

    @Autowired
    public BrandRepository brandRepository;

    @Autowired
    CategoryRepository categoryRepository;

    private final Path uploadPath = Paths.get("uploads");

    public ProductService(){
        try {
            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }
        } catch (Exception e){
            throw new RuntimeException("Couldn't create upload folder");
        }
    }

    //Lấy toàn bộ product
    public List<Product> getAllProduct(){
        return productRepository.findAll();
    }

    //Lấy product theo id
    public Product getProductById(Long Id){
        return productRepository.findById(Id).orElseThrow(() -> new RuntimeException("Khong tim thay"));
    }

    //Tạo product
    public Product createProduct(Product product, MultipartFile file){
        Long brandId = product.getBrand().getId();
        Long categoryId = product.getCategory().getId();
        if(!brandRepository.existsById(brandId)){
            throw new RuntimeException("Khong ton tai brand");
        }
        if(!categoryRepository.existsById(categoryId)){
            throw new RuntimeException("Khong ton tai category");
        }

        //Xử lý ảnh
        try {
            if (file != null & !file.isEmpty()){
                String fileName = saveImage(file);
                product.setImage(fileName);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot save");
        }
        return productRepository.save(product);
    }

    private String saveImage(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);
        return fileName;
    }

    //Update
    public Product updateProduct(Product product, Long Id, MultipartFile file){
        Product existingProduct = getProductById(Id);
        Long newBrandId = product.getBrand().getId();
        Long newCategoryId = product.getCategory().getId();
        if(!brandRepository.existsById(newBrandId)){
            throw new RuntimeException("Khong ton tai brand");
        }
        if(!categoryRepository.existsById(newCategoryId)){
            throw new RuntimeException("Khong ton tai category");
        }
        //Update lại existing
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setBrand(product.getBrand());
        existingProduct.setCategory(product.getCategory());

        try {
            if(file != null & !file.isEmpty()){
                deleteOldImage(existingProduct.getImage());
                String fileName = saveImage(file);
                existingProduct.setImage(fileName);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot update image");
        }
        return productRepository.save(existingProduct);
    }

    private void deleteOldImage(String fileName) {
        try {
            if (fileName != null && !fileName.isEmpty()){
                Path oldFilePath = uploadPath.resolve(fileName);
                Files.deleteIfExists(oldFilePath);
            }
        } catch (Exception e){
            System.err.println("Cannot delete");
        }
    }

    //delete
    public void deleteProduct(Long Id){
        Product existingProduct = getProductById(Id);
        deleteOldImage(existingProduct.getImage());
        productRepository.delete(existingProduct);
        return;
    }
}
