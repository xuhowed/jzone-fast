package com.jzone.common.constant;

public final class CacheKeyConstants {

    private CacheKeyConstants() {}

    private static final String PREFIX = "jzone:";

    // ---- system:dict ----
    public static final String DICT_TYPE_PREFIX = PREFIX + "system:dict:";

    public static String dictItems(String dictType) {
        return DICT_TYPE_PREFIX + "type_" + dictType;
    }

    // ---- system:menu ----
    public static final String MENU_TREE_PREFIX = PREFIX + "system:menu:";
    public static final String MENU_PERMS_PREFIX = PREFIX + "system:menu:perms_user_";

    public static String menuTreeByUser(Long userId) {
        return MENU_TREE_PREFIX + "tree_user_" + userId;
    }

    public static String menuPermsByUser(Long userId) {
        return MENU_PERMS_PREFIX + userId;
    }

    // ---- system:user ----
    public static final String USER_PREFIX = PREFIX + "system:user:";

    public static String userInfo(Long userId) {
        return USER_PREFIX + userId;
    }

    // ---- system:security (captcha & login lock) ----
    public static final String CAPTCHA_PREFIX = PREFIX + "system:captcha:";
    public static final String LOGIN_ERR_PREFIX = PREFIX + "system:login_err:";
    public static final String LOGIN_LOCK_PREFIX = PREFIX + "system:login_lock:";

    public static String captcha(String token) {
        return CAPTCHA_PREFIX + token;
    }

    public static String loginErrorCount(String username) {
        return LOGIN_ERR_PREFIX + username;
    }

    public static String loginLock(String username) {
        return LOGIN_LOCK_PREFIX + username;
    }
}
