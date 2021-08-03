package in.co.nexs.nexsapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import in.co.nexs.nexsapp.R;

public class NameActivity extends AppCompatActivity {

    public static final String FIRST_NAME = "first_name";
    public static final String MIDDLE_NAME = "middle_name";
    public static final String LAST_NAME = "last_name";
    private Context context;
    private EditText first, middle, last;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        setReferences();
        setListeners();
    }

    private void setReferences() {
        context = this;
        first = findViewById(R.id.first);
        middle = findViewById(R.id.middle);
        last = findViewById(R.id.last);
        next = findViewById(R.id.next);
    }

    private void setListeners() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (first.getText() == null || last.getText() == null) {
                    showToast("First and Last name required");
                    return;
                }
                if (first.getText().toString().trim().equals("") || last.getText().toString().trim().equals("")) {
                    showToast("First and Last name required");
                    return;
                }
                String firstName = first.getText().toString().trim();
                String lastName = last.getText().toString().trim();
                String middleName = "";
                if (middle.getText() != null) {
                    middleName = middle.getText().toString().trim();
                }
                startPhoneVerification(firstName, middleName, lastName);
            }
        });
    }

    private void startPhoneVerification(String firstName, String middleName, String lastName) {
        Intent intent = new Intent(context, PhoneAuth.class);
        intent.putExtra(FIRST_NAME, firstName);
        intent.putExtra(MIDDLE_NAME, middleName);
        intent.putExtra(LAST_NAME, lastName);
        startActivityForResult(intent, 1);
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                boolean authSuccess = data.getBooleanExtra("success", false);
                Intent intent = new Intent();
                intent.putExtra("success", authSuccess);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }
}