package com.tohelp.specialist.settings;

import com.tohelp.specialist.BuildConfig;

import java.util.regex.Pattern;

public class Variable
{
    public static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +
                    "(?=.*[a-zA-Z])"+
                    "(?=\\S+$)"+
                    ".{8,}"+
                    "$");

    public static final Pattern PHONE_PATTERN =
            Pattern.compile("\\D" + //(
                    "\\d{3}" + //код
                    "\\D" + //)
                    "\\s" + //пробел
                    "\\d{3}" + //первые три цифры номера
                    "\\s" + //пробел
                    "\\d{2}" + //вторые две цифры номера
                    "\\s" + //пробел
                    "\\d{2}" //третьи две цифры номера
            );

    public static final char[]array_for_password =
            {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '!','@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '+', '=', '?', '{', '}', '[', ']', '.', ','};

    public static final String store="play_market";
    //public static final String store="app_gallery";
    public static final String APP_PREFERENCES="my_profile_specialist";
    public static final String APP_NOTIFICATIONS="my_notifications";
    public static final String url= BuildConfig.URL;
    public static final String mobile_url=url+"articles_phone/";
    public static final String tablet_url=url+"articles_tablet/";
    public static final String forgot_password_url=url+"forgot_password.php";
    public static final String login_url=url+"login.php";
    public static final String status_of_busy_url=url+"status_of_busy.php";
    public static final String send_my_profile_url=url+"send_my_profile.php";
    public static final String send_password_url=url+"send_password.php";
    public static final String get_my_profile_url=url+"get_my_profile.php";
    public static final String register_url=url+"register.php";
    public static final String compare_email_url=url+"compare_email.php";
    public static final String compare_password_url=url+"compare_password.php";
    //create get_requests
    public static final String get_requests_main_url=url+"get_requests.php?id_of_specialist=";
    public static final String get_requests_first_add_url="&type_of_specialist=";
    public static final String get_requests_second_add_url="&subject_of_country=";
    public static final String get_requests_third_add_url="&access_token=";
    //create requests_personal
    public static final String requests_personal_url_main=url+"requests_personal.php?id_of_specialist=";
    public static final String requests_personal_url_add="&access_token=";
    public static final String requests_personal_confirm_url=url+"requests_personal_confirm.php";
    //create requests_for_all
    public static final String requests_for_all_url_main=url+"requests_for_all.php?type_of_specialist=";
    public static final String requests_for_all_url_first_add="&subject_of_country=";
    public static final String requests_for_all_url_second_add="&id=";
    public static final String requests_for_all_url_third_add="&access_token=";
    public static final String requests_for_all_confirm_url=url+"requests_for_all_confirm.php";
    //create requests_in_progress
    public static final String request_in_progress_url_main=url+"requests_in_progress.php?id_of_specialist=";
    public static final String request_in_progress_url_add="&access_token=";
    //create request_agreement
    public static final String request_agreement_url="https://tohelptohelp.ru/agreement/request_agreement.php";
    //create prepare_request
    public static final String prepare_request_url="https://tohelptohelp.ru/notification/prepare_request.php";
    //create list_of_graduates
    public static final String list_of_graduates_url_main=url+"list_of_graduates.php?id=";
    public static final String list_of_graduates_url_first_add="&access_token=";
    public static final String list_of_graduates_url_second_add="&graduate=";
    public static final String list_of_graduates_url_third_add="&subject_of_country=";
    public static final String list_of_graduates_url_fourth_add="&city=";
    public static final String list_of_graduates_url_fifth_add="&child_home=";
    //create request_tech_support
    public static final String request_tech_support_url="https://tohelptohelp.ru/tech_support/request_tech_support.php";
    //html
    public static final String document_phone_url=mobile_url+"processing_confidential_data.html";
    public static final String document_tablet_url=tablet_url+"processing_confidential_data.html";
    public static final String about_application_phone_url=mobile_url+"about_application.html";
    public static final String about_application_tablet_url=tablet_url+"about_application.html";
    //---------------------------------------------------------------------------------------------
    public static final String place_of_photo_url=url+"images_specialist/";
    public static final String update_image_url=url+"image_update.php";
    public static final String remove_image_url=url+"image_remove.php";
    public static final String photo_of_user_url="https://tohelptohelp.ru/tohelp/images_tohelp/";
    //----------------------------------------------------------------------------------------------------------------------
    public static final String account_delete_url=url+"account_delete.php";
    public static final String account_requests_reset_url=url+"account_requests_reset.php";
    public static final String account_restore_url=url+"account_restore.php";
    //SplashScreen
    public static final String update_version_of_app=url+"update_version_of_app.php";
    //--------------------------------------------------------------------------------------------
    public static final String secretKey="+KBZwCJt2pW/Mm8pAZ4NUA";
    public static final String AES="AES";
    public static final String SHA="SHA-256";
    public static final String UTF="UTF-8";
    public static final int REFRESH_PHOTO_CODE=1;
    public static final int DELETE_PHOTO_CODE=2;
    public static final int DOWNLOAD_FILE_CODE=3;
    public static final int ATTACH_FILE_CODE=4;
    public static final int ATTACH_SCREENSHOT_CODE=5;
    public static final int MIN_COUNT_OF_CLICK=0;
    public static final int COUNT_OF_CLICK=20;
    public static final int MAX_COUNT_OF_CLICK=100;
}
