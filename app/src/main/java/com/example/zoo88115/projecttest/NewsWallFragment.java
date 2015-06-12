package com.example.zoo88115.projecttest;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsWallFragment extends Fragment {


    public NewsWallFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_news_wall, container, false);
        ListView listView=(ListView)rootView.findViewById(R.id.listView);
        MyAdapter myAdapter=new MyAdapter(getActivity());
        listView.setAdapter(myAdapter);
        return rootView;
    }


}
