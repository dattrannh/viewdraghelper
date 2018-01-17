package com.dattran.viewdraghelper.MyDragHelper;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.ZoomControls;

import com.dattran.viewdraghelper.DragHelper.YoutubeLayout;
import com.dattran.viewdraghelper.R;

import static com.dattran.viewdraghelper.MyDragHelper.MyActivity.Mode.*;


/**
 * Created by DatTran on 05/01/2018.
 */

public class MyActivity extends AppCompatActivity {
    VideoView img;
    RelativeLayout.LayoutParams params;
    RelativeLayout rootMy;
    float dx = 0, dy = 0, oldDist = 1f, d, newRot, angle, scalediff;
    int width, height, currentVolume, maxVolume;
    MyRelative myRelative;
    //    RelativeLayout myRelative;
    Mode mode = NONE;
    GestureDetector gestureDetector;
    LinearLayout linearLayout;
    TextView textView;
    private final int speed = 100, swipe = 100;
    int screenWidth, screenHeight;
    private AudioManager audioManager;
    private float x, y;
    WindowManager.LayoutParams brightness;
    private final float MIN_DISTANCE = 12f;
    private final int HORIZONTAL = 1, VERTICAL = 2, NO = 0;
    private int orientation, duration = 0, seek = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_activity);
        img = findViewById(R.id.videoView);
        linearLayout = findViewById(R.id.touch_linear);
