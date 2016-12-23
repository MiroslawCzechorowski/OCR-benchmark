package pl.com.czechorowski.benchmkaranyway;

/**
 * Created by MiroslawCzechorowski on 17.12.2016.
 */

public class Config {
    //Address of scripts of the CRUD
    public static final String URL_TOKEN="http://ocr.waszkowiak.pl/api/token/get";
    public static final String URL_RESULTS="http://ocr.waszkowiak.pl/api/v1/results";
    public static final String URL_CHALLANGES="http://ocr.waszkowiak.pl/api/v1/challenges";
    public static final String URL_ANSWERS="http://ocr.waszkowiak.pl/api/v1/answers";
    //Keys that will be used to send the request for token
    public static final String KEY_USER_ID = "username";
    public static final String KEY_PASSWORD = "password";
}
