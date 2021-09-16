package ug.app.ihrisbiometric.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EmployeeResponse {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("employees")
    @Expose
    private List<Employee> employees = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public EmployeeResponse() {
    }

    /**
     *
     * @param error
     * @param employees
     */
    public EmployeeResponse(Boolean error, List<Employee> employees) {
        super();
        this.error = error;
        this.employees = employees;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    @Override
    public String toString() {
        return "EmployeeResponse{" +
                "error=" + error +
                ", employees=" + employees +
                '}';
    }
}

