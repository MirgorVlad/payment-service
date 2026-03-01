package org.mirgor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mirgor.common.dto.Snapshot;
import org.mirgor.service.SnapshotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/snapshots")
public class SnapshotController {

    private final SnapshotService snapshotService;

    @PostMapping
    public ResponseEntity<Snapshot> createSnapshot(@RequestBody @Valid Snapshot snapshotDto) {
        return new ResponseEntity<>(snapshotService.createSnapshot(snapshotDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Snapshot>> getAllSnapshots(@RequestParam(required = false) Long workspaceId) {
        return new ResponseEntity<>(snapshotService.getAllSnapshots(workspaceId), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Snapshot> getSnapshotById(@PathVariable Long id) {
        return snapshotService.getSnapshotById(id)
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Snapshot> updateSnapshot(@PathVariable Long id, @RequestBody @Valid Snapshot snapshot) {
        return ResponseEntity.ok(snapshotService.updateSnapshot(id, snapshot));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSnapshot(@PathVariable Long id) {
        snapshotService.deleteSnapshot(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
