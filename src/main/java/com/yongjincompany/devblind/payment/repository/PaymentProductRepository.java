package com.yongjincompany.devblind.payment.repository;

import com.yongjincompany.devblind.payment.entity.PaymentProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentProductRepository extends JpaRepository<PaymentProduct, Long> {
    
    List<PaymentProduct> findAllByActiveTrue();
    
    Optional<PaymentProduct> findByIdAndActiveTrue(Long id);
}
