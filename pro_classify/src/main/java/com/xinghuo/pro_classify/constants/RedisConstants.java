package com.xinghuo.pro_classify.constants;

import java.util.concurrent.TimeUnit;

public class RedisConstants {
    public static final String LITTER_CLASSIFY_KEY_PREFIX = "classify:";
    public static final Long TTL_VALUE = 1L;
    public static final TimeUnit TTL_UNIT = TimeUnit.HOURS;
    public static final String BLACKLIST_PREFIX = "blacklist:ip:";
    public static final String IP_REQUEST_COUNT_PREFIX = "ip:request:count:";
    public static final String LOCK_KEY = "schedule:remove_useless_image_and_zip_data_set_lock";
    public static final Long LOCK_TIMEOUT = 10 * 60L;
}
