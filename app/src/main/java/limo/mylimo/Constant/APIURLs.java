package limo.mylimo.Constant;

/**
 * Created by Shoaib Anwar on 12-Feb-18.
 */

public class APIURLs {
    //http://www.ranglerz.net/mylimo/api/register_user_sms

    //public static final String DOMAIN_URL = "http://www.ranglerz.net/mylimo/api/";
    public static final String DOMAIN_URL = "http://www.mylimo.pk/admin/api/";
    public static final String loginURL = DOMAIN_URL+"login";
    public static final String regitrationURL = DOMAIN_URL+"register";
    public static final String userHistory = DOMAIN_URL+"perticularHistory";
    public static final String submitOrder = DOMAIN_URL+"orders";
    public static final String SMS_REQUEST = DOMAIN_URL+"register_user_sms";
    public static final String FORGOT_PASSWORD = DOMAIN_URL+"user_forgot_password";
    public static final String FEEDBACK = DOMAIN_URL+"user_feedback";
    public static final String CANCEL_ORDER = DOMAIN_URL+"order_cancel";

    public static final String gettingSeesionIdForSMS = "https://telenorcsms.com.pk:27677/corporate_sms2/api/auth.jsp?msisdn=923468203792&password=0987";
}
