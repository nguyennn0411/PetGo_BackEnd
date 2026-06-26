package com.example.petgo.service;

import com.example.petgo.dto.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface WalletService {
        WalletResponse getMyWallet(HttpServletRequest request);

        List<WalletTransactionResponse> getMyTransactions(HttpServletRequest request);

        WalletTransactionResponse createTopUp(HttpServletRequest request, WalletTopUpRequest topUpRequest);

        WalletTransactionResponse verifyTopUp(HttpServletRequest request, Long transactionId);

        WalletTransactionResponse transfer(HttpServletRequest request, WalletTransferRequest transferRequest);

        WalletTransactionResponse requestWithdraw(HttpServletRequest request, WalletWithdrawRequest withdrawRequest);

        List<WalletTransactionResponse> getPendingAdminTransactions(HttpServletRequest request);

        List<WalletTransactionResponse> getSystemWalletTransactions(HttpServletRequest request);

        WalletTransactionResponse reviewAdminTransaction(HttpServletRequest request, Long transactionId,
                        WalletAdminReviewRequest reviewRequest);

        WalletResponse updateWalletStatus(HttpServletRequest request, Long userId,
                        WalletStatusUpdateRequest statusRequest);

        WalletTransactionResponse systemWithdraw(HttpServletRequest request, WalletWithdrawRequest withdrawRequest);

        WalletResponse getSystemWallet(HttpServletRequest request);

        void handlePayOsWebhookOrderCode(Long orderCode);

        void ensureWalletForUser(Long userId);
}