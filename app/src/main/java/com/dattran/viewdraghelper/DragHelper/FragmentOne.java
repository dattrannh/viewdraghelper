package com.dattran.viewdraghelper.DragHelper;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.dattran.viewdraghelper.R;

/**
 * Created by DatTran on 08/01/2018.
 */

public class FragmentOne extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_one,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        VideoView videoView=view.findViewById(R.id.videoView);
        String path = "android.resource://" + getActivity().getPackageName() + "/" + R.raw.english;
        videoView.setVideoURI(Uri.parse(path));
        //        videoView.setVideoPath("http://dattran.tk/English-Conversation-37.mp4");
        videoView.start();
    }
}
