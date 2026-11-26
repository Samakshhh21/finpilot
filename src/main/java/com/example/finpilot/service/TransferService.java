
package com.example.finpilot.service;

import com.example.finpilot.dto.request.TransferRequest;
import com.example.finpilot.dto.response.TransferResponse;
import com.example.finpilot.entity.Transfer;
import com.example.finpilot.exception.InvalidTransferException;
import com.example.finpilot.repository.TransferRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final TransferExecutionService executionService;

    public TransferService(
            TransferRepository transferRepository,
            TransferExecutionService executionService) {
        this.transferRepository = transferRepository;
        this.executionService = executionService;
    }

    // Deliberately NOT @Transactional.
    public TransferResponse transfer(
            TransferRequest request, String idempotencyKey) {

        Transfer existing = transferRepository
                .findByIdempotencyKey(idempotencyKey)
                .orElse(null);

        if (existing != null) {
            return returnExistingOrReject(existing, request);
        }

        try {
            return executionService.execute(request, idempotencyKey);

        } catch (DataIntegrityViolationException exception) {

            // The execution transaction has already rolled back
            // before control reaches this catch block.
            Transfer duplicate = transferRepository
                    .findByIdempotencyKey(idempotencyKey)
                    .orElse(null);

            if (duplicate != null) {
                return returnExistingOrReject(duplicate, request);
            }

            throw exception;
        }
    }

    private TransferResponse returnExistingOrReject(
            Transfer existing, TransferRequest request) {

        boolean sameRequest =
                existing.getFromAccountId().equals(
                        request.getFromAccountId()) &&
                        existing.getToAccountId().equals(
                                request.getToAccountId()) &&
                        existing.getAmount().compareTo(
                                request.getAmount()) == 0 &&
                        Objects.equals(
                                existing.getDescription(),
                                request.getDescription());

        if (!sameRequest) {
            throw new InvalidTransferException(
                    "Idempotency key was already used with different transfer details");
        }

        return new TransferResponse(
                existing.getId(),
                existing.getDebitTransactionId(),
                existing.getCreditTransactionId(),
                existing.getFromAccountId(),
                existing.getToAccountId(),
                existing.getAmount(),
                existing.getStatus(),
                "Transfer was already processed"
        );
    }
}
