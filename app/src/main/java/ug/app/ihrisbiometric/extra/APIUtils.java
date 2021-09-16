package ug.app.ihrisbiometric.extra;

public class APIUtils {

    private static final String BASE_URL = "https://hris2.health.go.ug/";

    private APIUtils() {
    }

    public static APIService getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
