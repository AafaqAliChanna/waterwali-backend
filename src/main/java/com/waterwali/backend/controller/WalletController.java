package com.waterwali.backend.controller;

import com.waterwali.backend.dto.TopUpRequest;
import com.waterwali.backend.dto.WalletResponse;
import com.waterwali.backend.security.CurrentUser;
import com.waterwali.backend.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/me")
    public ResponseEntity<WalletResponse> getMyWallet(Authentication authentication) {
        return ResponseEntity.ok(walletService.getWalletResponse(CurrentUser.id(authentication)));
    }

    @PostMapping("/topup")
    public ResponseEntity<WalletResponse> topUp(@Valid @RequestBody TopUpRequest request,
                                                 Authentication authentication) {
        return ResponseEntity.ok(walletService.topUp(CurrentUser.id(authentication), request.getAmount()));
    }
}