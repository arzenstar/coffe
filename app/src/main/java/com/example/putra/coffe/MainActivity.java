package com.example.putra.coffe;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    TextView text, username, password,isijason;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text=(TextView) findViewById(R.id.textView);
        isijason=(TextView) findViewById(R.id.textView2);
        username = (TextView) findViewById(R.id.username);
        password = (TextView) findViewById(R.id.password);
    }

    public void login(View v)
    {

        String user=username.getText().toString();
        String pass= password.getText().toString();
        HashMap hs = new HashMap();
        hs.put("username",user);
        hs.put("password",pass);
        PostResponseAsyncTask post = new PostResponseAsyncTask(MainActivity.this,hs, new AsyncResponse() {
            @Override
            public void processFinish(String s) {
                JSONObject json=null;
                JSONArray jsonarray = null;


                try {
                    json = new JSONObject(s);
                    jsonarray = json.getJSONArray("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                StringBuffer st = new StringBuffer();
                int panjang =0;
                Log.d("panjang", String.valueOf(jsonarray.length()));
                String id=null;
                while (panjang<jsonarray.length())
                {

                    try {
                        id = jsonarray.getJSONObject(panjang).getString("id");
                        String nama = jsonarray.getJSONObject(panjang).getString("username");
                        String email = jsonarray.getJSONObject(panjang).getString("email");
                        String password = jsonarray.getJSONObject(panjang).getString("password");
                        //iduser=id;
                        isijason.setText(id);
                        st.append(id+" "+nama+"\n");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    panjang++;
                }
                text.setText(st.toString());
                Intent i = new Intent(MainActivity.this, PesanActivity.class);
                i.putExtra("json",isijason.getText().toString());
                MainActivity.this.startActivity(i);

               /* Intent ser = new Intent(MainActivity.this, MyService.class);
                ser.putExtra("json",isijason.getText().toString());
                MainActivity.this.startService(ser);*/

            }
        });
        post.execute("http://192.168.137.77/coffe/php/api-login.php");
        Log.d("userid",isijason.getText().toString());
        /*if(text.toString()!=null)
        {
            Intent i = new Intent(this, PesanActivity.class);
            i.putExtra("json",isijason.getText().toString());
            MainActivity.this.startActivity(i);
        }*/
    }

    public void start(View v)
    {
        Intent i = new Intent(this, MyService.class);
        startService(i);
    }

    public  void stop(View v)
    {
        Intent i = new Intent(this, MyService.class);
        stopService(i);
    }
    public void post(View v)  {
        /*Asy asyc = new Asy();
        asyc.execute("POST");*/

        HashMap hs = new HashMap();
        hs.put("name","putra");
        hs.put("email","putra");
        PostResponseAsyncTask post = new PostResponseAsyncTask(MainActivity.this,hs, new AsyncResponse() {
            @Override
            public void processFinish(String s) {
                JSONObject json = null;
                try {
                    json = new JSONObject(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray jsonarray = null;
                try {
                    jsonarray = json.getJSONArray("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                StringBuffer st = new StringBuffer();
                int panjang =0;
                while (panjang<jsonarray.length())
                {
                    String id = null;
                    try {
                        id = jsonarray.getJSONObject(panjang).getString("id");
                        String nama = jsonarray.getJSONObject(panjang).getString("status");
                        st.append(id+" "+nama+"\n");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    panjang++;
                }

                text.setText(st.toString());
            }
        });
        post.execute("http://192.168.137.77/crimemap/post.php");

    }
    public void get(View v)  {
        Asy asyc = new Asy();
        asyc.execute("GET");
    }
    public void notif(View v)
    {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(this)
                .setContentTitle("New mail from " + "test@gmail.com")
                .setContentText("anda mendapat pesan baru silahkan anda buka secepatnya").setSmallIcon(R.drawable.ic_stat_name)
                .setContentIntent(pIntent)
                .addAction(R.drawable.ic_stat_name, "Call", pIntent)
                .addAction(R.drawable.ic_stat_name, "More", pIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .addAction(R.drawable.ic_stat_name, "And more", pIntent).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);
    }



    public class Asy extends AsyncTask<String, String, String>
    {
        String Hasil;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            TextView text =(TextView) findViewById(R.id.textView);
        }

        @Override
        protected String doInBackground(String... params) {
        String result=null;
            String data=params[0];
            if(data=="POST")
            {

                try {
                    Hasil =Post("http://192.168.137.77/crimemap/post.php","name","text/html; charset=UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(data=="GET")
            {
                try {
                    Hasil =Get("http://192.168.137.77/crimemap/api.php");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            text.setText(Hasil);
        }

        public String Get(String url) throws Exception {
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("GET");

            //Running request
            int respCode = conn.getResponseCode(); // TODO Handle error codes!

            //Reading response
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null)
            {
                sb.append(output);
            }

            JSONObject json = new JSONObject(sb.toString());
            JSONArray jsonarray = json.getJSONArray("map");

            StringBuffer st = new StringBuffer();
            int panjang =0;
            while (panjang<jsonarray.length())
            {
                String id = jsonarray.getJSONObject(panjang).getString("report_id");
                String nama = jsonarray.getJSONObject(panjang).getString("crime_name");
                st.append(id+" "+nama+"\n");
                panjang++;
            }

            return st.toString();
        }

        public String Post(String url, String postData, String contentType) throws Exception {
            DefaultHttpClient hc = new DefaultHttpClient();
            ResponseHandler response = new BasicResponseHandler();
            HttpPost http = new HttpPost(url);
            ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
            nameValuePair.add(new BasicNameValuePair("name", "test_user"));
            nameValuePair.add(new BasicNameValuePair("email", "123456789"));
            http.setHeader("Content-Type", contentType);
            http.setEntity(new UrlEncodedFormEntity(nameValuePair));
            final String resp = (String) hc.execute(http, response);
            return resp;

        }
    }

}
