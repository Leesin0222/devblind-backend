package com.yongjincompany.devblind.payment.service;

import com.yongjincompany.devblind.payment.dto.PaymentProductResponse;
import com.yongjincompany.devblind.payment.repository.PaymentProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentProductService {

    private final PaymentProductRepository productRepository;

    public List<PaymentProductResponse> getPaymentProducts() {
        return productRepository.findAllByActiveTrue().stream()
                .map(PaymentProductResponse::from)
                .toList();
    }
}
