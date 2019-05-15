package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LocateAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private ArrayList<LocateItem> locateItemList;
    private ArrayList<LocateItem> filterList;


    public LocateAdapter (Context context, ArrayList<LocateItem> locateItemList){
        this.context = context;
        this.locateItemList = locateItemList;
        this.filterList = locateItemList;

    }


//    public LocateAdapter(ArrayList<LocateItem> locateItems) {
//    }


    @Override
    public Filter getFilter() {
        return locateFilter;
    }

    private Filter locateFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
//            ArrayList<LocateItem> filteredList = new ArrayList<>();
//
//            if(constraint == null || constraint.length() == 0){
//                filteredList.addAll(locateItemListFull);
//            }else{
//                String filterPattern = constraint.toString().toLowerCase().trim();
//
//                for(LocateItem item : locateItemListFull){
//                    if(item.getBrand().toLowerCase().contains(filterPattern)){
//                        filteredList.add(item);
//                    }
//                }
//            }
//
//            FilterResults results = new FilterResults();
//            results.values = filteredList;
//            return  results;
            FilterResults results = new FilterResults();

            if(constraint != null && constraint.length()>0){
                constraint = constraint.toString().toUpperCase();

                ArrayList<LocateItem> filters = new ArrayList<>();

                for(int i=0; i<filterList.size();i++){
                    if(filterList.get(i).getBrand().toUpperCase().contains(constraint)){
                        LocateItem locateItem = filterList.get(i);
                        filters.add(locateItem);
                    }
                }

                results.count = filters.size();
                results.values = filters;
            }else{
                results.count = filterList.size();
                results.values = filterList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            locateItemList.clear();;
            locateItemList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public int getCount() {
        return locateItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return locateItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return locateItemList.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.layout_item, null);
        }

        TextView brandName = convertView.findViewById(R.id.brand_name);
        brandName.setText(String.valueOf(locateItemList.get(position).getBrand()));
        return convertView;
    }
}
