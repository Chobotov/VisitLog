package com.android.visitlog.DialogFileMenu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.visitlog.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class DirPickerActivity extends AppCompatActivity
  implements DirListAdapter.OnItemClickListener {

  public static final class Mode {
    public static final int DIRECTORY = 1;
    public static final int FILE = 2;
    static final int UNDEFINED = 0;
  }

  public static final String KEY_MODE = "DirPickerActivity.mode";
  private static final String KEY_LAST_PATH = "dirPicker.lastPath";
  private static final String SEP = File.separator;

  private Context appContext;
  private TextView tvPath, tvEmpty;
  private Button btnGo;
  private RecyclerView listDir;
  private DirListAdapter adapter;
  private ArrayList<DirEntry> arrayDir;
  private String path;
  private int mode;


  private Button back;



  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.act_dir_picker);

    if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

      // Permission is not granted
      // Should we show an explanation?
      if (ActivityCompat.shouldShowRequestPermissionRationale(this,
              Manifest.permission.READ_EXTERNAL_STORAGE) &&
              ActivityCompat.shouldShowRequestPermissionRationale(this,
                      Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        // Show an explanation to the user *asynchronously* -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.
      } else {
        // No explanation needed; request the permission
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                2);

        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
        // app-defined int constant. The callback method gets the
        // result of the request.
      }
    }

    appContext = getApplicationContext();
    arrayDir = new ArrayList<>();

    //loading saved instance
    State savedState = (State) getLastCustomNonConfigurationInstance();
    if (savedState != null) path = savedState.path;

    //View components init
    tvPath = findViewById(R.id.dirPicker_tvPath);
    tvEmpty = findViewById(R.id.dirPicker_tvEmpty);
    btnGo = findViewById(R.id.dirPicker_go);
    back = findViewById(R.id.back);
    btnGo.setOnClickListener(v -> onClickGo(null));
    adapter = new DirListAdapter(this, this, arrayDir);
    listDir = findViewById(R.id.dirPicker_listDir);
    listDir.setLayoutManager(new LinearLayoutManager(this));
    listDir.setHasFixedSize(true);
    listDir.setAdapter(adapter);

    mode = getIntent().getIntExtra(KEY_MODE, Mode.UNDEFINED);
    if (mode == Mode.FILE) {
      setTitle(R.string.actLabel_filePicker);
      btnGo.setVisibility(View.GONE);
      btnGo.setOnClickListener(null);
    }

    SharedPreferences settings = PreferenceManager
      .getDefaultSharedPreferences(appContext);
    if (path == null) path = settings.getString(KEY_LAST_PATH, SEP);
    // Checking access to file system root directory
    if (path.equals(SEP) && !isDirOpened(path)) {
      Toast.makeText(appContext,
        R.string.dirPicker_fsRootUnreadable, Toast.LENGTH_LONG).show();
      path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
      //save new start path in SharedPreferences
      settings.edit().putString(KEY_LAST_PATH, path).apply();



    }

    updateListDir();


    back.setOnClickListener(x->{
      finish();
    });
  }

  @Override
  public Object onRetainCustomNonConfigurationInstance() {
    return new State(path);
  }

  @Override
  public void onItemClick(DirEntry item) {
    if (item.isLevelUp()) {
      onClickBack();
    } else if (item.isDir()) {
      path = path.concat(item.getName()).concat(SEP);
      updateListDir();
    } else if (mode == Mode.FILE) {
      onClickGo(item.getName());
    }
  }

  private void onClickBack() {
    if (path.equals(SEP)) {
      setResult(RESULT_CANCELED, new Intent());
      finish();
    } else {
      path = new File(path).getParent();
      if (!path.equals(SEP))
        path = path.concat(SEP);
      updateListDir();
    }
  }

  private void onClickGo(String fileName) {
    setDefaultPath(appContext, path);
    Intent intent = new Intent();
    if (fileName != null) path = path.concat(fileName);
    intent.setData(Uri.parse("file://".concat(path)));
    setResult(RESULT_OK, intent);
    finish();
  }

  private void updateListDir() {
    arrayDir.clear();

    if (!path.equals(SEP)) arrayDir.add(new DirEntry());
    ArrayList<DirEntry> arrayFiles = new ArrayList<>();
    File[] files = new File(path).listFiles();
    boolean dirOpened = files != null;
    btnGo.setEnabled(dirOpened);

    if (dirOpened) {
      if (files.length > 0) {
        tvEmpty.setVisibility(TextView.INVISIBLE);
        Arrays.sort(files);

        for (File file : files) {
          boolean isDir = file.isDirectory();
          DirEntry entry = new DirEntry(file.getName(), isDir);
          if (isDir) arrayDir.add(entry);
          else arrayFiles.add(entry);
        }

        arrayDir.addAll(arrayFiles);
      } else
        tvEmpty.setVisibility(TextView.VISIBLE);
    } else {
      Toast.makeText(appContext,
        R.string.dirPicker_dirUnreadable, Toast.LENGTH_LONG).show();
    }

    adapter.notifyDataSetChanged();
    listDir.scrollToPosition(0);
    tvPath.setText(path);
  }

  private boolean isDirOpened(String dirName) {
    try {
      File[] files = new File(dirName).listFiles();
      for (File file : files) {}
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public static void setDefaultPath(Context context, String path) {
    PreferenceManager.getDefaultSharedPreferences(context)
      .edit().putString(KEY_LAST_PATH, path).apply();
  }

  private static class State {
    private String path;

    State(String path) {
      this.path = path;
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String permissions[], int[] grantResults) {
    switch (requestCode) {
      case 1: {

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
        ) {

          // permission was granted, yay! Do the
          // contacts-related task you need to do.
        } else {
          Toast.makeText(this, "Не удалось получить разрешение на работу с файловой системой. Проверте разрешение приложения.", Toast.LENGTH_LONG
          ).show();
          finish();
        }
        return;
      }

      // other 'case' lines to check for other
      // permissions this app might request
    }
  }




}


