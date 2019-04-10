package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemsAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private ArrayList<Items> itemsList;
    private ArrayList<Items> filterList;
    CustomFilter filter;

    public ItemsAdapter (Context context, ArrayList<Items> itemsList){
        this.context = context;
        this.itemsList = itemsList;
        this.filterList = itemsList;
    }

    @Override
    public int getCount() {
        return itemsList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return itemsList.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.listview_item, null);
        }


        TextView brand = convertView.findViewById(R.id.brand);
        TextView accreditation =convertView.findViewById( R.id.accreditation );
        TextView rating=convertView.findViewById( R.id.rating );
        TextView type=convertView.findViewById( R.id.type );


        brand.setText(itemsList.get(position).getBrand());
        accreditation.setText( itemsList.get( position ).getAccreditation() );
        String rate=itemsList.get( position ).getRating();
        rating.setText( itemsList.get( position ).getRating() );
        type.setText( itemsList.get( position ).getType() );

        if(rate.equalsIgnoreCase( "BEST" )){
            rating.setTextColor( Color.parseColor("#208E5C"));
        }if(rate.equalsIgnoreCase( "GOOD" )){
            rating.setTextColor( Color.parseColor("#f7912f"));

        }if(rate.equalsIgnoreCase( "AVOID" )){
            rating.setTextColor( Color.parseColor("#FF4081"));

        }


        return convertView;
    }

    @Override
    public Filter getFilter() {

        if(filter == null ){
            filter = new CustomFilter();
        }
        return filter;
    }

    class CustomFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if(constraint != null && constraint.length()>0){
                constraint = constraint.toString().toUpperCase();

                ArrayList<Items> filters = new ArrayList<>();

                for(int i=0; i<filterList.size();i++){
                    if(filterList.get(i).getBrand().toUpperCase().contains(constraint)){
                        Items p = filterList.get(i);
                        filters.add(p);
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

            itemsList = (ArrayList<Items>) results.values;
            notifyDataSetChanged();
        }
    }

}
