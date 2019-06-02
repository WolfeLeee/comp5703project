package comp5703.sydney.edu.au.kinderfoodfinder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.util.ArrayList;


public class LocateAdapter extends SuggestionsAdapter<String, LocateAdapter.SuggestionHolder>
{
    private SuggestionsAdapter.OnItemViewClickListener listener;
    private int viewHeight;

    public LocateAdapter(LayoutInflater inflater, int viewHeight)
    {
        super(inflater);
        this.viewHeight = viewHeight;
    }

    public void setListener(SuggestionsAdapter.OnItemViewClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public int getSingleViewHeight()
    {
        return viewHeight;
    }

    @Override
    public SuggestionHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = getLayoutInflater().inflate(R.layout.layout_item, parent, false);
        return new SuggestionHolder(view);
    }

    @Override
    public void onBindSuggestionHolder(String suggestion, SuggestionHolder holder, int position)
    {
        holder.brand.setText(suggestion);
    }

    public interface OnItemViewClickListener
    {
        void OnItemClickListener(int position, View v);

        void OnItemDeleteListener(int position, View v);
    }

    class SuggestionHolder extends RecyclerView.ViewHolder
    {
        protected TextView brand;

        public SuggestionHolder(View itemView)
        {
            super(itemView);
            brand = (TextView) itemView.findViewById(R.id.brand_name);
            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    v.setTag(getSuggestions().get(getAdapterPosition()));
                    listener.OnItemClickListener(getAdapterPosition(), v);
                }
            });
        }
    }
}