package com.yongjincompany.devblind.repository;

import com.yongjincompany.devblind.entity.PaymentProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentProductRepository extends JpaRepository<PaymentProduct, Long> {
    List<PaymentProduct> findAllByActiveTrue();
}
