package me.maxpro.workscheduler.utils;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.maxpro.workscheduler.CalendarActivity;

public class ExampleLoader extends AppCompatActivity {


    // A RecyclerView.Adapter which will display the data
    private RecyclerView.Adapter mAdapter;

    // Our Callbacks. Could also have the Activity/Fragment implement LoaderManager.LoaderCallbacks<List<String>>
    private LoaderManager.LoaderCallbacks<List<String>> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<List<String>>() {
        @Override
        public Loader<List<String>> onCreateLoader(int id, Bundle args) {
//            binding.loadingView.setVisibility(View.VISIBLE);
//            binding.activityCalendar.setVisibility(View.GONE);
            return new CalendarAsyncTaskLoader(ExampleLoader.this);
        }
        @Override
        public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
            // Display our data, for instance updating our adapter
//            mAdapter.setData(data);
//            binding.loadingView.setVisibility(View.GONE);
//            binding.activityCalendar.setVisibility(View.VISIBLE);
        }
        @Override
        public void onLoaderReset(Loader<List<String>> loader) {
            // Loader reset, throw away our data,
            // unregister any listeners, etc.
//            mAdapter.setData(null);
            // Of course, unless you use destroyLoader(),
            // this is called when everything is already dying
            // so a completely empty onLoaderReset() is
            // totally acceptable
        }
    };

    private static class CalendarAsyncTaskLoader extends AsyncTaskLoader<List<String>> {

        // You probably have something more complicated
        // than just a String. Roll with me
        private List<String> mData;

        public CalendarAsyncTaskLoader(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            if (mData != null) {
                // Use cached data
                deliverResult(mData);
            } else {
                // We have no data, so kick off loading it
                forceLoad();
            }
        }

        @Nullable
        @Override
        public List<String> loadInBackground() {
            // This is on a background thread
            // Good to know: the Context returned by getContext()
            // is the application context
//            File jsonFile = new File(getContext().getFilesDir(), "downloaded.json");
            List<String> data = new ArrayList<>();
            // Parse the JSON using the library of your choice
            // Check isLoadInBackgroundCanceled() to cancel out early
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return data;
        }

        @Override
        public void deliverResult(List<String> data) {
            // We’ll save the data for later retrieval
            mData = data;
            // We can do any pre-processing we want here
            // Just remember this is on the UI thread so nothing lengthy!
            super.deliverResult(data);
        }


    }

}
