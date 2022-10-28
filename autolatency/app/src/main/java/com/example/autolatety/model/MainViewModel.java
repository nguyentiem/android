package com.example.autolatety.model;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.autolatety.data.Result;
import com.example.autolatety.database.ResultDatabaseClient;
import com.example.autolatety.listener.RecognizeListener;
import com.example.autolatety.text2speech.data.Utterance;
import com.example.autolatety.text2speech.viewmodel.CertificateScreenViewModel;
import com.example.autolatety.text2speech.viewmodel.TTSViewModel;
import com.example.autolatety.ui.ResultActivity;
import com.example.autolatety.utils.RecognizeUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableEmitter;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.CompletableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class MainViewModel extends ViewModel {
    private RecognizeListener listener;
    private static final String TAG = "MainViewModel";
    private Context context;
    private Activity mainAc;
    private float totalDone = 0;
    private float totalFail = 0;
    private float avrAcc = 0;
    private float avrHiLate = 0;
    private float avrResLate = 0;
    private long millis =0;
    private TextToSpeech speech;
    private int curUtterance = -1;

    private int repeatTime = 6; // by default
    private int repeat = 0;
    private float speed = 0f;
    private float pitch = 0f;
    private Bundle params = new Bundle();

    private MutableLiveData<List<Result>> resultListDb = new MutableLiveData<List<Result>>();
    private List<Result> listDb = new ArrayList<>();

    private MutableLiveData<List<Result>> resultList = new MutableLiveData<List<Result>>();
    private List<Result> list = new ArrayList<>();

    private MutableLiveData<List<Utterance>> _speechList = new MutableLiveData<List<Utterance>>();
    private List<Utterance> speeches = new LinkedList<Utterance>();

    public void setContext(Context context, RecognizeListener lis) {
        this.context = context;
        mainAc = (Activity) lis;
        listener = (RecognizeListener) lis;
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
    }

    public void clearData() {
        list = new ArrayList<>();
        update();
    }

    public void setOption(int repeat, float speed, float pitch) {
        this.speed = speed;
        this.pitch = pitch;

        this.repeat = repeat;
    }

    private void setSpeech() {
        float pitchSpeed = 0.01f;
        if (pitch != 0f) {
            pitchSpeed = pitch;
        }
        speech.setPitch(pitchSpeed);

        float rateSpeed = 0.01f;
        if (speed != 0f) {
            rateSpeed = speed;
        }
        speech.setSpeechRate(rateSpeed);
    }

    public void configHi(Result result) {

        if (speeches.size() == 0) {
           // Log.d(TAG, "playHi: do not hace hi bixby ");
            return;
        }
        speech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    speech.setLanguage(Locale.US);
                    speech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String s) {
                            result.getHi().callStart();
                        }

                        @Override
                        public void onDone(String s) {
                            mainAc.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                   // Log.d("Main", " "+System.currentTimeMillis());
                                    result.getHi().callEnd();
                                    listener.hiDone(result);


                                }
                            });
                        }

                        @Override
                        public void onError(String s) {
                            mainAc.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    result.acc = false;
                                    listener.onError(result);
                                    Log.d(TAG, "onError: ");
                                }
                            });
                        }
                    });
                }
            }
        });
        setSpeech();
        curUtterance = 0;
    }

    public void playHi(Result result) {
        curUtterance = 0;
        result.getHi().command = speeches.get(curUtterance).getContent();
        speech.speak(speeches.get(curUtterance).getContent(), TextToSpeech.QUEUE_ADD, params, "UniqueID");
        curUtterance = -1;
    }

    public void configCmd(Result result) {
        if (speeches.size() < 2) {
            return;
        }
        speech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    speech.setLanguage(Locale.US);
                    speech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String s) {
                            Completable.create(new CompletableOnSubscribe() {
                                @Override
                                public void subscribe(@NonNull CompletableEmitter emitter) throws Throwable {
                                    if (!emitter.isDisposed())
                                        emitter.onComplete();
                                }
                            }).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new CompletableObserver() {
                                        @Override
                                        public void onSubscribe(@NonNull Disposable d) {

                                        }

                                        @Override
                                        public void onComplete() {
                                            result.getCommand().command = speeches.get(1).getContent();
                                            result.getCommand().callStart();
                                        }

                                        @Override
                                        public void onError(@NonNull Throwable e) {

                                        }
                                    });
                        }

                        @Override
                        public void onDone(String s) {
                            Completable.create(new CompletableOnSubscribe() {
                                @Override
                                public void subscribe(@NonNull CompletableEmitter emitter) throws Throwable {
                                    if (!emitter.isDisposed())
                                        emitter.onComplete();
                                }
                            }).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new CompletableObserver() {
                                        @Override
                                        public void onSubscribe(@NonNull Disposable d) {

                                        }

                                        @Override
                                        public void onComplete() {
                                            result.getCommand().callEnd();
                                            listener.uttDone(result);
                                        }

                                        @Override
                                        public void onError(@NonNull Throwable e) {

                                        }
                                    });

                        }

                        @Override
                        public void onError(String s) {
                            Completable.create(new CompletableOnSubscribe() {
                                @Override
                                public void subscribe(@NonNull CompletableEmitter emitter) throws Throwable {
                                    if (!emitter.isDisposed())
                                        emitter.onComplete();
                                }
                            }).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new CompletableObserver() {
                                        @Override
                                        public void onSubscribe(@NonNull Disposable d) {
                                        }

                                        @Override
                                        public void onComplete() {
                                            result.acc = false;
                                            listener.onError(result);
                                        }

                                        @Override
                                        public void onError(@NonNull Throwable e) {
                                        }
                                    });

                        }
                    });
                }
            }
        });
        setSpeech();
    }

    public void playCmd(Result result) {
        curUtterance = 1;
        speech.speak(speeches.get(curUtterance).getContent(), TextToSpeech.QUEUE_ADD, params, "UniqueID");
        curUtterance = -1;
    }

    public void stopPlay() {
        if (speech != null && speech.isSpeaking()) {
            speech.stop();
            speech.shutdown();
        }
    }

    public void setSpeeches(ArrayList<Utterance> spee) {
        speeches = spee;
        _speechList.setValue(speeches);
    }

    public LiveData<List<Result>> getResult() {
        return resultListDb;
    }


    public LiveData<List<Result>> getListAdapter() {
        return resultList;
    }

    public void addItem(Result re) {
        list.add(re);
        caculate(list);
        resultList.postValue(list);
    }

    public void update() {
        caculate(list);
        resultList.postValue(list);
    }

    public void synData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (Result re : list) {
                    insert(re);
                }

                listDb = ResultDatabaseClient.getInstance(context).getAppDatabase()
                        .resultDao().getAll();
                list.clear();
                list.addAll(listDb);
                caculate(listDb);
                resultList.postValue(list);
            }
        }).start();

    }

    public void getDb() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("getdb", "run: querry");
                listDb = ResultDatabaseClient.getInstance(context).getAppDatabase()
                        .resultDao().getAll();
                caculate(listDb);
                resultListDb.postValue(listDb);

            }
        }).start();
    }

    public void insert(Result result) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ResultDatabaseClient.getInstance(context).getAppDatabase()
                        .resultDao().insert(result);
                getDb();
            }
        }).start();
    }

    public void delete(Result result) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ResultDatabaseClient.getInstance(context).getAppDatabase()
                        .resultDao().delete(result);

            }
        }).start();
    }

    private void caculate(List<Result> l) {
        int num = l.size();
        totalDone = 0;
        avrAcc = 0;
        avrHiLate = 0;
        avrResLate = 0;
        totalFail = 0;
        if (num == 0) {
            return;
        }
        for (Result re : l) {
            if (re.acc) {
                totalDone++;// tong dung
                avrAcc++; // trung binh dung
                avrHiLate += ((re.getTing().begin - re.getHi().finish));
                avrResLate += re.getLatety();
            } else {
                totalFail++;
            }
        }

        if (totalDone == 0) {
            totalDone = 0;
            avrAcc = 0;
            avrHiLate = 0;
            avrResLate = 0;
        } else {
            avrAcc = (float) 100.0 * totalDone / num;
            avrHiLate/=1000.0;
            avrHiLate = (float)  (avrHiLate) / (totalDone) ;
            avrResLate = (float)  (avrResLate) / totalDone ;

        }
    }


    public float getTotalDone() {
        return totalDone;
    }

    public void setTotalDone(float totalDone) {
        this.totalDone = totalDone;
    }

    public float getTotalFail() {
        return totalFail;
    }

    public void setTotalFail(float totalFail) {
        this.totalFail = totalFail;
    }

    public float getAvrAcc() {
        return avrAcc;
    }

    public void setAvrAcc(float avrAcc) {
        this.avrAcc = avrAcc;
    }

    public float getAvrHiLate() {
        return avrHiLate;
    }

    public void setAvrHiLate(float avrHiLate) {
        this.avrHiLate = avrHiLate;
    }

    public float getAvrResLate() {
        return avrResLate;
    }

    public void setAvrResLate(float avrResLate) {
        this.avrResLate = avrResLate;
    }
}