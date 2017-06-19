package com.example.marcin.lista;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int HELP = 100;
    private static final int WRITE = 200;

    private ListView listView;
    private List<Row> rows;
    private CustomAdapter adapter;

    private BaseHelper baseHelper;

    private InputFragment input;
    private DeleteFragment delete;
    private DeleteAllFragment deleteAll;
    private ModifyFragment modify;

    private SensorManager manager;
    private Sensor proximity;
    private Sensor accelerometer;

    private Button addButton;

    private boolean disable;
    private float prevValue;
    private long prevTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addButton = (Button) findViewById(R.id.add);

        input = new InputFragment();
        delete = new DeleteFragment();
        deleteAll = new DeleteAllFragment();
        modify = new ModifyFragment();

        baseHelper = new BaseHelper(this);
        baseHelper.open();
        rows = baseHelper.getData();

        adapter = new CustomAdapter(rows, getApplicationContext(), this);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putInt("i", i);
                delete.setArguments(bundle);
                delete.show(getSupportFragmentManager(), "Delete");
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putInt("i", i);
                modify.setArguments(bundle);
                modify.show(getSupportFragmentManager(), "Modify");
            }
        });

        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        proximity = manager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        disable = false;
        prevValue = 5;
        prevTime = 0;
    }

    public void onResume() {
        super.onResume();
        baseHelper.open();
        manager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
        manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        prevTime = System.currentTimeMillis();
    }

    public void onPause() {
        super.onPause();
        baseHelper.close();
        manager.unregisterListener(this);
    }

    public void showInput(View v) {
        input.show(getSupportFragmentManager(), "Input");
    }

    public void saveToFile() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            String data = "";
            for (Row r : rows) {
                data += r.toString() + '\n';
            }
            Intent intent = new Intent(MainActivity.this, SaveToFileService.class);
            intent.putExtra("data", data);
            startService(intent);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE);
        }

    }

    public void showHelp() {
        Intent intent = new Intent(MainActivity.this, Help.class);
        startActivityForResult(intent, HELP);
    }

    public void add(String name, String amount) {
        boolean isInt = true;
        int value = 0;
        try {
            value = Integer.parseInt(amount);
        } catch (NumberFormatException e) {
            isInt = false;
        }
        if (isInt) {
            Row r = new Row(name, value);
            if (baseHelper.add(r)) {
                Toast.makeText(MainActivity.this, "Dodawanie powiodło się.", Toast.LENGTH_LONG).show();
                adapter.add(r);
            } else {
                Toast.makeText(MainActivity.this, "Wystąpił błąd.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "Podaj ilość jako liczbę całkowitą.", Toast.LENGTH_LONG).show();
        }
    }

    public void deleteAll() {
        adapter.clear();
        baseHelper.deleteAll();
    }

    public void delete(int i) {
        Row r = rows.get(i);
        adapter.remove(r);
        baseHelper.delete(String.valueOf(r.getId()));
    }

    public void updateAmount(Row r) {
        baseHelper.updateAmount(r.getId(), r.getAmount());
    }

    public void updateName(int i, String name) {
        Row r = rows.get(i);
        r.setName(name);
        adapter.notifyDataSetChanged();
        baseHelper.updateName(r.getId(), name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (disable) return true;
        switch (item.getItemId()) {
            case R.id.save:
                saveToFile();
                return true;
            case R.id.help:
                showHelp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (disable) return;
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == HELP && resultCode == Activity.RESULT_OK) {
            example(data.getStringExtra("result"));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == WRITE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveToFile();
            } else {
                Toast.makeText(MainActivity.this, "Nie udzielono pozwolenia.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void example(String s) {
        baseHelper.open();
        baseHelper.deleteAll();
        adapter.clear();
        String tab[] = s.replaceAll("\\s", "").replaceAll("\\{", "").replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"", "").split("\\}");
        ArrayList<String[]> arrayList = new ArrayList<>();
        for (String x : tab) {
            arrayList.add(x.split(","));
        }
        String[] tmp = new String[2];
        int i = 0;
        for (String[] a : arrayList) {
            for (String b : a) {
                if (!b.equals("")) {
                    tmp[i] = b.split(":")[1];
                    ++i;
                    if (i == 2) {
                        adapter.add(new Row(tmp[0], Integer.parseInt(tmp[1])));
                        baseHelper.add(rows.get(rows.size() - 1));
                        i = 0;
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_PROXIMITY: {
                WindowManager.LayoutParams params = getWindow().getAttributes();
                if (sensorEvent.values[0] == 0) {
                    params.screenBrightness = 0.01f;
                    getWindow().setAttributes(params);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    addButton.setEnabled(false);
                    listView.setVisibility(View.GONE);
                    disable = true;
                    Fragment fragment;
                    String[] tags = {"Input", "Delete", "Modify", "DeleteAll"};
                    for (int i = 0; i < 4; ++i) {
                        if ((fragment = getSupportFragmentManager().findFragmentByTag(tags[i])) != null) {
                            ((DialogFragment) fragment).dismiss();
                        }
                    }
                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    listView.setVisibility(View.VISIBLE);
                    addButton.setEnabled(true);
                    disable = false;
                    params.screenBrightness = -1;
                    getWindow().setAttributes(params);
                }
                break;
            }
            case Sensor.TYPE_ACCELEROMETER: {
                if (disable) break;
                long currTime = System.currentTimeMillis();
                if (currTime - prevTime > 200) {
                    prevTime = currTime;
                    float value = sensorEvent.values[2];
                    if ((Math.abs(value - prevValue) > 5)) {
                        if (getSupportFragmentManager().findFragmentByTag("Input") == null &&
                                getSupportFragmentManager().findFragmentByTag("Delete") == null &&
                                getSupportFragmentManager().findFragmentByTag("Modify") == null &&
                                getSupportFragmentManager().findFragmentByTag("DeleteAll") == null) {
                            deleteAll.show(getSupportFragmentManager(), "DeleteAll");
                        }
                    }
                    prevValue = value;
                }
                break;
            }
        }
    }
}
