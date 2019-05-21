package comp5703.sydney.edu.au.kinderfoodfinder;





import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.AccEntity;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Accreditation;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.DaoUnit;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Product;

public class ProductAdpater extends BaseAdapter implements Filterable {

    private Context context;
    private ArrayList<AccEntity> itemsList;
    private ArrayList<AccEntity> filterList=new ArrayList<>(  );
    private ArrayList<AccEntity> productsList=new ArrayList<>(  );
    CustomFilter filter;

    public ProductAdpater (Context context, ArrayList<AccEntity> productsList){
        this.context = context;
        this.productsList = productsList;
        this.filterList = productsList;
    }

    @Override
    public int getCount() {
        return productsList.size();
    }

    @Override
    public Object getItem(int position) {
        return productsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return productsList.indexOf(getItem(position));
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

//        List<Accreditation> accreditationList=productsList.get( position ).getAccreditation();



        brand.setText(productsList.get( position ).getBrandname());
        accreditation.setText( productsList.get( position ).getAccreditation() );
        String rate=productsList.get( position ).getRating();
        rating.setText( rate );
        type.setText( productsList.get( position ).getType() );

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

                ArrayList<AccEntity> filters = new ArrayList<>();

//                for(int i=0; i<filterList.size();i++){
////                    Long id=productsList.get( i ).getParentId();
////                    Product product=DaoUnit.getInstance().searchById( id );
////
////                    String filter=product.getBrand_Name();
//                    if(filters.get( i ).getAccreditation().toUpperCase().startsWith( (String) constraint )){
//                        Accreditation acc = filterList.get(i);
//                        filters.add(acc);
//                    }
//                }


                for(int i=0; i<filterList.size();i++){

                    if(filters.get( i ).getBrandname().toUpperCase().startsWith( (String) constraint )){
                        AccEntity p = filterList.get(i);
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

            productsList = (ArrayList<AccEntity>) results.values;
            notifyDataSetChanged();
        }
    }

}

