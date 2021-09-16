package ug.app.ihrisbiometric.extra;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthUser {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("user")
    @Expose
    private User user;

    public AuthUser(Boolean error, String message, String status, User user) {
        super();
        this.error = error;
        this.message = message;
        this.status = status;
        this.user = user;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
