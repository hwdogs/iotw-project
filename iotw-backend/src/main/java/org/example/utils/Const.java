package org.example.utils;

/**
 * @author hwshou
 * @date 2025/5/19  20:41
 */
public class Const {
    public static final String JWT_BLACK_LIST = "jwt:blacklist:";

    public static final String VERIFY_EMAIL_LIMIT = "verify:email:limit:";
    public static final String VERIFY_EMAIL_DATA = "verify:email:data";

    public static final String FLOW_LIMIT_COUNTER = "flow:counter:";
    public static final String FLOW_LIMIT_BLOCK = "flow:block:";

    public static final int ORDER_CORS = -102;
    public static final int ORDER_LIMIT = -101;

    public static final Short RULE_USER = 1;

    public static final Short IS_NOT_DELETED = 0;
    public static final Short IS_DELETED = 1;
    public static final Short SEX_MAN = 1;
    public static final Short SEX_WOMAN = 0;

    public static final Short NOT_APPROVED = 0;
    public static final Short APPROVED_SUCCESSFULLY = 1;
    public static final Short APPROVED_UNSUCCESSFULLY = -1;
}
