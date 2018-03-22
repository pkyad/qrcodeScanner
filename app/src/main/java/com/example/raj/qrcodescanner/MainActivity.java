package com.example.raj.qrcodescanner;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {

    private Button scan_btn;
    private Button submit_btn;
    private ListView lv;
    ArrayList<CardItem> arrayList;
    private CardArrayAdapter adapter;
    private RequestQueue queue;
    private String serverAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        scan_btn = (Button)findViewById(R.id.scan_btn);


        submit_btn = (Button)findViewById(R.id.submit);

        lv = (ListView) findViewById(R.id.ListView_lv);
        lv.setDivider(null);
        lv.setDividerHeight(0);

        queue = Volley.newRequestQueue(this);

        final Activity activity = this;

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("ERP server");

// Set up the input
        final EditText input = new EditText(getApplicationContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT );
        builder.setView(input);


// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                serverAddress = input.getText().toString();

            }
        });

        builder.show();



        arrayList = new ArrayList<CardItem>();


        adapter = new CardArrayAdapter(getApplicationContext(), R.layout.list_item_card);

//        for (int i = 0; i < 3; i++) {
//            CardItem card = new CardItem("Item 1 " , i, "https://www.duravit.co.uk/dimg/2458985_web_mil_zoom.jpg", 23, 0);
//            arrayList.add(card);
//            adapter.add(card);
//        }
        lv.setAdapter(adapter);

        scan_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {



                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
                adapter.notifyDataSetChanged();
            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                String url = "http://" + serverAddress +":8000/api/products/getQuote/";
                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                // response
                                Toast.makeText(getApplicationContext(),"Quote generated",Toast.LENGTH_LONG).show();
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error

                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams()
                    {
                        Map<String, String>  params = new HashMap<String, String>();

                        String productsArr = "";
                        String qtyArr = "";

                        for (int i = 0; i < arrayList.size(); i++) {
                            productsArr += arrayList.get(i).id + ",";
                            qtyArr += arrayList.get(i).qty + ",";
                        }


                        params.put("products", productsArr);
                        params.put("qty", qtyArr);

                        return params;
                    }
                };
                queue.add(postRequest);


            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode,data);
        if (result!=null){
            if (result.getContents()==null){
                Toast.makeText(this,"You cancelled the sanning",Toast.LENGTH_LONG).show();

            }
            else {

                final String scanContent = result.getContents();

                final String url = "http://"+ serverAddress + ":8000/api/products/product/?format=json&serial=" + scanContent;

// prepare the Request
                JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONArray>()
                        {
                            @Override
                            public void onResponse(final JSONArray response) {
                                // display response
                                Log.d("Response", response.toString());

                                final JSONObject prod;

                                try{
                                    prod = response.getJSONObject(0);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setTitle("Quantity");

// Set up the input
                                    final EditText input = new EditText(getApplicationContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                    input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                    builder.setView(input);


// Set up the buttons
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String m_Text = input.getText().toString();

                                            try{
                                                String name = prod.getString("name");
                                                CardItem card = new CardItem( name , parseInt(scanContent), prod.getString("dp"), prod.getInt("rate"), parseInt(m_Text));
                                                adapter.add(card);
                                                arrayList.add(card);
                                                adapter.notifyDataSetChanged();
                                            }catch (JSONException e){

                                            }
                                        }
                                    });
                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                                    builder.show();



                                }catch (JSONException e){
                                    Toast.makeText(getApplicationContext(),"Product not found in record",Toast.LENGTH_LONG).show();
                                }

                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }
                );

                queue.add(getRequest);
            }
        }
        else {

            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
