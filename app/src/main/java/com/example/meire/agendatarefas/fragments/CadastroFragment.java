package com.example.meire.agendatarefas.fragments;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.meire.agendatarefas.AlarmReceiver;
import com.example.meire.agendatarefas.R;
import com.example.meire.agendatarefas.dao.tasksDAO;
import com.example.meire.agendatarefas.model.TaskModel;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CadastroFragment extends Fragment implements OnMapReadyCallback, DatePickerDialog.OnDateSetListener {
    private EditText etTask;
    private EditText etDescription;
    private TaskModel taskModelEdit;
    private EditText etPhoneNumber;
    private ImageView imgCall;
    private AlertDialog alerta;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private Button btAddLocation;
    private Button btAddReminder;
    private TextView tvAdress_Name;
    private TextView tvAdress_Ad;
    private TextView tvDate;
    private TextView tvTime;
    private GoogleMap mMap;
    private MapView mMapView;
    private LinearLayout LayAdress;
    private LinearLayout laydate;
    private LinearLayout laytime;
    private Spinner spAlarmRepeat;
    private View itemviewReminder;
    String TITLEADRESS = "";
    LatLng LATLNG = new LatLng(0, 0);
    GregorianCalendar calendarTask;
    DateFormat format_date_long;
    DateFormat format_date_short;
    SimpleDateFormat sdf;
    int REPETITION;
    final int MY_PERMISSIONS_REQUEST_CALL = 1;
    final int MY_PERMISSIONS_REQUEST_LOCATION = 2;
    final int PLACE_PICKER_REQUEST = 1;

    public CadastroFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View itemview = inflater.inflate(R.layout.fragment_cadastro, container, false);
        etTask = (EditText) itemview.findViewById(R.id.etTask);
        etDescription = (EditText) itemview.findViewById(R.id.etDescription);
        etPhoneNumber = (EditText) itemview.findViewById(R.id.etPhoneNumber);
        imgCall = (ImageView) itemview.findViewById(R.id.imgCall);
        btAddLocation = (Button) itemview.findViewById(R.id.btAddLocation);
        btAddReminder = (Button) itemview.findViewById(R.id.btAddReminder);
        tvAdress_Name = (TextView) itemview.findViewById(R.id.tvAdress_Name);
        tvAdress_Ad = (TextView) itemview.findViewById(R.id.tvAdress_Ad);
        LayAdress = (LinearLayout) itemview.findViewById(R.id.LayAdress);
        mMapView = (MapView) itemview.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        sdf = new SimpleDateFormat(getString(R.string.format_hour), Locale.getDefault());
        format_date_short = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        format_date_long = DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault());

        calendarTask = new GregorianCalendar();

        imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etPhoneNumber.getText().toString().equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(R.string.question_call_this_number);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            if (requestCallPhonePermission() == 0) {
                                    CallNumber();
                            }
                        }
                    });
                    builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });
                    alerta = builder.create();
                    alerta.show();
                } else {
                    Toast.makeText(getContext(), R.string.msg_no_phone, Toast.LENGTH_SHORT).show();
                }
            }
        });

        btAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (requestFineLocationPermission() == 0) {
                        OpenMap();
                }
            }
        });

        btAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemviewReminder = inflater.inflate(R.layout.dialog_reminder, container, false);
                tvDate = (TextView) itemviewReminder.findViewById(R.id.tvDate);
                tvTime = (TextView) itemviewReminder.findViewById(R.id.tvTime);
                laydate = (LinearLayout) itemviewReminder.findViewById(R.id.laydate);
                laytime = (LinearLayout) itemviewReminder.findViewById(R.id.laytime);
                spAlarmRepeat = (Spinner) itemviewReminder.findViewById(R.id.spAlarmRepeat);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.alarmRepeat_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spAlarmRepeat.setAdapter(adapter);
                spAlarmRepeat.setSelection(REPETITION);
                tvDate.setText(format_date_long.format(calendarTask.getTime()));
                tvTime.setText(sdf.format(calendarTask.getTime()));

                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(false);

                builder.setTitle(btAddReminder.getText().equals(getString(R.string.add_reminder)) ? btAddReminder.getText() : getString(R.string.edit_reminder));
                builder.setView(itemviewReminder);

                laydate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                calendarTask.set(year, monthOfYear, dayOfMonth);
                                tvDate.setText(format_date_long.format(calendarTask.getTime()));
                            }

                        }, calendarTask.get(Calendar.YEAR), calendarTask.get(Calendar.MONTH), calendarTask.get(Calendar.DAY_OF_MONTH));
                        datePickerDialog.setCancelable(false);
                        datePickerDialog.show();
                    }
                });

                laytime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                calendarTask.set(Calendar.HOUR_OF_DAY, selectedHour);
                                calendarTask.set(Calendar.MINUTE, selectedMinute);
                                tvTime.setText(sdf.format(calendarTask.getTime()));
                            }
                        }, calendarTask.get(Calendar.HOUR_OF_DAY), calendarTask.get(Calendar.MINUTE), true);
                        timePickerDialog.setCancelable(false);
                        timePickerDialog.show();
                    }
                });
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
                builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

                if (!btAddReminder.getText().equals(getString(R.string.add_reminder))) {
                    builder.setNeutralButton(R.string.remove, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            calendarTask = new GregorianCalendar();
                            btAddReminder.setText(getString(R.string.add_reminder));
                        }
                    });
                }
                alerta = builder.create();
                alerta.show();

                Button theButton = alerta.getButton(DialogInterface.BUTTON_POSITIVE);
                theButton.setOnClickListener(new DialogButtonClickWrapper(alerta) {

                    @Override
                    protected boolean onClicked() {
                        GregorianCalendar calendarCurret = new GregorianCalendar();
                        if (calendarTask.getTime().before(calendarCurret.getTime())){
                            Toast.makeText(getContext(), R.string.date_passed, Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        else {
                            btAddReminder.setText(format_date_short.format(calendarTask.getTime()) + " " + sdf.format(calendarTask.getTime()));
                            REPETITION = spAlarmRepeat.getSelectedItemPosition();
                            return true;
                        }
                    }
                });
            }
        });

        if (!(this.getArguments() == null)) {
            taskModelEdit = this.getArguments().getParcelable("task");

            if (!(taskModelEdit == null)) {
                etTask.setText(taskModelEdit.getTitle());
                etDescription.setText(taskModelEdit.getDescription());
                etPhoneNumber.setText(taskModelEdit.getPhone());
                tvAdress_Name.setText(taskModelEdit.getAdress_name());
                tvAdress_Ad.setText(taskModelEdit.getAdress());
                REPETITION = taskModelEdit.getRepetition();

                if (taskModelEdit.getLatitude() != 0) {
                    LATLNG = new LatLng(taskModelEdit.getLatitude(), taskModelEdit.getLongitude());
                    TITLEADRESS = taskModelEdit.getAdress_name();

                    try {
                        mMapView.onResume();
                        MapsInitializer.initialize(getActivity().getApplicationContext());
                        mMapView.getMapAsync(this);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), R.string.error_access_maps, Toast.LENGTH_SHORT).show();
                    }
                }

                if (!taskModelEdit.getDatetime_reminder().equals("")) {
                    final String dateStr = taskModelEdit.getDatetime_reminder();
                    final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);

                    try {
                        final Date datetask = df.parse(dateStr);
                        calendarTask.setTime(datetask);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    btAddReminder.setText(format_date_short.format(calendarTask.getTime()) + " " + sdf.format(calendarTask.getTime()));
                }
            }
        }

        return itemview;
    }

    private int requestCallPhonePermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String permission = Manifest.permission.CALL_PHONE;
            int grant = ContextCompat.checkSelfPermission(getContext(), permission);
            if (grant != PackageManager.PERMISSION_GRANTED) {
                String[] permission_list = new String[1];
                permission_list[0] = permission;
                requestPermissions(permission_list, MY_PERMISSIONS_REQUEST_CALL);
            }
            return grant;
        }
        else {
            return 0;
        }
    }
    private int requestFineLocationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String permission = Manifest.permission.ACCESS_FINE_LOCATION;
            int grant = ContextCompat.checkSelfPermission(getContext(), permission);
            if (grant != PackageManager.PERMISSION_GRANTED) {
                String[] permission_list = new String[1];
                permission_list[0] = permission;
                requestPermissions(permission_list, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return grant;
        }
        else {
            return 0;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CallNumber();
                } else {
                    Toast.makeText(getContext(), R.string.error_permission_call, Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    OpenMap();
                } else {
                    Toast.makeText(getContext(), R.string.error_permission_location, Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            if (etTask.getText().toString().equals("")) {
                Toast.makeText(getContext(), R.string.message_no_title, Toast.LENGTH_SHORT).show();
                return false;
            } else {
                SaveTasks();
            }
            getActivity().onBackPressed();
        }

        return true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause() {
        ((InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(etTask.getWindowToken(), 0) ;
        super.onPause();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == -1) {
                final Place place = PlacePicker.getPlace(getContext(), data);

                final CharSequence name = place.getName();
                final CharSequence address = place.getAddress();
                LATLNG = place.getLatLng();

                tvAdress_Name.setText(name);
                tvAdress_Ad.setText(address);
                TITLEADRESS = name.toString();

                try {
                    mMapView.onResume();
                    MapsInitializer.initialize(getActivity().getApplicationContext());
                    mMapView.getMapAsync(this);
                } catch (Exception e) {
                    Toast.makeText(getContext(), R.string.error_access_maps, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(LATLNG).title(TITLEADRESS));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LATLNG, 17));
        LayAdress.setVisibility(getView().VISIBLE);
        btAddLocation.setText(R.string.edit_location);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

    }

    private void CallNumber(){
        String phone = etPhoneNumber.getText().toString();

        Uri uri = Uri.parse("tel:" + phone);
        Intent intent = new Intent(Intent.ACTION_CALL, uri);

        startActivity(intent);
    }

    private void OpenMap(){
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(getActivity());
            startActivityForResult(intent, PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException e) {
            Toast.makeText(getContext(), R.string.error_access_maps, Toast.LENGTH_LONG).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(getContext(), R.string.error_access_maps, Toast.LENGTH_LONG).show();
        }
    }

    private void SaveTasks() {
        tasksDAO tasksDAO = new tasksDAO(getContext());
        TaskModel taskModel = new TaskModel();
        taskModel.setTitle(etTask.getText().toString());
        taskModel.setPhone(etPhoneNumber.getText().toString());
        taskModel.setAdress(tvAdress_Ad.getText().toString());
        taskModel.setAdress_name(tvAdress_Name.getText().toString());
        taskModel.setDescription(etDescription.getText().toString());
        taskModel.setLongitude(LATLNG.longitude);
        taskModel.setLatitude(LATLNG.latitude);
        taskModel.setRepetition(REPETITION);
        taskModel.setDone(taskModelEdit == null ? "N" : taskModelEdit.getDone());

        if (!btAddReminder.getText().equals(getString(R.string.add_reminder))) {
            SimpleDateFormat sdfEdit = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
            taskModel.setDatetime_reminder(sdfEdit.format(calendarTask.getTime()));
        } else {
            taskModel.setDatetime_reminder("");
        }

        if (taskModelEdit == null) {
            long addTask = tasksDAO.add(taskModel);
            if ( addTask > 0) {
                Toast.makeText(getContext(), R.string.salved, Toast.LENGTH_SHORT).show();
                taskModel.setId((int) addTask);
            } else {
                Toast.makeText(getContext(), R.string.error_saving_task, Toast.LENGTH_SHORT).show();
            }
        } else {
            taskModel.setId(taskModelEdit.getId());
            if (tasksDAO.update(taskModel)) {
                Toast.makeText(getContext(), R.string.salved, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.error_saving_task, Toast.LENGTH_SHORT).show();
            }
        }

        if (!taskModel.getDatetime_reminder().equals("")) {
            GregorianCalendar calendarCurret = new GregorianCalendar();
            if (calendarTask.getTime().after(calendarCurret.getTime())) {
                Intent intent = new Intent(getContext(), AlarmReceiver.class);
                intent.putExtra("IDTASK", taskModel.getId());

                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        getActivity().getApplicationContext(), 0, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(getContext().ALARM_SERVICE);

                long repetitiontime = 0;
                switch (REPETITION) {
                    case 1: {
                        repetitiontime = AlarmManager.INTERVAL_DAY;
                        break;
                    }
                    case 2: {
                        repetitiontime = AlarmManager.INTERVAL_DAY * 7;
                        break;
                    }
                    case 3: {
                        repetitiontime = AlarmManager.INTERVAL_DAY * 30;
                        break;
                    }
                    case 4: {
                        repetitiontime = AlarmManager.INTERVAL_DAY * 365;
                        break;
                    }
                }
                if (REPETITION > 0) {
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendarTask.getTimeInMillis(), repetitiontime, pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendarTask.getTimeInMillis(), pendingIntent);
                }
            }
        }
    }
}

abstract class DialogButtonClickWrapper implements View.OnClickListener {

    private AlertDialog dialog;

    public DialogButtonClickWrapper(AlertDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void onClick(View v) {

        if(onClicked()){
            dialog.dismiss();
        }
    }

    protected abstract boolean onClicked();
}
