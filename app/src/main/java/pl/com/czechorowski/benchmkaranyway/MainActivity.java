package pl.com.czechorowski.benchmkaranyway;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
public class MainActivity extends AppCompatActivity {

   private Button exit,startTest,showResults;
    String token="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        //Get token from Starter activity
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        //Log.d("TAG",token);
        exit = (Button) findViewById(R.id.button4);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(1);
            }
        });
        startTest = (Button) findViewById(R.id.button);
        startTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Launcher.class);
                intent.putExtra("token", token);
                startActivity(intent);
            }
        });
        showResults = (Button) findViewById(R.id.button3);
        showResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ShowResults.class);
                intent.putExtra("token", token);
                startActivity(intent);
            }
        });
    }
}


