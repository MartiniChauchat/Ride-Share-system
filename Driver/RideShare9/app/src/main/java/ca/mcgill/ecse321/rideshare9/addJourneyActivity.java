package ca.mcgill.ecse321.rideshare9;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class addJourneyActivity extends AppCompatActivity {
    private String error = null; //tutorial
     Spinner seatingSpinner, vehicleSpinner ;
     ListView stopListView;
     StopList_adapter stopAdapter;
     TextView startLocationtx, endLocationtx, costFullTriptx, emptyText_stopLv;

    private List<String> vehicleList = new ArrayList<>();
    private ArrayAdapter<String> vehicleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journey);

        //control the vehicle spinner
        vehicleSpinner =  findViewById(R.id.spinnerVehicle);
        vehicleAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, vehicleList);
        vehicleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleSpinner.setAdapter(vehicleAdapter);

        // Get initial content for spinners
        refreshVehicleList(this.getCurrentFocus(), vehicleAdapter ,vehicleList, "participants");

        // Available seating spinner
        Integer[] items1 = new Integer[]{1,2,3,4};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, items1);
        seatingSpinner.setAdapter(adapter);

        //control the seating spinner
        seatingSpinner = findViewById(R.id.spinnerSeating);
        Integer[] items = new Integer[]{0,1,2,3,4};
        ArrayAdapter<Integer> adapter2 = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, items);
        seatingSpinner.setAdapter(adapter);

        // control the stop list
        stopListView = (ListView) findViewById(R.id.stop_list);
        stopListView.setEmptyView(emptyText_stopLv);
        stopAdapter = new StopList_adapter();
        stopListView.setAdapter(stopAdapter);
        stopAdapter.notifyDataSetChanged();

        // tutorial
        refreshErrorMessage();
    }

    public void addJourney(View v) {

        startLocationtx = (TextView) findViewById(R.id.startLocationText);
        endLocationtx =  (TextView) findViewById(R.id.endLocationText);
        costFullTriptx = (TextView) findViewById(R.id.finalcostText);

        error = "";
        //final TextView tv = (TextView) findViewById(R.id.newparticipant_name);
        HttpUtils.post("/adv/create-adv", new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onFinish() {
                refreshErrorMessage();
                startLocationtx.setText("");
                endLocationtx.setText("");
                costFullTriptx.setText("");

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    error += errorResponse.get("message").toString();
                } catch (JSONException e) {
                    error += e.getMessage();
                }
                refreshErrorMessage();
            }
        });
    }

    public void addEvent(View v) {
        // Title of trip
        TextView tv = (TextView) findViewById(R.id.trip_title_text);
        String tripTitle = tv.getText().toString();

        // start location
        tv = (TextView) findViewById(R.id.startLocationText);
        String startLocation = tv.getText().toString();

        // end location
        tv = (TextView) findViewById(R.id.endLocationText);
        String endLocation = tv.getText().toString();

        // start time
        tv = (TextView) findViewById(R.id.starttime);
        String text = tv.getText().toString();
        String comps[] = text.split(":");

        int startHours = Integer.parseInt(comps[0]);
        int startMinutes = Integer.parseInt(comps[1]);

        // date
        tv = (TextView) findViewById(R.id.startDate);
        text = tv.getText().toString();
        comps = text.split("-");

        int year = Integer.parseInt(comps[2]);
        int month = Integer.parseInt(comps[1]);
        int day = Integer.parseInt(comps[0]);

        // cost of trip
        tv = (TextView) findViewById(R.id.finalcostText);
        String fullTripCost = tv.getText().toString() ; // change type depending on request parameter

        // Vehicle
        long id_vehicle = vehicleSpinner.getSelectedItemId();

        // available seating
        int  availableSeats = 0;
        try {
            availableSeats = Integer.parseInt(seatingSpinner.getSelectedItem().toString());
        } catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }

        // Reminder: calling the service looks like this:
        // http://192.168.56.50:8088/createEvent?eventName=tst&date=2013-10-23&startTime=00:00&endTime=23:59

        RequestParams rp = new RequestParams();

        NumberFormat formatter = new DecimalFormat("00");
        rp.add("date", year + "-" + formatter.format(month) + "-" + formatter.format(day));
        rp.add("startTime", formatter.format(startHours) + ":" + formatter.format(startMinutes));

        HttpUtils.post("/adv/create-adv", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                refreshErrorMessage();
                ((TextView) findViewById(R.id.trip_title_text)).setText("");
                ((TextView) findViewById(R.id.startLocationText)).setText("");
                ((TextView) findViewById(R.id.endLocationText)).setText("");
                ((TextView) findViewById(R.id.finalcostText)).setText("");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    error += errorResponse.get("message").toString();
                } catch (JSONException e) {
                    error += e.getMessage();
                }
                refreshErrorMessage();
            }
        });
    }

    private void refreshErrorMessage() {
        // set the error message
        TextView tvError = (TextView) findViewById(R.id.error_addJourney);
        tvError.setText(error);

        if (error == null || error.length() == 0) {
            tvError.setVisibility(View.GONE);
        } else {
            tvError.setVisibility(View.VISIBLE);
        }

    }

    private Bundle getTimeFromLabel(String text) {
        Bundle rtn = new Bundle();
        String comps[] = text.toString().split(":");
        int hour = 12;
        int minute = 0;

        if (comps.length == 2) {
            hour = Integer.parseInt(comps[0]);
            minute = Integer.parseInt(comps[1]);
        }

        rtn.putInt("hour", hour);
        rtn.putInt("minute", minute);

        return rtn;
    }

    private Bundle getDateFromLabel(String text) {
        Bundle rtn = new Bundle();
        String comps[] = text.toString().split("-");
        int day = 1;
        int month = 1;
        int year = 1;

        if (comps.length == 3) {
            day = Integer.parseInt(comps[0]);
            month = Integer.parseInt(comps[1]);
            year = Integer.parseInt(comps[2]);
        }

        rtn.putInt("day", day);
        rtn.putInt("month", month-1);
        rtn.putInt("year", year);

        return rtn;
    }

    public void showTimePickerDialog(View v) {
        TextView tf = (TextView) v;
        Bundle args = getTimeFromLabel(tf.getText().toString());
        args.putInt("id", v.getId());

        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        TextView tf = (TextView) v;
        Bundle args = getDateFromLabel(tf.getText().toString());
        args.putInt("id", v.getId());

        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void setTime(int id, int h, int m) {
        TextView tv = (TextView) findViewById(id);
        tv.setText(String.format("%02d:%02d", h, m));
    }

    public void setDate(int id, int d, int m, int y) {
        TextView tv = (TextView) findViewById(id);
        tv.setText(String.format("%02d-%02d-%04d", d, m + 1, y));
    }

    private void refreshVehicleList(View v, final ArrayAdapter<String>  adapter, final List<String> names, String restFunctionName) {
        HttpUtils.get(restFunctionName, new RequestParams(), new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                names.clear();
                names.add("Please select...");
                for( int i = 0; i < response.length(); i++){
                    try {
                        names.add(response.getJSONObject(i).getString("name"));
                    } catch (Exception e) {
                        error += e.getMessage();
                    }
                    refreshErrorMessage();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    error += errorResponse.get("message").toString();
                } catch (JSONException e) {
                    error += e.getMessage();
                }
                refreshErrorMessage();
            }
        });
    }
}
