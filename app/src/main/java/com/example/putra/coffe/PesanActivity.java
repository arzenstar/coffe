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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;
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
import java.util.HashMap;

public class PesanActivity extends AppCompatActivity {
    public String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesan);

        ListView list =(ListView) findViewById(R.id.list1);
        String url = "http://192.168.137.77/coffe/php/api-item.php";


        Intent intent = getIntent();
        response = intent.getStringExtra("json");


        /*Intent ser = new Intent(PesanActivity.this, MyService.class);
        ser.putExtra("json",response);
        PesanActivity.this.startService(ser);*/



        GET get = new GET();
        get.execute(url,response);



        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView id_item = (TextView) findViewById(R.id.id_item);
                TextView user=(TextView) findViewById(R.id.textView3);
                Toast.makeText(PesanActivity.this,"id "+id_item.getText().toString()+" dipesan "+user.getText().toString(),Toast.LENGTH_LONG).show();


                    HashMap hs = new HashMap();
                    hs.put("id_item",id_item.getText().toString());
                    hs.put("id_user",user.getText().toString());
                    PostResponseAsyncTask post = new PostResponseAsyncTask(PesanActivity.this,hs, new AsyncResponse(){

                        @Override
                        public void processFinish(String s) {
                            Toast.makeText(PesanActivity.this,"pesan telah terkirim",Toast.LENGTH_SHORT).show();
                        }
                    });
                    post.execute("http://192.168.137.77/coffe/php/api-pesan.php");

            }
        });
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        if (id == R.id.action_settings) {
            Intent i = new Intent(PesanActivity.this,PesananActivity.class);
            i.putExtra("json",response);
            PesanActivity.this.startActivity(i);
            return true;
        }
        if(id == R.id.Strart)
        {
            Intent i = new Intent(PesanActivity.this,MyService.class);
            i.putExtra("json", response);
            PesanActivity.this.startService(i);
            return true;
        }
        if (id== R.id.Stop)
        {
            Intent i = new Intent(PesanActivity.this,MyService.class);
            PesanActivity.this.stopService(i);

        }

        return super.onOptionsItemSelected(item);
    }


//data item di convert menjadi list view
    public class myAdapter extends ArrayAdapter<String>
    {
        Context context;
        String []id;
        String []name;
        String []hrg;
        String []file;
        String id_user;
        myAdapter(Context c, String []nama, String []harga, String[] inmage, String []id,String id_user )
        {
            super(c,R.layout.activity_pesan,R.id.nama,nama);
            this.context = c;
            this.id=id;
            this.name=nama;
            this.hrg=harga;
            this.file=inmage;
            this.id_user=id_user;

        }

        public View getView(final int potition, View convertview, ViewGroup parent)
        {
            if (convertview == null) {
                convertview = LayoutInflater.from(getContext()).inflate(R.layout.activity_pesan, parent, false);
            }
            ImageView img =(ImageView) convertview.findViewById(R.id.imageView);
            TextView nama = (TextView) convertview.findViewById(R.id.nama);
            TextView harga = (TextView) convertview.findViewById(R.id.harga);
            TextView labelnama = (TextView) convertview.findViewById(R.id.textView4);
            TextView labelharga = (TextView) convertview.findViewById(R.id.textView5);
            TextView id_item = (TextView) convertview.findViewById(R.id.id_item);
            TextView user=(TextView) convertview.findViewById(R.id.textView3);
            Button btn = (Button) convertview.findViewById(R.id.button);

            img.setVisibility(View.VISIBLE);
            nama.setVisibility(View.VISIBLE);
            harga.setVisibility(View.VISIBLE);
            labelnama.setVisibility(View.VISIBLE);
            labelharga.setVisibility(View.VISIBLE);

            //btn.setVisibility(View.VISIBLE);

            Log.d("file", file[potition]);
            Picasso.with(PesanActivity.this).load(file[potition]).into(img);
            nama.setText(name[potition]);
            harga.setText(hrg[potition]);
            id_item.setText(id[potition]);
            user.setText(id_user);



            return convertview;
        }
    }

//mendapatkan data item
    public class GET extends AsyncTask<String, String, String>
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
            String []file= new String[100];
            try {
                json = new JSONObject(s);
                JSONArray jsonarray = json.getJSONArray("item");

                StringBuffer st = new StringBuffer();
                int panjang =0;
                id= new String[jsonarray.length()];
                name= new String[jsonarray.length()];
                hrg= new String[jsonarray.length()];
                file= new String[jsonarray.length()];

                while (panjang<jsonarray.length())
                {
                    id[panjang]=jsonarray.getJSONObject(panjang).getString("id");
                    name[panjang]=jsonarray.getJSONObject(panjang).getString("nama");
                    hrg[panjang]=jsonarray.getJSONObject(panjang).getString("harga");
                    file[panjang]=url2+jsonarray.getJSONObject(panjang).getString("file");
                    //st.append(id+" "+nama+"\n");
                    panjang++;
                }
                Log.d("resss",response);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            myAdapter adapter = new myAdapter(PesanActivity.this,name,hrg,file,id,response);
            list.setAdapter(adapter);
        }


    }
}
