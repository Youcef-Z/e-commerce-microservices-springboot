package com.youcef.ecommerce.product;

import com.youcef.ecommerce.exceptions.ProductPurchaseException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    public Integer createProduct(@Valid ProductRequest productRequest) {
        Product product = productMapper.toProduct(productRequest);
        return productRepository.save(product).getId();
    }

    public List<ProductPurchaseResponse> purchaseProducts(List<ProductPurchaseRequest> productPurchaseRequestList) {
        List<Integer> productIds = productPurchaseRequestList
                .stream()
                .map(ProductPurchaseRequest::productId)
                .toList();
        List<Product> availableProducts = productRepository.findAllByIdInOrderById(productIds);
        if (productIds.size() != availableProducts.size()) {
            throw new ProductPurchaseException("Some products are not available");
        }

        List<ProductPurchaseRequest> list = productPurchaseRequestList.stream()
                .sorted(Comparator.comparing(ProductPurchaseRequest::productId))
                .toList();

        List<ProductPurchaseResponse> purchasedProducts = new ArrayList<ProductPurchaseResponse>();
        for (int i = 0; i < list.size(); i++) {
            ProductPurchaseRequest productPurchaseRequest = list.get(i);
            Product product = availableProducts.get(i);
            if (product.getAvailableQuantity() < productPurchaseRequest.quantity()) {
                throw new ProductPurchaseException("Not enough quantity for product: " + product.getName());
            }
            product.setAvailableQuantity(product.getAvailableQuantity() - productPurchaseRequest.quantity());
            productRepository.save(product);
            purchasedProducts.add(productMapper.toProductPurchaseResponse(product, productPurchaseRequest.quantity()));
        }

        return purchasedProducts;
    }

    public ProductResponse getById(Integer productId) {
        return productRepository.findById(productId)
                .map(productMapper::toProductResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
    }

    public List<ProductResponse> listProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toProductResponse)
                .toList();
    }
}
