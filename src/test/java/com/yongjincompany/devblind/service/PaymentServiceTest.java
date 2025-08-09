package com.yongjincompany.devblind.payment.service;

import com.yongjincompany.devblind.common.util.TossPaymentClient;
import com.yongjincompany.devblind.payment.dto.PaymentRequest;
import com.yongjincompany.devblind.payment.dto.PaymentResponse;
import com.yongjincompany.devblind.payment.entity.PaymentHistory;
import com.yongjincompany.devblind.payment.entity.PaymentProduct;
import com.yongjincompany.devblind.user.entity.User;
import com.yongjincompany.devblind.common.exception.ApiException;
import com.yongjincompany.devblind.payment.repository.PaymentProductRepository;
import com.yongjincompany.devblind.payment.repository.PaymentHistoryRepository;
import com.yongjincompany.devblind.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;
    @Mock
    private PaymentProductRepository paymentProductRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TossPaymentClient tossPaymentClient;

    @InjectMocks
    private com.yongjincompany.devblind.payment.service.PaymentService paymentService;

    private User testUser;
    private PaymentProduct testProduct;
    private PaymentRequest paymentRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .nickname("테스트유저")
                .build();

        testProduct = PaymentProduct.builder()
                .id(1L)
                .name("테스트 상품")
                .price(1000L)
                .description("테스트 상품입니다")
                .build();

        paymentRequest = new PaymentRequest(1L, "CARD", "success_url", "fail_url");
    }

    @Test
    @DisplayName("결제 요청 성공")
    void requestPayment_Success() {
        // given
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(testUser));
        when(paymentProductRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(tossPaymentClient.requestPayment(anyString(), anyString(), anyLong(), anyString(), anyString()))
                .thenReturn("payment_url");
        when(paymentHistoryRepository.save(any(PaymentHistory.class))).thenReturn(PaymentHistory.builder().id(1L).build());

        // when
        PaymentResponse response = paymentService.requestPayment(paymentRequest, 1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.paymentUrl()).isEqualTo("payment_url");
        verify(paymentHistoryRepository).save(any(PaymentHistory.class));
    }

    @Test
    @DisplayName("사용자를 찾을 수 없을 때 예외 발생")
    void requestPayment_UserNotFound_ThrowsException() {
        // given
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.requestPayment(paymentRequest, 1L))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("상품을 찾을 수 없을 때 예외 발생")
    void requestPayment_ProductNotFound_ThrowsException() {
        // given
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(testUser));
        when(paymentProductRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.requestPayment(paymentRequest, 1L))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("토스 결제 요청 실패 시 예외 발생")
    void requestPayment_TossRequestFailed_ThrowsException() {
        // given
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(testUser));
        when(paymentProductRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(tossPaymentClient.requestPayment(anyString(), anyString(), anyLong(), anyString(), anyString()))
                .thenThrow(new RuntimeException("토스 API 오류"));

        // when & then
        assertThatThrownBy(() -> paymentService.requestPayment(paymentRequest, 1L))
                .isInstanceOf(ApiException.class);
    }
}
