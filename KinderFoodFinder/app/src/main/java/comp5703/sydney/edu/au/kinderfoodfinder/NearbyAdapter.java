package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class NearbyViewHolder extends RecyclerView.ViewHolder{

    public TextView address, distance, brand;
    ArrayList<Nearbydistance> distanceList;
    public NearbyViewHolder(@NonNull View convertView) {
        super(convertView);

        address = convertView.findViewById(R.id.n_address);
        distance = convertView.findViewById(R.id.n_distance);
        brand = convertView.findViewById(R.id.n_brand);
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
        notifyDataSetChanged();
    }
}

public class NearbyAdapter extends RecyclerView.Adapter<NearbyViewHolder>
{
    private Context context;
    ArrayList<Nearbydistance> distanceList;

    public NearbyAdapter (Context context, ArrayList<Nearbydistance> distanceList)
    {
        this.context = context;
        this.distanceList = distanceList;
    }

    @NonNull
    @Override
    public NearbyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.listview_nearby,parent,false);

        return new NearbyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NearbyViewHolder holder, int position) {

        holder.address.setText(String.valueOf(distanceList.get(position).getLocation()));
        holder.distance.setText(String.valueOf(distanceList.get(position).getDistance()));
        holder.brand.setText(String.valueOf(distanceList.get(position).getBrand()));
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return distanceList.size();
    }



}
