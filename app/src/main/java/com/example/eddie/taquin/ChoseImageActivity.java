package com.example.eddie.taquin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ChoseImageActivity extends AppCompatActivity implements View.OnClickListener{

    private long selectedID;
    private Uri selectedUri;
    private int selected = -1; // 0 -> ID, 1-> uri, 2->photo

    private byte[] photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose_image);

        //initOnclick
        Button imageSelect = (Button) findViewById(R.id.personnalPhoto);
        imageSelect.setOnClickListener(this);

        Button playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener(this);

        Button takePhotoButton = (Button) findViewById(R.id.takePhoto);
        takePhotoButton.setOnClickListener(this);

        //init Gridview
        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                /**
                 * on clic on an image, copy the image on the "selected image" view
                 */
                //Toast.makeText(ChoseImageActivity.this, "" + position, Toast.LENGTH_SHORT).show();

                ImageView imV = (ImageView) v;
                imV.buildDrawingCache();
                Bitmap b = imV.getDrawingCache();
                selectedID = new ImageAdapter(getApplicationContext()).getDrawableId((int)position);
                selected = 0;

                ImageView IV = (ImageView) findViewById(R.id.previewView);
                IV.setImageBitmap(b);
            }
        });

        //initSpinner
        Spinner spinner = (Spinner) findViewById(R.id.sizeSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.sizesArray, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.personnalPhoto :
                //open image picker
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");

                this.startActivityForResult(i, 0);
                break;
            case R.id.takePhoto:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 1);
                }
                break;
            case R.id.playButton:
                Spinner mySpinner=(Spinner) findViewById(R.id.sizeSpinner);
                String cuts = getResources().getStringArray(R.array.size_values)[mySpinner.getSelectedItemPosition()];
//                Toast.makeText(this, cuts,Toast.LENGTH_LONG).show();

                Intent in =new Intent(this,GameActivity.class);
                in.putExtra("cuts",cuts);
                in.putExtra("selected",selected);


                if(selected == 0){
//                    Toast.makeText(this, "0",Toast.LENGTH_LONG).show();
                    in.putExtra("id",selectedID);
                    this.startActivityForResult(in,GameActivityIndex);
                }
                else if(selected == 1 ){
//                    Toast.makeText(this, "1",Toast.LENGTH_LONG).show();
                    in.putExtra("id",selectedUri);
                    this.startActivityForResult(in,GameActivityIndex);
                }
                else if(selected == 2){

                    in.putExtra("photo",photo);
                    this.startActivityForResult(in,GameActivityIndex);
                }
                else{
                    Toast.makeText(this, getResources().getString(R.string.notSelected),Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    private final static int GameActivityIndex = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case 0:
                Uri pUri = data.getData();
                selectedUri = pUri;
                selected = 1;
                try{
                    Toast.makeText(this,pUri.toString(),Toast.LENGTH_LONG).show();

                    InputStream is = getContentResolver().openInputStream(pUri);

                    Bitmap img = BitmapFactory.decodeStream(is);
                    Bitmap resized = resizeImg(img);
                    ImageView IV = (ImageView) findViewById(R.id.previewView);
                    IV.setImageBitmap(resized);
                } catch (Exception e) {
                    Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
                break;
            case 1:
                selected = 2;
                Bundle extras = data.getExtras();
                Bitmap b = (Bitmap)extras.get("data");
                Bitmap resized = resizeImg(b);

                //Convert to byte array
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                photo = byteArray.clone();

                ImageView IV = (ImageView) findViewById(R.id.previewView);
                IV.setImageBitmap(resized);
                break;
            case GameActivityIndex:
                String action = data.getStringExtra("action");
                switch(action){
                    case "menu" :
                        finish();
                        break;
                    case "quit" :
                        Intent i = new Intent();
                        i.putExtra("action","quit");
                        setResult(Activity.RESULT_OK, i);
                        finish();
                        break;
                }
                break;
        }

    }

    private Bitmap resizeImg(Bitmap img) {

        float ratio = img.getHeight() / img.getWidth();
        int height = 512;
        int width = (int)(height*ratio);

        String s = "Height : " + img.getHeight() + " - Width : " + img.getWidth() + " - Ratio : " + ratio + " - new width " + width;

        Bitmap resized = Bitmap.createScaledBitmap(img, height, width, true);
        Bitmap cropped = Bitmap.createBitmap(resized,(resized.getWidth()-512)/2,0,512,512);

//        img.recycle();
//        resized.recycle();

        return cropped;
    }
}
