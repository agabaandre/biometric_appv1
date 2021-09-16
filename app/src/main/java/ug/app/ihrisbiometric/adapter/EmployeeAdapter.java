package ug.app.ihrisbiometric.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ug.app.ihrisbiometric.R;
import ug.app.ihrisbiometric.model.Employee;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.MyViewHolder> {

    private List<Employee> employees;

    public EmployeeAdapter(List<Employee> employees){
        this.employees = employees;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_item, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.Surname.setText(employees.get(position).getSurname());
        holder.Firstname.setText(employees.get(position).getFirstname());
        holder.Othername.setText(employees.get(position).getOthername());
        holder.Job.setText(employees.get(position).getJob());
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Surname,Firstname, Othername, Job;
        public MyViewHolder(View itemView) {
            super(itemView);
            Surname = (TextView)itemView.findViewById(R.id.surname);
            Firstname = (TextView) itemView.findViewById(R.id.firstname);
            Othername = (TextView)itemView.findViewById(R.id.othername);
            Job = (TextView) itemView.findViewById(R.id.job);
        }
    }

    public void clear() {
        employees.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Employee> list) {
        employees.addAll(list);
        notifyDataSetChanged();
    }

}
