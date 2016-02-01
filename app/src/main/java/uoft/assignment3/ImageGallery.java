package uoft.assignment3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christine on 16-01-31.
 */

public class ImageGallery extends Activity {

    private ImageAdapter imageAdapter;
    ArrayList<String> f = new ArrayList<String>();
    File[] listFile;
    GridView imagegrid;
    Context ctx;
    //public static String message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.gallery);
        getFromSdcard();
        imagegrid = (GridView) findViewById(R.id.gridview);
        imageAdapter = new ImageAdapter(this);
        imagegrid.setAdapter(imageAdapter);
        imagegrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Intent intent = new Intent(ctx, ImageShow.class);
                intent.putExtra("POSITION",f.get(position));
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onResume() {
        getFromSdcard();
        Log.i("resume", "asdfasdf");
        imageAdapter.notifyDataSetChanged();
        super.onResume();

    }

    public void getFromSdcard()
    {
        File sdCard = Environment.getExternalStorageDirectory();
        File file = new File (sdCard.getAbsolutePath() + "/Assignment3");
        if (file.isDirectory())
        {
            listFile = file.listFiles();
            f.clear();
            for (int i = 0; i < listFile.length; i++)
            {
                f.add(listFile[i].getAbsolutePath());
            }
        }
    }

    private final class ImageAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;

        public ImageAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return f.size();
        }
        @Override
        public Object getItem(int i) {
            return i;
        }
        @Override
        public long getItemId(int i) {
            return i;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = view;
            ImageView picture;
            TextView name;

            if (v == null) {
                v = mInflater.inflate(R.layout.item, viewGroup, false);
                v.setTag(R.id.picture, v.findViewById(R.id.picture));
                v.setTag(R.id.text, v.findViewById(R.id.text));
            }

            picture = (ImageView) v.getTag(R.id.picture);
            name = (TextView) v.getTag(R.id.text);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize =16;
            Bitmap myBitmap = BitmapFactory.decodeFile(f.get(i),options);
            picture.setImageBitmap(myBitmap);
            String fil = f.get(i);
            Log.i("filename", fil);
            String filename=fil.substring(fil.lastIndexOf("/")+1);
            String[] tmp = filename.split(",");
            String files = tmp[0]+","+tmp[1];
            name.setText(files);
            return v;
        }

    }
}