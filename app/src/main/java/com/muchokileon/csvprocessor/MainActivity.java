package com.muchokileon.csvprocessor;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.IOException;
import java.io.FileReader;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int PICKFILE_RESULT_CODE = 1;
    private Uri fileUri;
    private String filePath;
    private String selectedColumnSeparator;

    private Button btnChooseFile;
    private RadioGroup radioGroupSeparators;
    private RadioButton radioSeparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addListenerOnButton();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "CSV PROCESSOR", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void addListenerOnButton() {
        radioGroupSeparators = (RadioGroup) findViewById(R.id.radioGroupSeparators);
        btnChooseFile = findViewById(R.id.btnChooseFile);

        btnChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radioGroupSeparators.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getApplicationContext(),
                            "Please select column separator", Toast.LENGTH_SHORT).show();
                } else {
                    // get selected radio button from radioGroup
                    int selectedId = radioGroupSeparators.getCheckedRadioButtonId();

                    // find the radiobutton by returned id
                    radioSeparator = (RadioButton) findViewById(selectedId);

                    selectedColumnSeparator = radioSeparator.getText().toString();

                    openFile("text/*");
                }

            }
        });
    }

    public void openFile(String mimeType) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(mimeType);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // special intent for Samsung file manager
        Intent sIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
        sIntent.putExtra("CONTENT_TYPE", mimeType);
        sIntent.addCategory(Intent.CATEGORY_DEFAULT);

        Intent chooserIntent;
        if (getPackageManager().resolveActivity(sIntent, 0) != null){
            // it is device with Samsung file manager
            chooserIntent = Intent.createChooser(sIntent, "Open file");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { intent});
        } else {
            chooserIntent = Intent.createChooser(intent, "Open file");
        }

        try {
            startActivityForResult(chooserIntent, PICKFILE_RESULT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), "No suitable File Manager was found.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == -1) {
                    fileUri = data.getData();
                    filePath = fileUri.getPath();
                    readCsvFile(filePath);
                }

                break;
        }
    }

    public void readCsvFile(String FilePath) {
        try {
            CSVParser parser = new CSVParserBuilder().withSeparator(selectedColumnSeparator.charAt(0)).build();
            CSVReader csvReader = new CSVReaderBuilder(new FileReader(FilePath))
                    .withCSVParser(parser)
                    //.withSkipLines(1)
                    .build();

            List<String[]> allData = csvReader.readAll();
            retrieveDataAndDisplay(allData);

        } catch (IOException e) {

        }
    }

    private void retrieveDataAndDisplay(List<String[]> allData) {
        TableLayout table = (TableLayout)findViewById(R.id.table_layout);
        for (String[] array : allData){
            for (String s : array){
                //System.out.println(s.split(","));
                String[] _sArray = s.split(",");
                TableRow row = new TableRow(this);
                for (String s2 : _sArray){

                    TextView tv = new TextView(this);
                    tv.setText(s2);
                    //tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    row.addView(tv);

                }
                table.addView(row);

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
