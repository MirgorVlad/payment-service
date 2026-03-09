package org.mirgor.service.billing;

import jakarta.transaction.Transactional;
import org.mirgor.service.BaseIntegrationTest;

@Transactional  // rolls back after each test
public class BillingGenerationFlowIntegrationTest extends BaseIntegrationTest {
    //TODO use WireMock
}
