package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;

public class NearbyAdapter extends BaseAdapter
{
    private Context context;
    ArrayList<Nearbydistance> distanceList;

    public NearbyAdapter (Context context, ArrayList<Nearbydistance> distanceList)
    {
        this.context = context;
        this.distanceList = distanceList;
    }

    @Override
    public int getCount() {
        return distanceList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.listview_nearby, null);
        }

        TextView address = convertView.findViewById(R.id.n_address);
        TextView distance = convertView.findViewById(R.id.n_distance);
        TextView brand = convertView.findViewById(R.id.n_brand);

        address.setText(String.valueOf(distanceList.get(position).getLocation()));
        distance.setText(String.valueOf(distanceList.get(position).getDistance()));
        brand.setText(String.valueOf(distanceList.get(position).getBrand()));

        return convertView;
    }

    public void notifyDataSetChanged()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            distanceList.sort(new Comparator<Nearbydistance>()
            {
                @Override
                public int compare(Nearbydistance d1, Nearbydistance d2)
                {
                    if(d1.getDistance() < d2.getDistance())
                    {
                        return 1;
                    }
                    else
                    {
                        return 0;
                    }
                }
            });
        }
        super.notifyDataSetChanged();
    }

}
