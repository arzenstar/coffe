package com.example.putra.coffe;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class PesananActivity extends AppCompatActivity {
    String response;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesanan);

        Intent intent = getIntent();
        response = intent.getStringExtra("json");
        String url = "http://192.168.137.77/coffe/php/api-pesanan2.php";

        ListView list =(ListView) findViewById(R.id.list2);

        DPT get = new DPT();
        get.execute(url,response);

    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pesanan, menu);
        return true;
    }

    //setting
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.close) {
            this.finish();

            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    //data item di convert menjadi list view
    public class myAdapter2 extends ArrayAdapter<String>
    {
        Context context;
        String []id;
        String []name;
        String []hrg;
        String []file;
        String []status;
        myAdapter2(Context c, String []nama, String []harga, String[] inmage, String []id,String []status )
        {
            super(c,R.layout.activity_pesan,R.id.nama,nama);
            this.context = c;
            this.id=id;
            this.name=nama;
            this.hrg=harga;
            this.file=inmage;
            this.status=status;

        }

        public View getView(final int potition, View convertview, ViewGroup parent)
        {
            if (convertview == null) {
                convertview = LayoutInflater.from(getContext()).inflate(R.layout.activity_pesan, parent, false);
            }
            ImageView img =(ImageView) convertview.findViewById(R.id.image);
            TextView nama = (TextView) convertview.findViewById(R.id.name);
            TextView harga = (TextView) convertview.findViewById(R.id.price);
            TextView labelnama = (TextView) convertview.findViewById(R.id.Labelname);
            TextView labelharga = (TextView) convertview.findViewById(R.id.Labelprice);
            TextView status = (TextView) convertview.findViewById(R.id.status);



            img.setVisibility(View.VISIBLE);
            nama.setVisibility(View.VISIBLE);
            harga.setVisibility(View.VISIBLE);
            labelnama.setVisibility(View.VISIBLE);
            labelharga.setVisibility(View.VISIBLE);
            status.setVisibility(View.VISIBLE);
            //btn.setVisibility(View.VISIBLE);

            Log.d("file", file[potition]);
            Picasso.with(PesananActivity.this).load(file[potition]).into(img);
            nama.setText(name[potition]);
            harga.setText(hrg[potition]);
            status.setText(id[potition]);





            return convertview;
        }
    }

    //mendapatkan data item
    public class DPT extends AsyncTask<String, String, String>
    {
        String sem;
        ListView list;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list =(ListView) findViewById(R.id.list1);
            /*Intent intent = getIntent();
            sem = intent.getStringExtra("json");*/
        }

        public String getSem()
        {
            return  this.sem;
        }
        @Override
        protected String doInBackground(String... params) {
            sem=params[1];
            URL obj = null;
            StringBuilder sb =null;

            try {
                obj = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                conn.setRequestMethod("GET");
                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                sb = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null)
                {
                    sb.append(output);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("sb", sb.toString());

            return sb.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("String", s);
            String url2="http://192.168.137.77/coffe/";
            JSONObject json = null;

            String []id= new String[100];
            String []name= new String[100];
            String []hrg= new String[100];
            String []status= new String[100];
            String []file= new String[100];
            try {
                json = new JSONObject(s);
                JSONArray jsonarray = json.getJSONArray("rekap");

                StringBuffer st = new StringBuffer();
                int panjang =0;
                id= new String[jsonarray.length()];
                name= new String[jsonarray.length()];
                hrg= new String[jsonarray.length()];
                status= new String[jsonarray.length()];
                file= new String[jsonarray.length()];

                while (panjang<jsonarray.length())
                {
                    id[panjang]=jsonarray.getJSONObject(panjang).getString("id");
                    name[panjang]=jsonarray.getJSONObject(panjang).getString("nama");
                    hrg[panjang]=jsonarray.getJSONObject(panjang).getString("harga");
                    file[panjang]=url2+jsonarray.getJSONObject(panjang).getString("file");
                    status[panjang]=jsonarray.getJSONObject(panjang).getString("status");

                    //st.append(id+" "+nama+"\n");
                    panjang++;
                }
                Log.d("resss",response);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            myAdapter2 adapter = new myAdapter2(PesananActivity.this,name,hrg,file,id,status);
            list.setAdapter(adapter);
        }


    }
}
