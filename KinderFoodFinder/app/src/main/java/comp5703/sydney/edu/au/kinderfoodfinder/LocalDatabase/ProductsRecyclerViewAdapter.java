package comp5703.sydney.edu.au.kinderfoodfinder.LocalDatabase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import comp5703.sydney.edu.au.kinderfoodfinder.R;

public class ProductsRecyclerViewAdapter extends RecyclerView.Adapter<ProductsRecyclerViewAdapter.ProductsViewHolder> {

    private static final String TAG = "ProductsRecyclerViewAda";
    private List<Products> productsList;
    private Context context;

    public ProductsRecyclerViewAdapter(List<Products> productsList, Context context) {
        this.productsList = productsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_browse,viewGroup,false);
        return new ProductsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsRecyclerViewAdapter.ProductsViewHolder productsViewHolder, int position) {
        if(productsList != null && (productsList.size() != 0)){
            Products products = productsList.get(position);
            Log.d(TAG, "onBindViewHolder: "+products.getBrand()+" -----> "+position);
            productsViewHolder.Brand_Category.setText(products.getCategory());
            productsViewHolder.Brand_Title.setText(products.getBrand());
            productsViewHolder.Brand_Image.setImageBitmap(products.getImage());
        }
    }

    @Override
    public int getItemCount() {
        return ((productsList != null) && (productsList.size()!=0) ? productsList.size() : 0);
    }

    void loadNewData(List<Products> newProducts){
        productsList = newProducts;
        notifyDataSetChanged();
    }

    public Products getProducts(int position){
        return ((productsList != null)&& (productsList.size()!=0) ? productsList.get(position) : null);
    }

    static class ProductsViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "ProductsViewHolder";
        TextView Brand_Category = null;
        TextView Brand_Title = null;
        ImageView Brand_Image = null;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            this.Brand_Category = (TextView) itemView.findViewById(R.id.Brand_Category);
            this.Brand_Title = (TextView) itemView.findViewById(R.id.Brand_Title);
            this.Brand_Image = (ImageView) itemView.findViewById(R.id.Brand_Image);
        }
    }
}
