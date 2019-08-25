package com.mega.matrimony.Utility;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Utils {

    public static final int DRAWABLE_SIZE = 20;
    public static final int REQUEST_TIMEOUT = 60000;

    public static final String USER_AGENT = "NI-AAPP";

    public static final String Base_url = "https://www.usnazranimatrimony.com/";
    //public static final String Base_url = "https://www.mega-matrimony.narjisdemos.com/";
    //public static final String Base_url = "http://192.168.1.111/mega_matrimony/original_script/v1.0/";//Working
    //public static final String Base_url = "http://192.168.1.111/mega_matrimony/original_script/";//Deprecated
    public static final String get_token = Base_url + "common_request/get_tocken";//?user_agent=NI-AAPP
    public static final String login = Base_url + "login/check_login_service";
    public static final String forgot = Base_url + "login/check_email_forgot";
    public static final String common_list = Base_url + "common_request/get_common_list_ddr";
    public static final String common_depedent_list = Base_url + "common_request/get_list_json";
    public static final String register_first = Base_url + "register/save_register";
    public static final String register_step = Base_url + "register/save_register_step";
    public static final String site_data = Base_url + "common_request/get_site_data";
    public static final String register_upload_profile_image = "register/save_register_step";
    public static final String contact_form = Base_url + "contact/submit_contact";
    public static final String get_my_profile = Base_url + "my-profile/get_my_profile";
    public static final String recent_join = Base_url + "my-dashboard/recent-profile";
    public static final String recent_login = Base_url + "my-dashboard/recently-login";
    public static final String photo_password_request = Base_url + "search/send_photo_password_request";
    public static final String check_plan = Base_url + "premium-member/current-plan";
    public static final String like_profile = Base_url + "search/member-like";
    public static final String send_interest = Base_url + "search/express-interest-sent";
    public static final String shortlist_user = Base_url + "search/add_remove_shortlist_app";
    public static final String block_user = Base_url + "search/blocklist";
    public static final String change_password = Base_url + "privacy-setting/change_password";
    public static final String saved_search = Base_url + "search/saved/";
    public static final String delete_saved_search = Base_url + "search/delete-saved-search";
    public static final String delete_profile = Base_url + "my-profile/send-delete-reason-admin";
    public static final String contact_setting = Base_url + "privacy-setting/contact_privacy_setting";
    public static final String block_list = Base_url + "my-profile/block-list/";
    public static final String all_cms = Base_url + "cms/get_cms";
    public static final String shortlist_profile = Base_url + "my-profile/short_list_app/";
    public static final String like_profile_list = Base_url + "my-profile/like_unlike_profile_app/";
    public static final String i_viewed_list = Base_url + "my-profile/i_viewed_profile_app/";
    public static final String who_viewed_list = Base_url + "my-profile/who_viewed_profile_app/";
    public static final String photo_pass_request_received = Base_url + "my-profile/photo_pass_request_received_app/";
    public static final String photo_pass_request_send = Base_url + "my-profile/photo_pass_request_sent_app/";
    public static final String delete_request = Base_url + "my-profile/delete_request";
    public static final String add_video = Base_url + "upload/add_video";
    public static final String delete_id_proof_photo = Base_url + "upload/delete_id_proof_photo";
    public static final String upload_id_proof_photo = Base_url + "upload/upload_id_proof_photo";
    public static final String upload_horoscope = Base_url + "upload/upload_horoscope_photo";
    public static final String save_search = Base_url + "search/add_saved_search";
    public static final String message_list = Base_url + "message/get_message_list";
    public static final String newmessage_list = Base_url + "message/massages_list_api";
    public static final String conversation = Base_url + "message/conversation_list_api";
    public static final String update_status = Base_url + "message/update_status";
    public static final String express_interest = Base_url + "express_interest/index/";
    public static final String action_update_status = Base_url + "express-interest/action_update_status";
    public static final String upload_photo_new = Base_url + "modify_photo/upload_photo_new";
    public static final String delete_photo = Base_url + "modify_photo/delete_photo";
    public static final String set_profile_pic = Base_url + "modify_photo/set_profile_pic";
    public static final String photo_visibility_status = Base_url + "privacy-setting/photo_visibility_for_mobile_app";
    public static final String search_result = Base_url + "search/result/";
    public static final String plan_list = Base_url + "premium_member/get_plan_data";
    public static final String check_coupan = Base_url + "premium_member/check-coupan";
    public static final String save_matches = Base_url + "matches/save_matches";
    public static final String search_now = Base_url + "matches/search_now/";
    public static final String other_user_profile = Base_url + "search/view_profile_app";
    public static final String edit_profile = Base_url + "my-profile/save-profile";
    public static final String view_contact = Base_url + "search/view-contact-details";
    public static final String contact_admin = Base_url + "Contact/send_msg_admin";
    public static final String send_message = Base_url + "message/send_message";
    public static final String get_payment_method = Base_url + "premium_member/get_payment_method";
    public static final String payment_url = Base_url + "premium-member/payment-process-mobile-app/";
    public static final String payment_fail = Base_url + "premium-member/payment_fail_mobile_app_redirect";
    public static final String payment_success = Base_url + "premium-member/payment_success_mobile_app_redirect";
    public static final String delete_user_message = Base_url + "message/delete_user_message_list_api";
    public static final String reject_request = Base_url + "my-profile/reject_request";
    public static final String delete_cover_photo = Base_url + "upload/delete_cover_photo";
    public static final String delete_msg = Base_url + "message/update_status";

    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    public static final String OUICK_TAG = "quickmessage";
    public static final String CHAT_TAG = "chatting";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
    /*
      Unpaid Act
      NE411747
      111111
     */


    public static final String TYPE_SEARCH_QUICK = "Quick Search";
    public static final String TYPE_SEARCH_ADVANCE = "Advance Search";
    public static final String TYPE_SEARCH_ID = "Id Search";
    public static final String TYPE_SEARCH_KEYWORD = "Keyword Search";

    public static final String DIRECTORY_NAME = "Mega Matrimony";

    public static final String BIRTH_DATE_FORMAT = "dd-MM-yyyy";
    public static final String BIRTH_DATE_UPLOAD_FORMAT = "yyyy-M-dd";
    public static final SimpleDateFormat filterDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
}
