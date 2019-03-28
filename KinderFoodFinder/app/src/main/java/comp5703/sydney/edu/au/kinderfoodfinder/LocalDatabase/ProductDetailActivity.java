package comp5703.sydney.edu.au.kinderfoodfinder.LocalDatabase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import comp5703.sydney.edu.au.kinderfoodfinder.R;

import static comp5703.sydney.edu.au.kinderfoodfinder.SearchFragment.IMAGE_DETAIL;
import static comp5703.sydney.edu.au.kinderfoodfinder.SearchFragment.PRODUCT_DETAIL;

public class ProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);
        Intent intent = getIntent();
        Products products = (Products) intent.getSerializableExtra(PRODUCT_DETAIL);
        byte[] byteArray = (byte[]) intent.getSerializableExtra(IMAGE_DETAIL);
        Bitmap image = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);

        if(products != null){
            TextView Brand_Title = (TextView) findViewById(R.id.Detail_Brand_Title);
            ImageView Brand_Image = (ImageView) findViewById(R.id.Detail_Brand_Image);
            Brand_Title.setText(products.getBrand());
            Brand_Image.setImageBitmap(image);
        }

    }

//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.product_detail,container,false);
//        Intent intent = getIntent();
//        Products products = (Products) intent.getSerializableExtra(PRODUCT_DETAIL);
//        byte[] byteArray = (byte[]) intent.getSerializableExtra(IMAGE_DETAIL);
//        Bitmap image = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
//
//        if(products != null){
//            TextView Brand_Title = (TextView) view.findViewById(R.id.Brand_Title);
//            ImageView Brand_Image = (ImageView) view.findViewById(R.id.Brand_Image);
//            Brand_Title.setText(products.getBrand());
//            Brand_Image.setImageBitmap(image);
//        }
//        return view;
//
//    }

}
