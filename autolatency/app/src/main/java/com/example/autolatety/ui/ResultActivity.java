package com.example.autolatety.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.autolatety.R;
import com.example.autolatety.data.Result;
import com.example.autolatety.databinding.ActivityMainBinding;
import com.example.autolatety.listener.ItemClickListener;
import com.example.autolatety.listener.RecognizeListener;
import com.example.autolatety.model.MainViewModel;
import com.example.autolatety.text2speech.data.Utterance;
import com.example.autolatety.utils.RecognizeUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableEmitter;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.CompletableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class ResultActivity extends AppCompatActivity implements ItemClickListener, RecognizeListener {

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_AUDIO_PERMISSION_CODE = 200;
    private static final String TAG = "Main";
    private FileRecycleView adapter;
    private ActivityMainBinding binding;
    private int  repeat;
    private float speed, pitch;
    private MainViewModel model;
    private boolean doing =true;
    private int oldRepeat =0;
    private Result result;
    private boolean checkCall =false;
    private Disposable dis;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item, menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.syndb:
                model.synData();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initView();
        RequestPermissions();
        if(repeat>0){

            result =new Result();
            model.addItem(result);
        }
        startTest();
        doing = true;

    }

    private  void startTest(){
        Log.d(TAG, "");
        if(dis!= null) dis.dispose();
        if(repeat==-1) repeat =1;
        if(repeat>0) {
            checkCall =false;
           // RecognizeUtil.recognizeInit(this, result, 0, this);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        model.configHi(result);
                        Thread.sleep(500);
                        model.playHi(result);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
            repeat--;
            //RecognizeUtil.startRecognize();
        }
    }

    void initView() {
        model = new ViewModelProvider(this).get(MainViewModel.class);
        model.setContext(getApplicationContext(),this);
        Serializable speeches = getIntent().getSerializableExtra("Speeches");
        model.setSpeeches((ArrayList<Utterance>)speeches);

        repeat   = getIntent().getIntExtra("repeat", -1);
        speed  = getIntent().getFloatExtra("speed", 1f);
        pitch  = getIntent().getFloatExtra("pitch", 1f);
        oldRepeat =repeat;
        Log.d(TAG, " "+repeat+" "+speed+" "+pitch);
        model.setOption( repeat, speed, pitch);
        if(((ArrayList<?>) speeches).size()<2){
            Toast.makeText(this, "dont find any command ",Toast.LENGTH_SHORT).show();
            finish();
        }
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.toolbar.inflateMenu(R.menu.menu_item);
        adapter = new FileRecycleView(this,repeat);
        binding.recyclerView.setAdapter(adapter);

        binding.backTool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(doing){
                    binding.replay.setText("Replay");
                    doing = false;
                    model.stopPlay();
                    RecognizeUtil.stopRecognize();
                    if(result==null){
                        result =new Result();
                    }
                    result.isDone =true;
                    result.acc =true;
                    if(result.getTing().command==null||result.getTing().command.equals("")){
                        result.acc =false;
                        result.getTing().finish=0;
                    }

                    if(result.getBixbyRespone().command==null||result.getBixbyRespone().command.equals("")){
                        result.getBixbyRespone().finish=0;
                        result.acc =false;
                    }

                    if(result.getHi().command==null||result.getHi().command.equals("")){
                        result.getHi().finish=0;
                        result.acc =false;
                    }

                    if(result.getCommand().command==null||result.getCommand().command.equals("")){
                        result.getCommand().finish=0;
                        result.acc =false;
                    }
                    model.update();
                }else{
                    binding.replay.setText("Stop");
                    doing =true;
                    repeat =oldRepeat;
                    model.clearData();
                    result =new Result();
                    model.addItem(result);
                    startTest();
                }
            }
        });

        model.getResult().observe(this, new Observer<List<Result>>() {
            @Override
            public void onChanged(List<Result> list) {

            }
        });
        model.getListAdapter().observe(this, new Observer<List<Result>>() {
            @Override
            public void onChanged(List<Result> results) {
                adapter.setData(results);
                binding.totaldone.setText("Total test done: "+String.valueOf(model.getTotalDone()));
                binding.totalfail.setText("Total test fail: "+String.valueOf(model.getTotalFail()));
                binding.avrAcc.setText("Accuracy:  "+String.valueOf(model.getAvrAcc())+"%");
                binding.avrHiLate.setText("Average Hi Bixby latency: "+String.valueOf(model.getAvrHiLate())+"s");
                binding.avrResLate.setText("Average Command latency: "+String.valueOf(model.getAvrResLate())+"s");
            }
        });
    }


    public boolean CheckPermissions() {
        // this method is used to check permission
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        if (!CheckPermissions()) {
            ActivityCompat.requestPermissions(ResultActivity.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToRead = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore && permissionToRead) {
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        model.getResult();
    }

    @Override
    public void onPause() {
        super.onPause();

        model.stopPlay();
    }

    @Override
    public void onClick(Result file) {
    }

    @Override
    public void onLongClick(Result file) {
        showDialog(file);
    }

    public void showDialog(Result file) {
        new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete this Test?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        model.delete(file);
//                        model.getDb();

                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    @Override
    public void hiDone(Result result) {
        Log.d(TAG, "hiDone: ");
        RecognizeUtil.recognizeInit(this, result,1,this);
        RecognizeUtil.startRecognize();
        adapter.updateLastItem();
    }



    @Override
    public void callAgain(Result result) {// nghe ting tong
//        //RecognizeUtil.stopRecognize();
//        RecognizeUtil.recognizeInit(this, result,1,this);
//        RecognizeUtil.startRecognize();
//        Log.d(TAG, "call again: ");
//        adapter.updateLastItem();
    }

    @Override
    public void tingDone(Result result) {
        if(!checkCall) {
            checkCall =true;
            Log.d(TAG, "tingDone: ");
            model.configCmd(result);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                        model.playCmd(result);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            adapter.updateLastItem();
        }
    }

    @Override
    public void ting2Done(Result result) {
        Log.d(TAG, "ting2Done: ");
        RecognizeUtil.recognizeInit(this, result,3,this);
        RecognizeUtil.startRecognize();
//        adapter.updateLastItem();
    }

    @Override
    public void uttDone(Result result) {
        RecognizeUtil.recognizeInit(this, result,4,this);
        RecognizeUtil.startRecognize();
        Log.d(TAG, "uttDone: ");
        adapter.updateLastItem();
    }

    @Override
    public void responeDone(Result result) {
        Log.d(TAG, "responeDone: ");
        adapter.updateLastItem();
    }

    @Override
    public void onError(Result result){
        Log.d(TAG, "onError: ");
        if(repeat<=0){
            doing=false;
            binding.replay.setText("Replay");
        }
        result.acc =false;
        result.isDone =true;
        if(result.getTing().command==null||result.getTing().command.equals("")){
           // result.getTing().command="no respone wake up";
            result.getTing().finish=0;
        }

        if(result.getBixbyRespone().command==null||result.getBixbyRespone().command.equals("")){
            //result.getBixbyRespone().command="no respone from device";
            result.getBixbyRespone().finish=0;
        }

        if(result.getHi().command==null||result.getHi().command.equals("")){
           // result.getHi().command="can't start voice";
            result.getHi().finish=0;
        }

        if(result.getCommand().command==null||result.getCommand().command.equals("")){
           // result.getCommand().command="can't start command";
            result.getCommand().finish=0;
        }
        adapter.updateLastItem();
        model.update();
        if(repeat>0){
            this.result =new Result();
            model.addItem(this.result);


        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull CompletableEmitter emitter) throws Throwable {
                Thread.sleep(3000);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        dis = d;
                    }

                    @Override
                    public void onComplete() {

                        startTest();
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                    }
                });
        }
    }
    @Override
    public void onDone(Result result) {
        if(repeat<=0){
            doing=false;
            binding.replay.setText("Replay");
        }
        result.isDone =true;
        result.acc =true;
        if(result.getTing().command==null||result.getTing().command.equals("")){
            result.acc =false;
            result.getTing().finish=0;
        }

        if(result.getBixbyRespone().command==null||result.getBixbyRespone().command.equals("")){
            result.getBixbyRespone().finish=0;
            result.acc =false;
        }

        if(result.getHi().command==null||result.getHi().command.equals("")){
            result.getHi().finish=0;
            result.acc =false;
        }

        if(result.getCommand().command==null||result.getCommand().command.equals("")){
            result.getCommand().finish=0;
            result.acc =false;
        }
//        model.insert(result);
//        model.getDb();
        Log.d(TAG, "onDone: ");
        model.update();
        if(repeat>0){
            this.result =new Result();
            model.addItem(this.result);

        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull CompletableEmitter emitter) throws Throwable {
                Thread.sleep(3000);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        dis = d;
                    }

                    @Override
                    public void onComplete() {
                        startTest();
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                    }
                });
        }
    }

}