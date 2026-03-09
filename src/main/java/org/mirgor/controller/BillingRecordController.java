package org.mirgor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mirgor.common.dto.TimeInterval;
import org.mirgor.common.dto.entity.BillingRecord;
import org.mirgor.service.billing.BillingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/billing")
public class BillingRecordController {

    private final BillingService billingService;

    @PostMapping("/generate/{workspaceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public CompletableFuture<BillingRecord> billingWorkspace(@PathVariable Long workspaceId,
                                                              @RequestBody TimeInterval timeInterval) {
        return billingService.generateBillingRecord(workspaceId, timeInterval);

    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BillingRecord> createBillingRecord(@RequestBody @Valid BillingRecord billingRecord) {
        return new ResponseEntity<>(billingService.createBillingRecord(billingRecord), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BillingRecord>> getAllBillingRecords(@RequestParam(required = false) Long workspaceId) {
        return new ResponseEntity<>(billingService.getAllBillingRecords(workspaceId), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillingRecord> getBillingRecordById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(billingService.getBillingRecordById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BillingRecord> updateBillingRecord(@PathVariable Long id, @RequestBody @Valid BillingRecord billingRecord) {
        try {
            return ResponseEntity.ok(billingService.updateBillingRecord(id, billingRecord));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBillingRecord(@PathVariable Long id) {
        try {
            billingService.deleteBillingRecord(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
