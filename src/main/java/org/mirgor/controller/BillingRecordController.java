package org.mirgor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mirgor.common.dto.BillingRecord;
import org.mirgor.service.BillingRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/billing")
public class BillingRecordController {

    private final BillingRecordService billingRecordService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BillingRecord> createBillingRecord(@RequestBody @Valid BillingRecord billingRecord) {
        return new ResponseEntity<>(billingRecordService.createBillingRecord(billingRecord), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BillingRecord>> getAllBillingRecords(@RequestParam(required = false) Long workspaceId) {
        return new ResponseEntity<>(billingRecordService.getAllBillingRecords(workspaceId), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillingRecord> getBillingRecordById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(billingRecordService.getBillingRecordById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BillingRecord> updateBillingRecord(@PathVariable Long id, @RequestBody @Valid BillingRecord billingRecord) {
        try {
            return ResponseEntity.ok(billingRecordService.updateBillingRecord(id, billingRecord));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBillingRecord(@PathVariable Long id) {
        try {
            billingRecordService.deleteBillingRecord(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