//        img.setMediaController(new MediaController(this));
        String path = "android.resource://" + getPackageName() + "/" + R.raw.english;
        img.setVideoURI(Uri.parse(path));
        img.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                duration = mp.getDuration();
                mp.start();
            }
        });
        rootMy = findViewById(R.id.root_my);
        myRelative = findViewById(R.id.my_relative);
        textView = findViewById(R.id.textV);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRelative.maximize();
            }
        });

        myRelative.setDraggableListener(new MyRelative.DraggableListener() {
            @Override
            public void onMaximized() {
                if (!img.isPlaying())
                    img.start();
            }

            @Override
            public void onMinimized() {
            }

            @Override
            public void onClosed() {
                img.pause();
            }
        });

        gestureDetector = new GestureDetector(this, new GestureListener());

        linearLayout.setOnTouchListener(new View.OnTouchListener() {
            float bright;
            int curBrightnessValue;
            int pos = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                gestureDetector.onTouchEvent(event);
                int action = event.getAction();
                switch (action & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        orientation = NO;
                        x = event.getX();
                        y = event.getY();
                        bright = brightness.screenBrightness;
                        screenWidth = linearLayout.getWidth();
                        screenHeight = linearLayout.getHeight() / 2;
                        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dy = event.getY() - y;
                        float dx = event.getX() - x;
                        if (orientation == NO) {
                            if (Math.abs(dx) > MIN_DISTANCE) {
                                orientation = HORIZONTAL;
                            } else if (Math.abs(dy) > MIN_DISTANCE) {
                                orientation = VERTICAL;
                            }
                        } else {
                            if (orientation == VERTICAL) {
                                if (2 * x < screenWidth) {
                                    int volume = currentVolume - (int) (maxVolume * dy / screenHeight);
                                    if (volume <= maxVolume && volume >= 0) {
                                        setCurrentVolume(volume);
                                    }
                                } else {
                                    float br = bright - dy / screenHeight;
                                    br = Math.max(Math.min(br, 1f), 0f);
                                    setBrightness(br);
                                }
                            } else {
                                pos = (int) (100 * dx / screenWidth);
                                if (pos > 0) {
                                    textView.setText("them=+" + pos + "s");
                                } else {
                                    textView.setText("tru=" + pos + "s");
                                }
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (orientation == HORIZONTAL) {
                            seek += pos;
                            if (seek > 0) {
                                textView.setText("them=+" + seek + "s"+", current="+img.getCurrentPosition()/1000);
                            } else {
                                textView.setText("tru=" + seek + "s"+", current="+img.getCurrentPosition()/1000);
                            }
                            handler.removeCallbacks(runnable);
                            handler.postDelayed(runnable, 800);
                        }
                }
                return true;
            }
        });
        setTech();
    }

    private void setCurrentVolume(int volume) {
        textView.setText("volume=" + volume + ", max=" + maxVolume);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.ADJUST_SAME);
    }

    private void setBrightness(float bright) {
        int per = (int) (bright * 100);
        textView.setText("bright=" + per + "%");
        brightness.screenBrightness = bright;
        getWindow().setAttributes(brightness);
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            seekTo();
        }
    };

    private void seekTo() {
        int pos = img.getCurrentPosition() + seek * 1000;
        if (pos > 0 && pos < duration) {
            img.seekTo(pos);
        }
        seek = 0;
    }


    private ContentResolver cResolver;

    private void setTech() {
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        cResolver = getContentResolver();
        float bright = android.provider.Settings.System.getFloat(cResolver, android.provider.Settings.System.SCREEN_BRIGHTNESS, -1);
        brightness = getWindow().getAttributes();
        brightness.screenBrightness = bright / 255;
        getWindow().setAttributes(brightness);
//        try {
//            int curBrightnessValue = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
//            textView.setText("br=" + curBrightnessValue);
//        } catch (Settings.SettingNotFoundException e) {
//            e.printStackTrace();
//        }

    }

    private void setBrightness(int bright) {
        textView.setText("bright=" + bright + "%");
        android.provider.Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, bright);
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) {

            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                        .setData(Uri.parse("package:" + this.getPackageName()))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float dx = e2.getX() - e1.getX();
            float dy = e2.getY() - e1.getY();
            if (dx > swipe && Math.abs(velocityX) > speed) {
                MyRelative.print("tu trai qua phai -->");
            } else if (dx < -swipe && Math.abs(velocityX) > speed) {
                MyRelative.print("tu phai qua trai  <--");
            }
            if (dy > swipe && Math.abs(velocityY) > speed) {
                MyRelative.print("tu tren xuong duoi ");
            } else if (dy < -swipe && Math.abs(velocityY) > speed) {
                MyRelative.print("tu duoi len tren ");
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }


    private void setOnTouch() {
        img.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        params = (RelativeLayout.LayoutParams) img.getLayoutParams();
                        dx = event.getRawX() - params.leftMargin;
                        dy = event.getRawY() - params.topMargin;
                        width = rootMy.getWidth() - img.getWidth();
                        height = rootMy.getHeight() - img.getHeight();
                        mode = DRAG;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(event);
                        if (oldDist > 10f) {
                            mode = ZOOM;
                        }
                        d = rotation(event);
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            int w = (int) (event.getRawX() - dx);
                            int h = (int) (event.getRawY() - dy);
//                            params.leftMargin = Math.min(Math.max(0, w), width);
//                            params.topMargin = Math.min(Math.max(0, h), height);
                            params.leftMargin = w;
                            params.topMargin = h;
////                        params.rightMargin = -250;
////                        params.bottomMargin = -250;
                            img.setLayoutParams(params);
                        } else if (mode == ZOOM) {
                            if (event.getPointerCount() == 2) {
                                newRot = rotation(event);
                                angle = newRot - d;
                                float newDist = spacing(event);
                                if (newDist > 10f) {
                                    float scale = newDist / oldDist * img.getScaleX();
                                    if (scale > 0.6) {
                                        scalediff = scale;
                                        img.setScaleX(scale);
                                        img.setScaleY(scale);
                                    }
                                }
                                img.animate().rotationBy(angle).setDuration(0).setInterpolator(new LinearInterpolator()).start();
//                                int w = (int) (event.getRawX() - dx+scalediff);
//                                int h = (int) (event.getRawY() - dy+scalediff);
//                                params.leftMargin =w;
//                                params.topMargin = h;
//                                img.setLayoutParams(params);
                            }
                        }
                        break;
                }
//                rootMy.invalidate();
                return true;
            }
        });
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.hypot(x, y);
    }

    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    public static enum Mode {
        DRAG, ZOOM, NONE
    }


}
