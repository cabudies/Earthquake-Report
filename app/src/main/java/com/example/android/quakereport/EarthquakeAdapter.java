package com.example.android.quakereport;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.graphics.drawable.GradientDrawable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.R.attr.resource;
import static android.support.v4.content.ContextCompat.getColor;

/**
 * Created by gurjas on 07-06-2017.
 */

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

    public EarthquakeAdapter(Activity context, List<Earthquake> earthquakes) {
        super(context, 0, earthquakes);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View singleListItem = convertView;

        if (singleListItem == null)
        {
            singleListItem = LayoutInflater.from(getContext()).inflate(R.layout.earthquakeslisting, parent, false);
        }

        /*
         * here we will store single entry's data into the object named @singleEarthquakeInformation
         * we will get information using @getItem at specific position method.
          */

        Earthquake singleEarthquakeInformation = getItem(position);

        // set magnitude
        TextView earthquakeMagnitude = (TextView) singleListItem.findViewById(R.id.show_magnitude);
        earthquakeMagnitude.setText(""+singleEarthquakeInformation.getEarthquakeMagnitude());

        // we will use drawable resource to change the circle background color for magnitude
        GradientDrawable magnitudeCircle = (GradientDrawable) earthquakeMagnitude.getBackground();
        int magnitudeColor = getMagnitudeColor(singleEarthquakeInformation.getEarthquakeMagnitude());
        magnitudeCircle.setColor(magnitudeColor);


        try {
            // splitting the string into two parts
            String baseString = singleEarthquakeInformation.getEarthquakeOriginPlace();
            String[] dividedString = baseString.split(",");

            TextView earthquakeOriginPlace = (TextView) singleListItem.findViewById(R.id.show_place);
            earthquakeOriginPlace.setText(dividedString[0]);

            TextView earthquakeOriginNearBy = (TextView) singleListItem.findViewById(R.id.show_place_nearby);
            earthquakeOriginNearBy.setText(dividedString[1]);
        }
        catch (Exception e)
        {
            TextView earthquakeOriginPlace = (TextView) singleListItem.findViewById(R.id.show_place);
            earthquakeOriginPlace.setText("No Near By");

            TextView earthquakeOriginNearBy = (TextView) singleListItem.findViewById(R.id.show_place_nearby);
            earthquakeOriginNearBy.setText(singleEarthquakeInformation.getEarthquakeOriginPlace());
        }

        // set occurring date
        Date dateObject = new Date(singleEarthquakeInformation.getEarthquakeOccurringDate());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String dateToDisplay = dateFormat.format(dateObject);
        TextView earthquakeOccurringDate = (TextView) singleListItem.findViewById(R.id.show_date);
        earthquakeOccurringDate.setText(dateToDisplay);

        // set occurring time
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        String timeToDisplay = timeFormat.format(dateObject);
        TextView earthquakeOccurringTime = (TextView) singleListItem.findViewById(R.id.show_time);
        earthquakeOccurringTime.setText(timeToDisplay);

        return singleListItem;
    }

    private int getMagnitudeColor(double magnitudeReceived)
    {
        int magnitudeColorResouceID;
        int magnitudeFloor = (int) Math.floor(magnitudeReceived);
        switch (magnitudeFloor)
        {
            case 0:
            case 1: magnitudeColorResouceID = R.color.magnitude1;
                break;
            case 2: magnitudeColorResouceID = R.color.magnitude2;
                break;
            case 3: magnitudeColorResouceID = R.color.magnitude3;
                break;
            case 4: magnitudeColorResouceID = R.color.magnitude4;
                break;
            case 5: magnitudeColorResouceID = R.color.magnitude5;
                break;
            case 6: magnitudeColorResouceID = R.color.magnitude6;
                break;
            case 7: magnitudeColorResouceID = R.color.magnitude7;
                break;
            case 8: magnitudeColorResouceID = R.color.magnitude8;
                break;
            case 9: magnitudeColorResouceID = R.color.magnitude9;
                break;
            default: magnitudeColorResouceID = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResouceID);
    }
}
