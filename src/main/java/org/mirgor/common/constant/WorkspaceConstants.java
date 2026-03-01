package org.mirgor.common.constant;

import java.util.HashMap;
import java.util.Map;

public final class WorkspaceConstants {

    private WorkspaceConstants() {
    }

    public static Map<OperationalEntityType, String> fetchEndpointMap;

    public static final String CUSTOMERS_URL_PATTERN = "/api/customerInfos/all";
    public static final String DEVICES_URL_PATTERN = "/api/deviceInfos/all";
    public static final String ASSETS_URL_PATTERN = "/api/assetInfos/all";
    public static final String WORKSPACE_PING_URL_PATTERN = "%s/api/auth/login";

    static {
        fetchEndpointMap = new HashMap<>();
        fetchEndpointMap.put(OperationalEntityType.CUSTOMER, CUSTOMERS_URL_PATTERN);
        fetchEndpointMap.put(OperationalEntityType.DEVICE, DEVICES_URL_PATTERN);
        fetchEndpointMap.put(OperationalEntityType.ASSET, ASSETS_URL_PATTERN);
    }
}
