package comp5703.sydney.edu.au.kinderfoodfinder.Adapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.List;

import comp5703.sydney.edu.au.kinderfoodfinder.Model.Product_Info;
import comp5703.sydney.edu.au.kinderfoodfinder.R;
import comp5703.sydney.edu.au.kinderfoodfinder.ReportAddressFragment;

class BrandViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView list_brand, list_category;
    OnItemClickListener mOnItemClickListener;
    public BrandViewHolder(@NonNull View itemView) {
        super(itemView);
        list_brand = itemView.findViewById(R.id.list_brand);
        list_category = itemView.findViewById(R.id.list_category);

        itemView.setOnClickListener(this);

    }

    public interface OnItemClickListener {
        void onItemClick (int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public void onClick(View v) {
        this.mOnItemClickListener.onItemClick(this.getLayoutPosition());

    }
}
public class BrandAdapter extends RecyclerView.Adapter<BrandViewHolder>{

    private FragmentActivity context;
    private List<Product_Info> products;


    public BrandAdapter(FragmentActivity context, List<Product_Info> products){
        this.context = context;
        this.products = products;
    }



    @NonNull
    @Override
    public BrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.brand_item,parent,false);


        return new BrandViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull BrandViewHolder holder, final int position) {

        final String brand = products.get(position).getBrand_name();
        final String category = products.get(position).getCategory();

        holder.list_brand.setText(brand);
        holder.list_category.setText(category);

        holder.setOnItemClickListener(new BrandViewHolder.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                openAddressFragment(brand,category);

            }
        });



    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    private void openAddressFragment(String brand, String category){

        /*Intent intent = new Intent(context, ReportAddressFragment.class);


        intent.putExtra("BRAND_KEY", brand);
        intent.putExtra("CATEGORY_KEY", category);*/

        // pack data to send
        Bundle bundle = new Bundle();
        bundle.putString("BRAND_KEY", brand);
        bundle.putString("CATEGORY_KEY", category);
        ReportAddressFragment fragmentaddress = new ReportAddressFragment();
        fragmentaddress.setArguments(bundle);

        // open fragment
        FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, fragmentaddress);
        transaction.addToBackStack(null);
        transaction.commit();

    }
}
