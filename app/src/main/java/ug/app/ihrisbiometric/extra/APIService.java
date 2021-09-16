package ug.app.ihrisbiometric.extra;

import ug.app.ihrisbiometric.model.EmployeeResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIService {

    @POST("/attendance/biometric/index.php/api/login")
    @FormUrlEncoded
    Call<AuthUser> authenticate(@Field("username") String username, @Field("password") String password);

    @GET("attendance/biometric/index.php/api/enrolled")
    Call<EmployeeResponse> getEmployeeData(@Query("page") int page);
}
