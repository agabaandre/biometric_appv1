
package ug.app.ihrisbiometric.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Employee {

    @SerializedName("surname")
    @Expose
    private String surname;
    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("othername")
    @Expose
    private String othername;
    @SerializedName("job")
    @Expose
    private String job;
    @SerializedName("ihris_pid")
    @Expose
    private String ihrisPid;

    /**
     * No args constructor for use in serialization
     *
     */
    public Employee() {
    }

    /**
     *
     * @param ihrisPid
     * @param job
     * @param surname
     * @param firstname
     * @param othername
     */
    public Employee(String surname, String firstname, String othername, String job, String ihrisPid) {
        super();
        this.surname = surname;
        this.firstname = firstname;
        this.othername = othername;
        this.job = job;
        this.ihrisPid = ihrisPid;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getOthername() {
        return othername;
    }

    public void setOthername(String othername) {
        this.othername = othername;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getIhrisPid() {
        return ihrisPid;
    }

    public void setIhrisPid(String ihrisPid) {
        this.ihrisPid = ihrisPid;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "surname='" + surname + '\'' +
                ", firstname='" + firstname + '\'' +
                ", othername='" + othername + '\'' +
                ", job='" + job + '\'' +
                ", ihrisPid='" + ihrisPid + '\'' +
                '}';
    }
}
