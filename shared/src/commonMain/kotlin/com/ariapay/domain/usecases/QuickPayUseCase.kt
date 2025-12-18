package com.ariapay.domain.usecases

import com.ariapay.domain.model.NfcPaymentData
import com.ariapay.domain.model.PaymentResult

class QuickPayUseCase(
    private val getDefaultCardUseCase: GetDefaultCardUseCase,
    private val processNfcPaymentUseCase: ProcessNfcPaymentUseCase
) {
    suspend operator fun invoke(
        amount: Double,
        merchantId: String,
        merchantName: String
    ): Result<PaymentResult> {
        val cardResult = getDefaultCardUseCase()
        if (cardResult.isFailure) return Result.failure(Exception("No payment card available"))

        val card = cardResult.getOrNull() ?: return Result.failure(Exception("No default card set"))
        if (!card.nfcEnabled) return Result.failure(Exception("NFC not enabled for this card"))

        val nfcData = NfcPaymentData(
            tokenId = card.tokenId,
            cardId = card.id,
            encryptedPayload = "ENC_${card.tokenId}_${amount}",
            timestamp = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        )

        return processNfcPaymentUseCase(nfcData, amount, merchantId, merchantName)
    }
}