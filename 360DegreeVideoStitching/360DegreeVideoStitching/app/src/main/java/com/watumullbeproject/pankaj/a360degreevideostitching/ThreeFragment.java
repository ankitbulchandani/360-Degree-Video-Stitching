package com.watumullbeproject.pankaj.a360degreevideostitching;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class ThreeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    ListView videolist;
    int count;
    String thumbPath;
    View view;
    String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA, MediaStore.Video.Thumbnails.VIDEO_ID};
    private Cursor videoCursor;
    private int videoColumnIndex;
    private AdapterView.OnItemClickListener videogridlistener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            System.gc();
            videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            videoCursor.moveToPosition(position);
            String filename = videoCursor.getString(videoColumnIndex);
            Log.i("FileName: ", filename);


            Intent intent = new Intent(getActivity(), Main2Activity.class);
            intent.putExtra("videofilename", filename);
            startActivity(intent);
        }
    };

    public ThreeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_three, container, false);
        //code should be written here

        initialization();
        return view;
    }

    private void initialization() {
        System.gc();
        String[] videoProjection = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.SIZE};
        videoCursor = getActivity().managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoProjection, null, null, null);
        count = videoCursor.getCount();
        videolist = (ListView) view.findViewById(R.id.PhoneVideoList);

        videolist.setAdapter(new VideoListAdapter(this.getActivity().getApplicationContext()));
        videolist.setOnItemClickListener(videogridlistener);
    }

    public class VideoListAdapter extends BaseAdapter {
        int layoutResourceId;
        private Context vContext;

        public VideoListAdapter(Context c) {
            vContext = c;
        }

        public int getCount() {
            return videoCursor.getCount();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View listItemRow = null;
            listItemRow = LayoutInflater.from(vContext).inflate(R.layout.listview, parent, false);

            TextView txtTitle = (TextView) listItemRow.findViewById(R.id.txtTitle);
            TextView txtSize = (TextView) listItemRow.findViewById(R.id.txtSize);
            ImageView thumbImage = (ImageView) listItemRow.findViewById(R.id.imgIcon);

            videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            videoCursor.moveToPosition(position);
            txtTitle.setText(videoCursor.getString(videoColumnIndex));

            videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
            videoCursor.moveToPosition(position);
            txtSize.setText(" Size(KB):" + videoCursor.getString(videoColumnIndex));

            int videoId = videoCursor.getInt(videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
            Cursor videoThumbnailCursor = getActivity().managedQuery(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID + "=" + videoId, null, null);

            if (videoThumbnailCursor.moveToFirst()) {
                thumbPath = videoThumbnailCursor.getString(videoThumbnailCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                Log.i("ThumbPath: ", thumbPath);

            }
            thumbImage.setImageURI(Uri.parse(thumbPath));

            return listItemRow;

        }
    }

}