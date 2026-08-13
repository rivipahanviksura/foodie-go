package lk.foodie.foodiego;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lk.foodie.foodiego.R;

public class DashboardFragment extends Fragment {
    private FirebaseFirestore db;
    private TextView tvCustomerCount, tvTotalOrders, tvMonthlyRevenue, tvTotalFoodCount;

    private BarChart barChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        db = FirebaseFirestore.getInstance();

        tvCustomerCount = view.findViewById(R.id.tvTotalCustomers);
        tvTotalOrders = view.findViewById(R.id.tvTotalOrders);
        tvMonthlyRevenue = view.findViewById(R.id.tvMonthlyRevenue);
        tvTotalFoodCount = view.findViewById(R.id.tvTotalFoodCount);

        barChart = view.findViewById(R.id.barChart);
        setupChart();
        fetchCustomerCount();
        fetchOrdersCount();
        fetchMonthlyRevenue();
        fetchFoodCount();

        return view;
    }

    private void fetchCustomerCount() {
        db.collection("user").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot documents = task.getResult();
                int count = documents.size() - 1;
                tvCustomerCount.setText("" + count + "");
            } else {
                Log.e("DashboardFragment", "Error getting customer count", task.getException());
            }
        });
    }

    private void fetchOrdersCount() {
        db.collection("orders").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot documents = task.getResult();
                int count = documents.size();
                tvTotalOrders.setText("" + count + "");
            } else {
                Log.e("DashboardFragment", "Error getting order count", task.getException());
            }
        });
    }

    private void fetchFoodCount() {
        db.collection("foods").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot documents = task.getResult();
                int count = documents.size();
                tvTotalFoodCount.setText("" + count + "");
            } else {
                Log.e("DashboardFragment", "Error getting food count", task.getException());
            }
        });
    }

    private void fetchMonthlyRevenue() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startOfMonth = calendar.getTime();

        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startOfNextMonth = calendar.getTime();

        db.collection("orders")
                .whereGreaterThanOrEqualTo("orderTimestamp", new Timestamp(startOfMonth))
                .whereLessThan("orderTimestamp", new Timestamp(startOfNextMonth))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        double totalRevenue = 0;
                        for (DocumentSnapshot document : task.getResult()) {
                            Double totalAmount = document.getDouble("totalAmount");
                            if (totalAmount != null) {
                                totalRevenue += totalAmount;
                            }
                        }
                        tvMonthlyRevenue.setText("Rs. " + totalRevenue);
                    } else {
                        Log.e("DashboardFragment", "Error getting monthly revenue", task.getException());
                    }
                });
    }
    private void setupChart() {
        db.collection("orders").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<BarEntry> entries = new ArrayList<>();
                float[] monthlyRevenue = new float[12]; // Array to hold revenue for each month

                for (DocumentSnapshot document : task.getResult()) {
                    Double totalAmount = document.getDouble("totalAmount");
                    Timestamp timestamp = document.getTimestamp("orderTimestamp");

                    if (totalAmount != null && timestamp != null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(timestamp.toDate());
                        int month = calendar.get(Calendar.MONTH); // Get month (0 = January, 11 = December)

                        monthlyRevenue[month] += totalAmount; // Add revenue to the respective month
                    }
                }

                // Populate the entries for each month
                for (int i = 0; i < 12; i++) {
                    entries.add(new BarEntry(i, monthlyRevenue[i]));
                }

                BarDataSet dataSet = new BarDataSet(entries, "Monthly Revenue (Rs)");
                dataSet.setColor(getResources().getColor(R.color.purple_500));
                dataSet.setValueTextSize(12f);

                BarData barData = new BarData(dataSet);
                barChart.setData(barData);
                barChart.getDescription().setEnabled(false);

                String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                XAxis xAxis = barChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setLabelCount(months.length);

                barChart.invalidate(); // Refresh the chart
            } else {
                Log.e("DashboardFragment", "Error fetching order data", task.getException());
            }
        });
    }
}
