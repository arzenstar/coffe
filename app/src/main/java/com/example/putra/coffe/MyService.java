package com.example.putra.coffe;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyService extends Service {
    backgroundThread bg = new backgroundThread();
    public String response;

    public MyService() {
    }

    public String res()
    {
        return this.response;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        this.response = intent.getStringExtra("json");
        Toast.makeText(this,"Starting "+response, Toast.LENGTH_SHORT).show();
        backgroundThread bg = new backgroundThread();
        bg.Stat(Boolean.TRUE);
        //bg.execute(response);
        bg.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,response);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        bg.Stat(Boolean.FALSE);
        Toast.makeText(this,"Stoping Service", Toast.LENGTH_SHORT).show();
    }




    public class backgroundThread extends AsyncTask<String, String, String>{
        Boolean status;
        Runnable Rn;
        String id;
        public  void  Stat(Boolean status)
        {
            this.status=status;

        }

        @Override
        protected String doInBackground(String... params) {
            final String userId = params[0];
            String hasil="Kosong";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date wk = null;
            try {
                wk = (Date) sdf.parse(sdf.format(new Date()));
            } catch (ParseException e) {
               hasil=e.getLocalizedMessage();
            }


            while (status.equals(Boolean.TRUE)) {
                try {
                    for (int y = 1; y < 1000000000; y++) {
                        for (int k=1;k<10;k++)
                        {}
                    }

                    //open connection
                    URL url = new URL("http://192.168.137.77/coffe/php/api-pesanan.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));

                    //data will be posted
                    StringBuilder post = new StringBuilder();
                    post.append(URLEncoder.encode("id", "UTF-8"));
                    post.append("=");
                    post.append(URLEncoder.encode(userId, "UTF-8"));


                    //posting data
                    writer.write(post.toString());
                    writer.flush();
                    writer.close();

                    //read response
                    BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                    StringBuilder sb = new StringBuilder();
                    String output;
                    while ((output = br.readLine()) != null) {
                        sb.append(output);
                    }

                    JSONObject json = new JSONObject(sb.toString());
                    JSONArray jsonarray = json.getJSONArray("rekap");


                    StringBuffer st = new StringBuffer();
                    int panjang = 0;
                    while (panjang < jsonarray.length()) {
                        String id = jsonarray.getJSONObject(panjang).getString("id_user");
                        String tanggal = jsonarray.getJSONObject(panjang).getString("tanggal");
                        st.append(id + " " + tanggal + "\n");
                        //Log.d("hasil", st.toString());
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = (Date) df.parse(tanggal);
                        Log.d("jam1", wk.toString());
                        Log.d("jam2", date.toString());
                        if (date.after(wk)) {
                            Log.d("jam", "berhasil");
                            wk = date;
                            Handler handler=  new Handler( getApplicationContext().getMainLooper());
                            handler.post(new Runnable() {

                                @Override
                                public void run() {

                                    // Prepare intent which is triggered if the
                                    // notification is selected
                                    Intent intent = new Intent(getApplicationContext(), PesananActivity.class);
                                    intent.putExtra("json",userId);
                                    PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);

                                    // Build notification
                                    // Actions are just fake
                                    Notification noti = new Notification.Builder(getApplicationContext())
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
                            });
                        }
                        panjang++;
                    }


                    hasil = sb.toString();
               } catch (Exception e) {
                    hasil = e.getLocalizedMessage();
                }
            }
             return hasil;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("Async", "preExecute");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Async", "postExecute");
            Toast.makeText(MyService.this,"Hasil............."+s, Toast.LENGTH_SHORT).show();
        }
    }
}
