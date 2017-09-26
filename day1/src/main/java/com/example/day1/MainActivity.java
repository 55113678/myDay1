package com.example.day1;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import Bean.Beandata;
import Utils.XListView;

public class MainActivity extends AppCompatActivity implements XListView.IXListViewListener {

    private XListView xlv;
    private MyAdapter adapter;
    private List<Beandata.DataBean.AdlistBean> adlist;
    private int num = 0;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            close();

            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xlv = (XListView) findViewById(R.id.xlv);

        readFile();
    }

    private void readFile() {

        new Thread(){

            private String s;

            @Override
            public void run() {

                String path = "http://www.meirixue.com/api.php?c=index&a=index";

                try {
                    URL url = new URL(path);

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(5000);
                    connection.setConnectTimeout(5000);

                    int responseCode = connection.getResponseCode();

                    if(responseCode == 200){
                        InputStream inputStream = connection.getInputStream();

                        byte[] buffer = new byte[1024];
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        int len;

                        while((len = inputStream.read(buffer))!=-1){
                            outputStream.write(buffer,0,len);
                        }

                        s = outputStream.toString();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Gson gson = new Gson();

                            Beandata dataBean = gson.fromJson(s, Beandata.class);

                            adlist = dataBean.getData().getAdlist();

                            adapter = new MyAdapter();
                            xlv.setAdapter(adapter);

                            xlv.setPullLoadEnable(true);
                            xlv.setPullRefreshEnable(true);

                            xlv.setXListViewListener(MainActivity.this);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void close() {

        xlv.stopRefresh();
        xlv.stopLoadMore();
        xlv.setRefreshTime("2017:8:25");
    }

    @Override
    public void onRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                adlist.add(0, adlist.get(num++));

                handler.sendEmptyMessage(0);
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                adlist.add(adlist.get(num++));

                handler.sendEmptyMessage(0);
            }
        }, 2000);
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return adlist.size();
        }

        @Override
        public Object getItem(int position) {
            return adlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = View.inflate(MainActivity.this, R.layout.item, null);

            TextView tv = (TextView) convertView.findViewById(R.id.tv);
            ImageView iv = (ImageView) convertView.findViewById(R.id.iv);

            tv.setText(adlist.get(position).getTitle());
            ImageLoader.getInstance().displayImage(adlist.get(position).getImg(),iv);
            return convertView;
        }
    }
}
