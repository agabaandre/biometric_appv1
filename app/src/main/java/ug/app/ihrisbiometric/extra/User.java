package ug.app.ihrisbiometric.extra;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("facility")
    @Expose
    private String facility;
    @SerializedName("facility_id")
    @Expose
    private String facilityId;

    public User() {
    }

    public User(String facility, String facilityId) {
        this.facility = facility;
        this.facilityId = facilityId;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

}
