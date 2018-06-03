package com.agprince.android.nerdlauncher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NerdLauncherFragment extends Fragment {

    private RecyclerView mRecyclerView;

    public static NerdLauncherFragment newInstance() {
        return new NerdLauncherFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View  v = inflater.inflate(R.layout.fragment_nerd_launcher,container,false);

        mRecyclerView = v.findViewById(R.id.app_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new ActivityAdapter(setupAdatper()));

        return v;
    }
    private List<ResolveInfo> setupAdatper(){
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

         PackageManager pm = getActivity().getPackageManager();

        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent,0);
        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo o1, ResolveInfo o2) {
                PackageManager pm = getActivity().getPackageManager();
                return String.CASE_INSENSITIVE_ORDER.compare(o1.loadLabel(pm).toString(),o2.loadLabel(pm).toString());
            }
        });

        LogUtil.d("Found : "+activities.size()+" activities");
        return activities;


    }
    private class ActivityHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

       private ResolveInfo mResolveInfo;
       private TextView mNameTextView;

        public ActivityHolder(View itemView) {
            super(itemView);
            mNameTextView = (TextView) itemView;
            mNameTextView.setOnClickListener(this);
        }

        public void bindActivity(ResolveInfo resolveInfo){
            mResolveInfo =resolveInfo;
            PackageManager pm = getActivity().getPackageManager();
            String name = resolveInfo.loadLabel(pm).toString();
            mNameTextView.setText(name);

        }

        @Override
        public void onClick(View v) {
            ActivityInfo activityInfo = mResolveInfo.activityInfo;
            Intent intent = new Intent(Intent.ACTION_MAIN).setClassName(activityInfo.applicationInfo.packageName,activityInfo.name).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder>{

        private List<ResolveInfo> mActivities;

        public ActivityAdapter(List<ResolveInfo> activities){
            mActivities = activities;
        }

        @NonNull
        @Override
        public ActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v= inflater.inflate(android.R.layout.simple_list_item_1, parent,false);
            return  new ActivityHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ActivityHolder holder, int position) {

            ResolveInfo resolveInfo = mActivities.get(position);
            holder.bindActivity(resolveInfo);
        }

        @Override
        public int getItemCount() {
            return mActivities.size();
        }
    }

}
