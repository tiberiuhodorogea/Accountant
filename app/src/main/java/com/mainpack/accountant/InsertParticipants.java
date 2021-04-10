package com.mainpack.accountant;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class InsertParticipants extends ListActivity {

    TextView content;
    static ArrayList<Participant> g_participants;
    static double[][] g_matrix = null;
    ParticipantListAdapter adapter;
    static public String event_name = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        event_name = this.getIntent().getExtras().getString("event_name");

        if(null == g_matrix) {
            g_matrix = new double[31][];
            for(int i = 0;i<31;i++)
                g_matrix[i] = new double[31];

            for(int i =0;i<31;i++)
                for(int j=0;j<31;j++)
                    g_matrix[i][j] = 0;
        }
        g_participants = new ArrayList<Participant>();
        setContentView(R.layout.activity_insert_participants);


        //content = (TextView)findViewById(R.id.output);

        //listView = (ListView) findViewById(R.id.list);

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third - the Array of data

        adapter = new ParticipantListAdapter(this,
                R.layout.participant_list_item, g_participants);

        // Assign adapter to List
        setListAdapter(adapter);

    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);

        // ListView Clicked item index
        int itemPosition     = position;

        // ListView Clicked item value
        TextView  liPartName    =  (TextView)v.findViewById(R.id.listItemPartName);
        liPartName.setText("asafs");
        Snackbar.make(l, "12344",
                Snackbar.LENGTH_SHORT)
                .show();

    }

    public void hideKeyboard() {
        // Check if no view has focus:
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) (getSystemService(Context.INPUT_METHOD_SERVICE));
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void onAddParticipant(View view) {
        EditText etPart = (EditText) findViewById(R.id.etPartName);
        hideKeyboard();
        etPart.clearFocus();
        String partName = etPart.getText().toString();
        partName = partName.trim();

        if(partName.isEmpty())
        {
           Utils.ErrMsg(view,"Name required");
           etPart.setText("");
           return;
        }
        if(g_participants.size() == 30)
        {
            Utils.ErrMsg(view,"Participants limit reached");
            return;
        }

        Participant p = new Participant();
        p.name = partName;
        g_participants.add(p);

        Button btn_add_part = (Button)view;
        btn_add_part.setText("+(" + g_participants.size() + ")");

        adapter.notifyDataSetChanged();
        etPart.setText("");

        ArrayList<Boolean> all_toggled = new ArrayList<>();
        for(int i =0;i<g_participants.size();i++)
            all_toggled.add(true);

        InsertParticipants.g_matrix[g_participants.size()][g_participants.size()-1] = 0;
        Utils.computePayment_v2(InsertParticipants.g_matrix,g_participants.size(),g_participants.size()-1,all_toggled);
    }

}