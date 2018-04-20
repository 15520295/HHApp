package com.example.huydaoduc.hieu.chi.hhapp.Manager.Place;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

public class PlaceAutocompleteAdapter extends RecyclerView.Adapter<PlaceAutocompleteAdapter.PlaceViewHolder> implements Filterable{

    public interface PlaceAutoCompleteInterface{
        void onPlaceClick(ArrayList<CusPlaceAutocomplete> mResultList, int position);
        void OnPlaceResultReturn();
    }

    Context mContext;
    PlaceAutoCompleteInterface mListener;
    private static final String TAG = "PlaceAutoCmpltAdapter";
    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);
    ArrayList<CusPlaceAutocomplete> mResultList;

    private GoogleApiClient mGoogleApiClient;

    private LatLngBounds mBounds;

    private int layout;

    private AutocompleteFilter mPlaceFilter;

    RecyclerView recyclerView ;

    public PlaceAutocompleteAdapter(Context context, int resource, GoogleApiClient googleApiClient,
                                    LatLngBounds bounds, AutocompleteFilter filter, RecyclerView recyclerView){
        this.mContext = context;
        layout = resource;
        mGoogleApiClient = googleApiClient;
        mBounds = bounds;
        mPlaceFilter = filter;
        this.mListener = (PlaceAutoCompleteInterface)mContext;
        this.recyclerView = recyclerView;
    }

    /*
    Clear List items
     */
    public void clearList(){
        if(mResultList!=null && mResultList.size()>0){
            mResultList.clear();
        }
    }


    /**
     * Sets the bounds for all subsequent queries.
     */
    public void setBounds(LatLngBounds bounds) {
        mBounds = bounds;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    mResultList = getAutocomplete(constraint);
                    if (mResultList != null) {
                        // The API successfully returned results.
                        results.values = mResultList;
                        results.count = mResultList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {

                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
//                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    /**
     * Main Algorithm
     */
    @SuppressLint("RestrictedApi")
    private ArrayList<CusPlaceAutocomplete> getAutocomplete(CharSequence constraint) {
        if (mGoogleApiClient.isConnected()) {
            Log.i(TAG, "Starting autocomplete query for: " + constraint);

            //region Old code -- use GeoDataApi
            //            // Submit the query to the autocomplete API and retrieve a PendingResult that will
//            // contain the results when the query completes.
//            PendingResult<AutocompletePredictionBuffer> results =
//                    Places.GeoDataApi
//                            .getAutocompletePredictions(mGoogleApiClient,
//                                    constraint.toString(),
//                                    mBounds, mPlaceFilter);
//
//            // This method should have been called off the main UI thread. Block and wait for at most 60s
//            // for a result from the API.
//            AutocompletePredictionBuffer autocompletePredictions = results
//                    .await(20, TimeUnit.SECONDS);
//
//            // Confirm that the query completed successfully, otherwise return null
//            final Status status = autocompletePredictions.getStatus();
//            if (!status.isSuccess()) {
//                Toast.makeText(mContext, "Error contacting API: " + status.toString(),
//                        Toast.LENGTH_SHORT).show();
//                Log.e(TAG, "Error getting autocomplete prediction API call: " + status.toString());
//                autocompletePredictions.release();
//                return null;
//            }
//
//            Log.i(TAG, "Query completed. Received " + autocompletePredictions.getCount()
//                    + " predictions.");
//
//            // Copy the results into our own data structure, because we can't hold onto the buffer.
//            // AutocompletePrediction objects encapsulate the API response (place ID and description).
//
//            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
//            int count = autocompletePredictions.getCount();
//            ArrayList resultList = new ArrayList<>(count);
//            while (iterator.hasNext()) {
//                AutocompletePrediction prediction = iterator.next();
//                // Get the details of this prediction and copy it into a new CusPlaceAutocomplete object.
//                resultList.add(new CusPlaceAutocomplete(prediction.getPlaceId(),
//                        prediction.getFullText(null)));
//            }
//
//            // Release the buffer now that all data has been copied.
//            autocompletePredictions.release();
//
//            return resultList;
            //endregion

            Task<AutocompletePredictionBufferResponse> results =
                    Places.getGeoDataClient(mContext)
                            .getAutocompletePredictions(constraint.toString(), mBounds,1,
                                    mPlaceFilter);

            try {
                Tasks.await(results, 60, TimeUnit.SECONDS);
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                e.printStackTrace();
            }

            try {
                AutocompletePredictionBufferResponse autocompletePredictions = results.getResult();

                ArrayList<AutocompletePrediction> AutocompletePredictionList = DataBufferUtils.freezeAndClose(autocompletePredictions);

                int count =  AutocompletePredictionList.size();
                Log.i(TAG, "Query completed. Received " +count
                        + " predictions.");
                Iterator<AutocompletePrediction> iterator = AutocompletePredictionList.iterator();
                ArrayList resultList = new ArrayList<>(count);
                while (iterator.hasNext()) {
                    AutocompletePrediction prediction = iterator.next();
                    // Get the details of this prediction and copy it into a new CusPlaceAutocomplete object.
                    resultList.add(new CusPlaceAutocomplete(prediction.getPlaceId(),
                            prediction.getPrimaryText(null),
                            prediction.getSecondaryText(null),
                            prediction.getFullText(null)));
                }

                // Release the buffer now that all data has been copied.
                autocompletePredictions.release();
                if(resultList != null)
                    if ( resultList.size() > 0)
                        mListener.OnPlaceResultReturn();
                return resultList;


            } catch (RuntimeExecutionException e) {
                // If the query did not complete successfully return null
                Toast.makeText(mContext, "Error contacting API: " + e.toString(),
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error getting autocomplete prediction API call", e);
                return null;
            }
        }
        Log.e("", "Google API client is not connected for autocomplete query.");
        return null;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = layoutInflater.inflate(layout, viewGroup, false);
        PlaceViewHolder mPredictionHolder = new PlaceViewHolder(convertView);
        return mPredictionHolder;
    }


    @Override
    public void onBindViewHolder(PlaceViewHolder mPredictionHolder, final int i) {
        if(mResultList != null)
            if (mPredictionHolder.getAdapterPosition() < mResultList.size()) {
                final int pos = mPredictionHolder.getAdapterPosition();
                mPredictionHolder.mPrimaryText.setText(mResultList.get(pos).primaryText);
                mPredictionHolder.mSecondaryText.setText(mResultList.get(pos).secondaryText.toString());

                mPredictionHolder.mParentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onPlaceClick(mResultList,pos);
                    }
                });
            }

    }

    @Override
    public int getItemCount() {
        if(mResultList != null)
            return mResultList.size();
        else
            return 0;
    }

    public CusPlaceAutocomplete getItem(int position) {
        return mResultList.get(position);
    }

    /*
    View Holder For Trip History
     */
    public class PlaceViewHolder extends RecyclerView.ViewHolder {
        //        CardView mCardView;
        public RelativeLayout mParentLayout;
        public TextView mPrimaryText;
        public TextView mSecondaryText;


        public PlaceViewHolder(View itemView) {
            super(itemView);
            mParentLayout = (RelativeLayout)itemView.findViewById(R.id.predictedRow);
            mPrimaryText = (TextView)itemView.findViewById(R.id.primary_text);
            mSecondaryText = (TextView)itemView.findViewById(R.id.secondary_text);

        }

    }

    /**
     * Holder for Places Geo Data Autocomplete API results.
     */
    public class CusPlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence primaryText;
        public CharSequence secondaryText;
        public CharSequence fullText;


        CusPlaceAutocomplete(CharSequence placeId, CharSequence primaryText, CharSequence secondaryText, CharSequence fullText) {
            this.placeId = placeId;
            this.primaryText = primaryText;
            this.secondaryText = secondaryText;
            this.fullText = fullText;

        }

        @Override
        public String toString() {
            return fullText.toString();
        }
    }
}
