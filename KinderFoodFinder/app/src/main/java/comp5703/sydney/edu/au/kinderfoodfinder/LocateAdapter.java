package comp5703.sydney.edu.au.kinderfoodfinder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.util.ArrayList;


public class LocateAdapter extends SuggestionsAdapter<LocateItem, LocateAdapter.SuggestionHolder> {

    public LocateAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    @Override
    public int getSingleViewHeight() {
        return 80;
    }

    @Override
    public SuggestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.layout_item, parent, false);
        return new SuggestionHolder(view);
    }

    @Override
    public void onBindSuggestionHolder(LocateItem suggestion, SuggestionHolder holder, int position) {
        holder.brand.setText(suggestion.getBrand());
//        holder.address.setText(suggestion.getAddress());
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                String term = constraint.toString();
                if(term.isEmpty())
                    suggestions = suggestions_clone;
                else {
                    suggestions = new ArrayList<>();
                    for (LocateItem item: suggestions_clone)
                        if(item.getBrand().toLowerCase().contains(term.toLowerCase()))
                            suggestions.add(item);
                }
                results.values = suggestions;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                suggestions = (ArrayList<LocateItem>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    static class SuggestionHolder extends RecyclerView.ViewHolder{
        protected TextView brand;
//        protected TextView address;
//        protected ImageView image;

        public SuggestionHolder(View itemView) {
            super(itemView);
            brand = (TextView) itemView.findViewById(R.id.brand_name);
//            address = (TextView) itemView.findViewById(R.id.address);
        }
    }

}