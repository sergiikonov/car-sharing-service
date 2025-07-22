package mate.academy.carsharingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharingservice.dto.payment.CreatePaymentRequestDto;
import mate.academy.carsharingservice.dto.payment.CreatePaymentResponseDto;
import mate.academy.carsharingservice.dto.payment.PaymentDto;
import mate.academy.carsharingservice.model.User;
import mate.academy.carsharingservice.service.payment.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Controller for Payment class",
        description = "All methods of Payment controller")
public class PaymentController {
    private final PaymentService paymentService;

    @PreAuthorize("hasAnyRole('CUSTOMER','MANAGER')")
    @Operation(summary = "Get payments method",
            description = "Returns payments of customer or show all for manager")
    @GetMapping
    public Page<PaymentDto> getPayments(@RequestParam(required = false) Long id,
                                        Pageable pageable,
                                        Authentication authentication) {
        return paymentService.getPaymentsById(id,(User) authentication.getPrincipal(), pageable);
    }

    @PostMapping()
    @PreAuthorize("hasAnyRole('MANAGER','CUSTOMER')")
    @Operation(summary = "Initiate payment session",
            description = "Initiates payment session for desired rental")
    @ResponseStatus(HttpStatus.CREATED)
    public CreatePaymentResponseDto makePayment(
            @RequestBody @Valid CreatePaymentRequestDto requestDto,
            Authentication authentication,
            HttpServletRequest request
    ) {
        String baseUrl = UriComponentsBuilder
                .fromUriString(request.getRequestURL().toString())
                .replacePath(null)
                .build()
                .toUriString();
        return paymentService.createPayment(
                requestDto,
                (User) authentication.getPrincipal(),
                baseUrl
        );
    }

    @GetMapping("/success")
    @PreAuthorize("hasAnyRole('MANAGER','CUSTOMER')")
    @Operation(summary = "Successful payment method",
            description = "Handles successful payment processing through Stripe redirection")
    public ResponseEntity<Map<String, String>> paymentSuccess(
            @RequestParam("session_id") String sessionId
    ) {
        return paymentService.handleSuccess(sessionId);
    }

    @GetMapping("/cancel")
    @PreAuthorize("hasAnyRole('MANAGER','CUSTOMER')")
    @Operation(summary = "Unsuccessful payment method",
            description = "Handles NOT successful payment")
    public ResponseEntity<Map<String, String>> paymentCancel() {
        return ResponseEntity.ok(Map.of(
                "status", "canceled",
                "message", "Payment paused OR canceled. "
                        + "Please, note: payment can be accomplished within 24 hours only"
        ));
    }
}
