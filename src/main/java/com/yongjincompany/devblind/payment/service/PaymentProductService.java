package com.yongjincompany.devblind.service;

import com.yongjincompany.devblind.dto.PaymentProductResponse;
import com.yongjincompany.devblind.repository.PaymentProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentProductService {

    private final PaymentProductRepository productRepository;

    public List<PaymentProductResponse> getActiveProducts() {
        return productRepository.findAllByActiveTrue().stream()
                .map(PaymentProductResponse::from)
                .toList();
    }
}
