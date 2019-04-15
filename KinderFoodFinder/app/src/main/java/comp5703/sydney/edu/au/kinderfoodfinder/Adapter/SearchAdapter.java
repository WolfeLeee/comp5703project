package comp5703.sydney.edu.au.kinderfoodfinder.Adapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import comp5703.sydney.edu.au.kinderfoodfinder.Model.Product_Info;
import comp5703.sydney.edu.au.kinderfoodfinder.R;

class SearchViewHolder extends RecyclerView.ViewHolder{

    public TextView brand_name, category, accreditation, rating, location;
    public SearchViewHolder(@NonNull View itemView) {
        super(itemView);
        brand_name = itemView.findViewById(R.id.brand_name);
        category = itemView.findViewById(R.id.category);
        accreditation = itemView.findViewById(R.id.accreditation);
        rating = itemView.findViewById(R.id.rating);
        location = itemView.findViewById(R.id.location);

    }
}
public class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder>{

    private Context context;
    private List<Product_Info> products;

    public SearchAdapter(Context context, List<Product_Info> products){
        this.context = context;
        this.products = products;
    }


    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.layout_item,parent,false);

        return new SearchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        holder.brand_name.setText(products.get(position).getBrand_name());
        holder.category.setText(products.get(position).getCategory());
        holder.accreditation.setText(products.get(position).getAccreditation());
        holder.rating.setText(products.get(position).getRating());
        holder.location.setText(products.get(position).getLocation());

    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
