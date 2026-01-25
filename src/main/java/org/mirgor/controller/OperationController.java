package org.mirgor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mirgor.dto.OperationDto;
import org.mirgor.service.OperationService;
import org.mirgor.service.mapper.OperationMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/operations")
public class OperationController {

    private final OperationService operationService;
    private final OperationMapper mapper;

    @PostMapping
    public ResponseEntity<OperationDto> createOperation(@RequestBody @Valid OperationDto operationDto) {
        var operation = mapper.fromDto(operationDto);
        var createdOperation = operationService.createOperation(operation);
        return new ResponseEntity<>(mapper.toDto(createdOperation), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OperationDto>> getAllOperations() {
        var operations = operationService.getAllOperations();
        var operationDtos = operations.stream()
                .map(mapper::toDto)
                .toList();
        return new ResponseEntity<>(operationDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OperationDto> getOperationById(@PathVariable Long id) {
        return operationService.getOperationById(id)
                .map(mapper::toDto)
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationDto> updateOperation(@PathVariable Long id, @RequestBody @Valid OperationDto operationDto) {
        var updatedOperation = operationService.updateOperation(id, mapper.fromDto(operationDto));
        var updatedOperationDto = mapper.toDto(updatedOperation);
        return ResponseEntity.ok(updatedOperationDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOperation(@PathVariable Long id) {
        operationService.deleteOperation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
