package dev.smartconsumer.common;

public class Const {
    // Session Keys
    public static final String SESSION_USER = "LOGIN_USER";

    // View Paths
    public static final String VIEW_PREFIX = "/WEB-INF/view/";

    // 10개 소비 카테고리: [컬럼명, 한글명]
    public static final String[][] CATEGORIES = {
        {"INTERIOR_AM", "인테리어"},
        {"INSUHOS_AM",  "보험/병원"},
        {"OFFEDU_AM",   "사무/교육"},
        {"TRVLEC_AM",   "여행/여가"},
        {"FSBZ_AM",     "외식"},
        {"SVCARC_AM",   "서비스"},
        {"DIST_AM",     "유통"},
        {"PLSANIT_AM",  "건강/위생"},
        {"CLOTHGDS_AM", "의류/잡화"},
        {"AUTO_AM",     "자동차"}
    };

    // AVERAGE_DATA_F 컬럼명 (카테고리 순서와 동일)
    public static final String[] AVG_COLUMNS = {
        "INTERIOR_AM_MEAN", "INSUHOS_AM_MEAN", "OFFEDU_AM_MEAN",
        "TRVLEC_AM_MEAN",   "FSBZ_AM_MEAN",    "SVCARC_AM_MEAN",
        "DIST_AM_MEAN",     "PLSANIT_AM_MEAN",  "CLOTHGDS_AM_MEAN",
        "AUTO_AM_MEAN"
    };
}
