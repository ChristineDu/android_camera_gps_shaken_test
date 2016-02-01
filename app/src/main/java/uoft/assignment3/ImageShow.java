package uoft.assignment3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Christine on 16-01-31.
 */
public class ImageShow extends Activity{
    Context ctx;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_image);
        Intent intent = getIntent();
        final String position = intent.getStringExtra("POSITION");

        ImageView jpgView = (ImageView)findViewById(R.id.view);
        Bitmap bitmap = BitmapFactory.decodeFile(position);
        jpgView.setImageBitmap(bitmap);

        Button button = (Button) findViewById(R.id.delete);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                File file = new File(position);
                Log.i("delete",position);
                Toast.makeText(ImageShow.this, "Picture Deleted", Toast.LENGTH_SHORT).show();
                Boolean del = file.delete();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                        Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                Log.i("delete succedd?",String.valueOf(del));
                onBackPressed();

            }
        });
    }



}
