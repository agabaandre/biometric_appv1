package ug.app.ihrisbiometric.extra;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClockStatus {
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;

    public ClockStatus() {
    }

    public ClockStatus(Boolean error, String message) {
        this.error = error;
        this.message = message;
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

    @Override
    public String toString() {
        return "ClockStatus{" +
                "error=" + error +
                ", message='" + message + '\'' +
                '}';
    }
}
