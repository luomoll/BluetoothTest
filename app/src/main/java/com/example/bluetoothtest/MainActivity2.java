package com.example.bluetoothtest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.nio.ByteBuffer;

public class MainActivity2 extends AppCompatActivity {
    Button button;
    Button test1;
    ByteBuffer reBuffer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        byte[] buf={1,2,3,4,5,6,7,8,9,10};
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        reBuffer=ByteBuffer.allocate(100);
        button=findViewById(R.id.testId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            reBuffer.put(buf,0,10);
            }
        });
        test1=findViewById(R.id.button2);
        test1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reBuffer.flip();
                while (true)
                {
                    reBuffer.get();
                    if(!reBuffer.hasRemaining())
                    {
                        reBuffer.compact();
                        break;
                    }
                }

            }
        });
    }
}