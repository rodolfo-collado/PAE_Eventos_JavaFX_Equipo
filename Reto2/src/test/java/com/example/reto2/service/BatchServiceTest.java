package com.example.reto2.service;

import com.example.reto2.model.Batch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BatchServiceTest {

    private BatchService batchService;

    @BeforeEach
    void setUp() {
        batchService = new BatchService();
    }

    @Test
    void testAddBatch_Success() {
        Batch batch = new Batch("Farm Paradise", "BATCH-001", new Date(), 150.5);
        boolean result = batchService.addBatch(batch);

        assertTrue(result);
        assertEquals(1, batchService.getBatchCount());
        assertFalse(batchService.isEmpty());
    }

    @Test
    void testAddBatch_NullOrEmptyId() {
        assertFalse(batchService.addBatch(null));

        Batch emptyIdBatch = new Batch("Farm Paradise", "", new Date(), 100.0);
        assertFalse(batchService.addBatch(emptyIdBatch));

        Batch nullIdBatch = new Batch("Farm Paradise", null, new Date(), 100.0);
        assertFalse(batchService.addBatch(nullIdBatch));
    }

    @Test
    void testAddBatch_DuplicateId() {
        Batch batch1 = new Batch("Provider A", "BATCH-001", new Date(), 100.0);
        Batch batch2 = new Batch("Provider B", "batch-001", new Date(), 200.0);

        assertTrue(batchService.addBatch(batch1));
        assertFalse(batchService.addBatch(batch2));
        assertEquals(1, batchService.getBatchCount());
    }

    @Test
    void testFindByIdAndCode() {
        Batch batch = new Batch("Northern Coffee", "BATCH-002", new Date(), 80.0);
        batchService.addBatch(batch);

        Batch found = batchService.findById("BATCH-002");
        assertNotNull(found);
        assertEquals("Northern Coffee", found.getProvider());

        assertNotNull(batchService.findById("batch-002"));
        assertEquals(found, batchService.findByCode("BATCH-002"));

        assertNull(batchService.findById("NON_EXISTENT"));
        assertNull(batchService.findById(null));
        assertNull(batchService.findById("   "));
    }

    @Test
    void testFindByProvider() {
        batchService.addBatch(new Batch("Hope Farm", "B01", new Date(), 50.0));
        batchService.addBatch(new Batch("Northern Cooperative", "B02", new Date(), 60.0));
        batchService.addBatch(new Batch("San Jose Farm", "B03", new Date(), 70.0));

        List<Batch> matches = batchService.findByProvider("farm");
        assertEquals(2, matches.size());

        List<Batch> all = batchService.findByProvider("");
        assertEquals(3, all.size());
    }

    @Test
    void testUpdateBatch() {
        Batch batch = new Batch("Original Provider", "BATCH-003", new Date(), 100.0);
        batchService.addBatch(batch);

        Batch updated = new Batch("Updated Provider", "BATCH-003", new Date(), 120.0);
        boolean success = batchService.updateBatch(updated);

        assertTrue(success);
        Batch current = batchService.findById("BATCH-003");
        assertEquals("Updated Provider", current.getProvider());
        assertEquals(120.0, current.getWeight());

        Batch nonExistent = new Batch("Other", "NON-EXISTENT", new Date(), 50.0);
        assertFalse(batchService.updateBatch(nonExistent));
        assertFalse(batchService.updateBatch(null));
    }

    @Test
    void testDeleteByIdAndCode() {
        Batch batch = new Batch("Provider C", "BATCH-004", new Date(), 90.0);
        batchService.addBatch(batch);

        assertTrue(batchService.existsById("BATCH-004"));
        assertTrue(batchService.deleteById("batch-004"));
        assertFalse(batchService.existsById("BATCH-004"));
        assertEquals(0, batchService.getBatchCount());

        assertFalse(batchService.deleteById("BATCH-004"));
        assertFalse(batchService.deleteById(null));
    }

    @Test
    void testDeleteBatch() {
        Batch batch = new Batch("Provider D", "BATCH-005", new Date(), 110.0);
        batchService.addBatch(batch);

        assertTrue(batchService.deleteBatch(batch));
        assertFalse(batchService.deleteBatch(batch));
        assertFalse(batchService.deleteBatch(null));
    }

    @Test
    void testClear() {
        batchService.addBatch(new Batch("P1", "B01", new Date(), 10.0));
        batchService.addBatch(new Batch("P2", "B02", new Date(), 20.0));
        assertEquals(2, batchService.getBatchCount());

        batchService.clear();
        assertEquals(0, batchService.getBatchCount());
        assertTrue(batchService.isEmpty());
    }
}
