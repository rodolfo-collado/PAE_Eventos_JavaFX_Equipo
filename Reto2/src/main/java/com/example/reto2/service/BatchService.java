package com.example.reto2.service;

import com.example.reto2.model.Batch;

import java.util.ArrayList;
import java.util.List;

public class BatchService {

    private final List<Batch> batches;

    public BatchService() {
        this.batches = new ArrayList<>();
    }

    public boolean addBatch(Batch batch) {
        if (batch == null || batch.getId() == null || batch.getId().trim().isEmpty()) {
            return false;
        }
        if (findById(batch.getId()) != null) {
            return false;
        }
        return batches.add(batch);
    }

    public List<Batch> getAllBatches() {
        return new ArrayList<>(batches);
    }

    public Batch findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        String cleanId = id.trim();
        for (Batch batch : batches) {
            if (batch.getId() != null && batch.getId().equalsIgnoreCase(cleanId)) {
                return batch;
            }
        }
        return null;
    }

    public Batch findByCode(String code) {
        return findById(code);
    }

    public List<Batch> findByProvider(String provider) {
        if (provider == null || provider.trim().isEmpty()) {
            return getAllBatches();
        }
        String clean = provider.trim().toLowerCase();
        List<Batch> matches = new ArrayList<>();
        for (Batch batch : batches) {
            if (batch.getProvider() != null && batch.getProvider().toLowerCase().contains(clean)) {
                matches.add(batch);
            }
        }
        return matches;
    }

    public boolean existsById(String id) {
        return findById(id) != null;
    }

    public int getBatchCount() {
        return batches.size();
    }

    public boolean isEmpty() {
        return batches.isEmpty();
    }

    public boolean updateBatch(Batch updatedBatch) {
        if (updatedBatch == null || updatedBatch.getId() == null || updatedBatch.getId().trim().isEmpty()) {
            return false;
        }
        String cleanId = updatedBatch.getId().trim();
        for (int i = 0; i < batches.size(); i++) {
            Batch current = batches.get(i);
            if (current.getId() != null && current.getId().equalsIgnoreCase(cleanId)) {
                batches.set(i, updatedBatch);
                return true;
            }
        }
        return false;
    }

    public boolean deleteById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        String cleanId = id.trim();
        return batches.removeIf(b -> b.getId() != null && b.getId().equalsIgnoreCase(cleanId));
    }

    public boolean deleteByCode(String code) {
        return deleteById(code);
    }

    public boolean deleteBatch(Batch batch) {
        if (batch == null) {
            return false;
        }
        return batches.remove(batch);
    }

    public void clear() {
        batches.clear();
    }
}
