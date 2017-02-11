package rs.fon.places;
/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import rs.fon.places.common.activities.SampleActivityBase;
import rs.fon.places.place.PlaceFragment;
import rs.fon.places.place.State;
import rs.fon.places.place.CardStream;
import rs.fon.places.place.OnClickListener;
import rs.fon.places.place.StreamRetentionFragment;

public class MainActivity extends SampleActivityBase implements CardStream {
    public static final String TAG = "MainActivity";
    public static final String FRAGTAG = "PlacePickerFragment";

    private PlaceFragment mCardStreamFragment;

    private StreamRetentionFragment mRetentionFragment;
    private static final String RETENTION_TAG = "retention";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        PlacePickerFragment fragment =
                (PlacePickerFragment) fm.findFragmentByTag(FRAGTAG);

        if (fragment == null) {
            FragmentTransaction transaction = fm.beginTransaction();
            fragment = new PlacePickerFragment();
            transaction.add(fragment, FRAGTAG);
            transaction.commit();
        }

        // Use fragment as click listener for cards, but must implement correct interface
        if (!(fragment instanceof OnClickListener)){
            throw new ClassCastException("PlacePickerFragment must " +
                    "implement OnCardClickListener interface.");
        }
        OnClickListener clickListener = (OnClickListener) fm.findFragmentByTag(FRAGTAG);

        mRetentionFragment = (StreamRetentionFragment) fm.findFragmentByTag(RETENTION_TAG);
        if (mRetentionFragment == null) {
            mRetentionFragment = new StreamRetentionFragment();
            fm.beginTransaction().add(mRetentionFragment, RETENTION_TAG).commit();
        } else {
            // If the retention fragment already existed, we need to pull some state.
            // pull state out
            State state = mRetentionFragment.getCardStream();

            // dump it in CardStreamFragment.
            mCardStreamFragment =
                    (PlaceFragment) fm.findFragmentById(R.id.fragment_cardstream);
            mCardStreamFragment.restoreState(state, clickListener);
        }
    }

    public PlaceFragment getCardStream() {
        if (mCardStreamFragment == null) {
            mCardStreamFragment = (PlaceFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_cardstream);
        }
        return mCardStreamFragment;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        State state = getCardStream().dumpState();
        mRetentionFragment.storeCardStream(state);
    }
}

