package com.bdb.lottery.utils.regex

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2017/03/13
 * desc  : constants of regex
</pre> *
 */
object REGEX {
    /**
     * Regex of simple mobile.
     */
    const val REGEX_MOBILE_SIMPLE = "^[1]\\d{10}$"

    /**
     * Regex of exact mobile.
     *
     * china mobile: 134(0-8), 135, 136, 137, 138, 139, 147, 150, 151, 152, 157, 158, 159, 165, 172, 178, 182, 183, 184, 187, 188, 198
     *
     * china unicom: 130, 131, 132, 145, 155, 156, 166, 167, 171, 175, 176, 185, 186
     *
     * china telecom: 133, 153, 162, 173, 177, 180, 181, 189, 199, 191
     *
     * global star: 1349
     *
     * virtual operator: 170
     */
    const val REGEX_MOBILE_EXACT =
        "^((13[0-9])|(14[57])|(15[0-35-9])|(16[2567])|(17[01235-8])|(18[0-9])|(19[189]))\\d{8}$"

    /**
     * Regex of telephone number.
     */
    const val REGEX_TEL = "^0\\d{2,3}[- ]?\\d{7,8}$"

    /**
     * Regex of id card number which length is 15.
     */
    const val REGEX_ID_CARD15 = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$"

    /**
     * Regex of id card number which length is 18.
     */
    const val REGEX_ID_CARD18 =
        "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$"

    /**
     * Regex of email.
     */
    const val REGEX_EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$"

    /**
     * Regex of url.
     */
    const val REGEX_URL = "[a-zA-z]+://[^\\s]*"

    /**
     * Regex of Chinese character.
     */
    const val REGEX_ZH = "^[\\u4e00-\\u9fa5]+$"

    /**
     * Regex of username.
     *
     * scope for "a-z", "A-Z", "0-9", "_", "Chinese character"
     *
     * can't end with "_"
     *
     * length is between 6 to 20
     */
    const val REGEX_USERNAME = "^[\\w\\u4e00-\\u9fa5]{6,20}(?<!_)$"

    /**
     * Regex of date which pattern is "yyyy-MM-dd".
     */
    const val REGEX_DATE =
        "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$"

    /**
     * Regex of ip address.
     */
    const val REGEX_IP = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)"
    ///////////////////////////////////////////////////////////////////////////
    // The following come from http://tool.oschina.net/regex
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Regex of double-byte characters.
     */
    const val REGEX_DOUBLE_BYTE_CHAR = "[^\\x00-\\xff]"

    /**
     * Regex of blank line.
     */
    const val REGEX_BLANK_LINE = "\\n\\s*\\r"

    /**
     * Regex of QQ number.
     */
    const val REGEX_QQ_NUM = "[1-9][0-9]{4,}"

    /**
     * Regex of postal code in China.
     */
    const val REGEX_CHINA_POSTAL_CODE = "[1-9]\\d{5}(?!\\d)"

    /**
     * Regex of integer.
     */
    const val REGEX_INTEGER = "^(-?[1-9]\\d*)|0$"

    /**
     * Regex of positive integer.
     */
    const val REGEX_POSITIVE_INTEGER = "^[1-9]\\d*$"

    /**
     * Regex of negative integer.
     */
    const val REGEX_NEGATIVE_INTEGER = "^-[1-9]\\d*$"

    /**
     * Regex of non-negative integer.
     */
    const val REGEX_NOT_NEGATIVE_INTEGER = "^[1-9]\\d*|0$"

    /**
     * Regex of non-positive integer.
     */
    const val REGEX_NOT_POSITIVE_INTEGER = "^-[1-9]\\d*|0$"

    /**
     * Regex of positive float.
     */
    const val REGEX_FLOAT = "^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$"

    /**
     * Regex of positive float.
     */
    const val REGEX_POSITIVE_FLOAT = "^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*$"

    /**
     * Regex of negative float.
     */
    const val REGEX_NEGATIVE_FLOAT = "^-[1-9]\\d*\\.\\d*|-0\\.\\d*[1-9]\\d*$"

    /**
     * Regex of positive float.
     */
    const val REGEX_NOT_NEGATIVE_FLOAT = "^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0$"

    /**
     * Regex of negative float.
     */
    const val REGEX_NOT_POSITIVE_FLOAT =
        "^(-([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*))|0?\\.0+|0$" ///////////////////////////////////////////////////////////////////////////
    // If u want more please visit http://toutiao.com/i6231678548520731137
    ///////////////////////////////////////////////////////////////////////////
}