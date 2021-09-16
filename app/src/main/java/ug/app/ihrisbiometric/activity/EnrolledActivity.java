package ug.app.ihrisbiometric.activity;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;


import java.util.List;

import ug.app.ihrisbiometric.R;
import ug.app.ihrisbiometric.adapter.EmployeeAdapter;
import ug.app.ihrisbiometric.extra.APIService;
import ug.app.ihrisbiometric.extra.APIUtils;
import ug.app.ihrisbiometric.extra.SessionHandler;
import ug.app.ihrisbiometric.model.Employee;
import ug.app.ihrisbiometric.model.EmployeeResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnrolledActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeContainer;

    EmployeeAdapter employeeAdapter;

    APIService apiService;

    SessionHandler session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrolled);

        session = new SessionHandler(getApplicationContext());

        session.checkLogin();

        apiService = APIUtils.getAPIService();

        apiService.getEmployeeData(0).enqueue(new Callback<EmployeeResponse>() {
            @Override
            public void onResponse(Call<EmployeeResponse> call, Response<EmployeeResponse> response) {

                if(response.body() != null) {

                    EmployeeResponse employeeResponse = response.body();

                    if(!employeeResponse.getError()) {
                        Toast.makeText(EnrolledActivity.this, "No errors found", Toast.LENGTH_SHORT).show();

                        List<Employee> employees = employeeResponse.getEmployees();

                        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_employee_list);
                        recyclerView.setHasFixedSize(true);

                        layoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(layoutManager);
                        adapter = new EmployeeAdapter(employees);
                        recyclerView.setAdapter(adapter);

                    } else {
                        Toast.makeText(EnrolledActivity.this, "Found lots of errors", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(EnrolledActivity.this, "Received no response from server", Toast.LENGTH_SHORT).show();
                }




            }

            @Override
            public void onFailure(Call<EmployeeResponse> call, Throwable t) {
                Toast.makeText(EnrolledActivity.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
            }
        });

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                // fetchEnrolledAsync(0);

                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        // Stop animation (This will be after 3 seconds)
                        swipeContainer.setRefreshing(false);
                    }
                }, 4000); // Delay in millis

            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    public void fetchEnrolledAsync(int page) {
        Toast.makeText(this, "Retrieving data from api...", Toast.LENGTH_SHORT).show();
//        apiService.getEmployeeData(page).enqueue(new Callback<EmployeeResponse>() {
//            @Override
//            public void onResponse(Call<EmployeeResponse> call, Response<EmployeeResponse> response) {
//                if(response.isSuccessful()) {
//                    if(!response.body().getError()) {
//                        employeeAdapter.clear();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<EmployeeResponse> call, Throwable t) {
//
//            }
//        });
    }
}
