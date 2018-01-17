package com.dattran.viewdraghelper.DragHelper;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;

import com.dattran.viewdraghelper.R;
import com.dattran.viewdraghelper.pedrovgs.DraggableListener;
import com.dattran.viewdraghelper.pedrovgs.DraggablePanel;
import com.dattran.viewdraghelper.pedrovgs.DraggableView;

public class MainActivity extends AppCompatActivity {
    DraggablePanel draggablePanel;
    DraggableView draggableView;
    VideoView videoView;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView=findViewById(R.id.iv_fan_art);
//        videoView.setMediaController(new MediaController(this));
//        int idVideo=getResources().getIdentifier("english","raw",getPackageName());
        draggableView=findViewById(R.id.draggable_view);
        String path = "android.resource://" + getPackageName() + "/" + R.raw.english;
        videoView.setVideoURI(Uri.parse(path));
        //        videoView.setVideoPath("http://dattran.tk/English-Conversation-37.mp4");
        videoView.start();
//        draggablePanel=findViewById(R.id.draggable_panel);
//        draggableView.setDraggableListener(new DraggableListener() {
//            @Override
//            public void onMaximized() {
//                videoView.start();
//                YoutubeLayout.print("onMaximized");
//            }
//
//            @Override
//            public void onMinimized() {
//                YoutubeLayout.print("onMinimized");
//
//            }
//
//            @Override
//            public void onClosedToLeft() {
//                videoView.pause();
//                YoutubeLayout.print("onClosedToLeft");
//
//            }
//
//            @Override
//            public void onClosedToRight() {
//                videoView.pause();
//                YoutubeLayout.print("onClosedToRight");
//
//            }
//        });
//        findViewById(R.id.buttonDragH).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                if ( !draggableView.isMinimized()){
////                    Toast.makeText(MainActivity.this, "ko nho nhat 0", Toast.LENGTH_SHORT).show();
////                }
////                draggableView.maximize();
//                Intent intent = new Intent(MainActivity.this, DragActivity.class);
//                intent.putExtra("horizontal", true);
//                startActivity(intent);
//            }
//        });
////        ListView lsView=findViewById(R.id.lv_episodes);
////        lsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////            @Override
////            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                if ( draggableView.isMinimized()){
////                    Toast.makeText(MainActivity.this, "nho nhat 1", Toast.LENGTH_SHORT).show();
////                }
////            }
////        });
//        findViewById(R.id.buttonDragV).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, DragActivity.class);
//                intent.putExtra("vertical", true);
//                startActivity(intent);
//            }
//        });
//        findViewById(R.id.buttonDragEdge).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, DragActivity.class);
//                intent.putExtra("edge", true);
//                startActivity(intent);
//            }
//        });
//        findViewById(R.id.buttonDragCapture).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, DragActivity.class);
//                intent.putExtra("capture", true);
//                startActivity(intent);
//            }
//        });
//        findViewById(R.id.buttonYoutube).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, YoutubeActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    private void initializeDraggablePanel() throws Resources.NotFoundException {
        draggablePanel.setFragmentManager(getSupportFragmentManager());
        draggablePanel.setTopFragment(new FragmentOne());
        draggablePanel.setBottomFragment(new FragmentTwo());
        draggablePanel.initializeView();
        draggablePanel.setTopFragmentResize(true);


    }
    public int getRawResIdByName(String resName) {
        String pkgName = this.getPackageName();

        // Trả về 0 nếu không tìm thấy.
        int resID = this.getResources().getIdentifier(resName, "raw", pkgName);
        Log.i("AndroidVideoView", "Res Name: " + resName + "==> Res ID = " + resID);
        return resID;
    }
}
