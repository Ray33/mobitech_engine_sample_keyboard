package io.mobitech.keyboarddemoapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

import io.mobitech.commonlibrary.model.Products;
import io.mobitech.commonlibrary.model.dto.ICallbackProducts;
import io.mobitech.demokeyboard.R;
import io.mobitech.shoppingengine.MobitechOffersManager;

public class MainActivity extends AppCompatActivity {

    private final static String PUBLISHER_KEY = "tentica";
    static int i=0;
    EditText mEditText;
    TextView mTextView;
    boolean robotIsOff = false;

    Handler handler = new Handler();
    int[] sleepArray = new int[3];
    String [] text = {"new", "new balance", "new balance shoes", "n", "air max", "nike 2015"};
    Runnable test = new Runnable() {
        @Override
        public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (robotIsOff) {
                            return;
                        }
                        if ((i + 1) % 6 == 0) {
                            mEditText.setText(text[i % 6] + " " + i % 1000, TextView.BufferType.NORMAL);
                            mTextView.setText("Searching... ", TextView.BufferType.NORMAL);
                        } else {
                            mEditText.setText(text[i % 6], TextView.BufferType.NORMAL);
                        }
                        i++;
                        handler.postDelayed(test, sleepArray[(i - 1) % 3]);
                    }
                });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWordGeneratorComponents();

        //*******************************************************
        //init mobitech Engine  - with callback
        //*******************************************************
        final MobitechOffersManager shoppingEngine = MobitechOffersManager.build(this,PUBLISHER_KEY , new ICallbackProducts() {
            @Override
            public void execute(final Products products, Context context) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setText("Found " + products.size() + " Offers ! words: " + products.get(0).getKeywords());
                    }
                });
            }
        });


        //*******************************************************
        //Call mobitech Engine with input
        //*******************************************************
        mEditText = (EditText) findViewById(R.id.editText) ;
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.v("TAG", "onTextChanged : " + s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.v("TAG", "afterTextChanged : " + s);
                shoppingEngine.putInput(s.toString(), Locale.ENGLISH, "com.android.chrome", "", "","in_app_main");
            }
        });
    }

    private void initWordGeneratorComponents() {
        sleepArray[0] = 1000;
        sleepArray[1] = 1000;
        sleepArray[2] = 6000;

        Button button = (Button) MainActivity.this.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag() != null && v.getTag().equals("manual")) {
                    v.setTag("robot");
                    ((Button) v).setText("Robot");
                    robotIsOff = true;
                } else {
                    v.setTag("manual");
                    ((Button) v).setText("Manual");
                    robotIsOff = false;
                    handler.postDelayed(test, 100);
                }
            }
        });

        mTextView = ((TextView) MainActivity.this.findViewById(R.id.textView));
    }

    @Override
    protected void onResume() {
        super.onResume();




    }
}
