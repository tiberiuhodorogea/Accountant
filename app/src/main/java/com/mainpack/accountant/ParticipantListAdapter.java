package com.mainpack.accountant;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.provider.Telephony;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class ParticipantListAdapter extends ArrayAdapter {

    Context context;
    protected ArrayList<Participant> participants;
    PopupWindow popUp;
    boolean click = true;

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public ParticipantListAdapter(Context context, int resource , ArrayList<Participant> parts) {
        super(context, resource, parts);
        this.participants = parts;
        this.context = context;
    }

    public void hideKeyboard() {
        // Check if no view has focus:
        View view = ((InsertParticipants)context).getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) ((InsertParticipants)context).getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Participant participant = participants.get(position);
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.participant_list_item, null);
        ((TextView)(convertView.findViewById(R.id.listItemPartName))).setText(participant.name);


        EditText etAmountSpend = (EditText) convertView.findViewById(R.id.listItemAmountSpent);
        etAmountSpend.setHint("so far " + String.format("%,d",(int) InsertParticipants.g_participants.get(position).spent).replace(","," ") );


        ((Button)(convertView.findViewById(R.id.button_add_spent))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                EditText etAmountSpend = (EditText) ( (View)(v.getParent())).findViewById(R.id.listItemAmountSpent);
                etAmountSpend.clearFocus();
                if( etAmountSpend.getText().toString().equals("")
                        || etAmountSpend.getText().toString().isEmpty() ||
                        0 == Double.parseDouble(etAmountSpend.getText().toString())){
                    Snackbar snackbar = Snackbar
                            .make(parent, "No value", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }

                if(participants.size() < 3)
                {
                    Snackbar snackbar = Snackbar
                            .make(parent, "Must be at least 3 participants", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }

                Double amountSpent = Double.parseDouble(etAmountSpend.getText().toString());
                participants.get(position).spent += amountSpent;

                final ArrayList<String> participants_names = new ArrayList<String>();
                boolean all_checked[] = new boolean[31];
                final ArrayList selectedItems = new ArrayList();

                for (int i = 0;i<participants.size();i++){
                    all_checked[i] = true;
                    participants_names.add(i,participants.get(i).name);
                    selectedItems.add(i);
                }


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Included in payment");
                builder.setMultiChoiceItems(participants_names.toArray(new CharSequence[participants_names.size()]),  all_checked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        //Here you add or remove the items from the list selectedItems. That list will be the result of the user selection.
                        if (isChecked) {
                            selectedItems.add(which);
                        } else if (selectedItems.contains(which)) {
                            selectedItems.remove(Integer.valueOf(which));
                        }
                    }
                });

                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ArrayList<Boolean> checked_vect = new ArrayList<Boolean>();
                        for(int i=0; i < participants.size();i++)
                        {
                            checked_vect.add(i,selectedItems.contains(i) ? true : false);
                        }

                        etAmountSpend.setText("");
                        etAmountSpend.setHint("so far " + String.format("%,d",(int) InsertParticipants.g_participants.get(position).spent).replace(","," ") );

                        InsertParticipants.g_matrix[participants.size()][position] = amountSpent;
                        Utils.computePayment_v2(InsertParticipants.g_matrix,InsertParticipants.g_participants.size(),position,checked_vect);
                        Utils.printMat(InsertParticipants.g_matrix,InsertParticipants.g_participants.size()+1);
                        Log.d("Separator","-----------");
                    }
                });


                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //Do something else if you want
                    }
                });

                builder.create();
                builder.show();

            }
        });

        ((Button)(convertView.findViewById(R.id.btn_info))).setOnClickListener(new View.OnClickListener() {


            private String getFullReport()
            {
                String ret = "";
                String give = "";

                //give transactions
                int total = 0;
                for(int j = 0; j<participants.size();j++){
                    for(int i = 0; i < participants.size();++i)
                    {
                        double val = InsertParticipants.g_matrix[i][j];
                        if( val > 1 && j != i)
                        {
                            //tranzactie de dat
                            give += "   " + participants.get(i).name + " " + String.format("%,d",(int)val).replace(",", " ") + " ---> " + InsertParticipants.g_participants.get(j).name + "\n";
                            total += val;
                        }
                    }

                }

                ret = give + "\n" + "\n" +
                        "Total spent @" + InsertParticipants.event_name + " " + String.format("%,d",(int)InsertParticipants.g_matrix[InsertParticipants.g_participants
                        .size()][InsertParticipants.g_participants.size()]).replace(",", " ")
                        + ".";

                return ret;
            }

            private String getIndividualReport(int which)
            {
                String ret = "";
                String give = "", take = "";
                double totalSpent = participants.get(which).spent;

                //give transactiions
                int total = 0;
                for(int j = 0; j<participants.size();j++){
                    double val = InsertParticipants.g_matrix[which][j];
                    if(j != which && val > 1)
                    {
                        //tranzactie de dat
                        give += "   " + String.format("%,d",(int)val).replace(","," ") + " ---> " + InsertParticipants.g_participants.get(j).name + "\n";
                        total += val;
                        totalSpent+= val;
                    }
                }
                if(total != 0)
                    give = "you need to pay " + String.format("%,d",(int)total).replace(","," ")  + "\n" + "\n" + give + "\n";
                total = 0;

                //take tansactions
                for(int i = 0; i < participants.size();i++){
                    double val = InsertParticipants.g_matrix[i][which];
                    if(i != which && val > 1)
                    {
                        //tranzactie de dat
                        take += "   " + String.format("%,d",(int)val).replace(","," ")  + " <--- " + InsertParticipants.g_participants.get(i).name + "\n";
                        total += val;
                        totalSpent -= val;
                    }
                }

                if(total != 0)
                    take= "you need to receive " + String.format("%,d",(int)total).replace(","," ")  + "\n" + "\n" + take+ "\n";


                return give + (give.isEmpty() ? "" : "\n\n") + take + "\n\n" + "Total spent "
                        + String.format("%,d",(int)totalSpent).replace(","," ") + ".";
            }

            boolean full = false;
            @Override
            public void onClick(View v) {
                hideKeyboard();
                etAmountSpend.clearFocus();

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                String titleline = InsertParticipants.event_name == null ? "" : "@" + InsertParticipants.event_name + " ";
                String individual_report_title = titleline + "transactions for " + InsertParticipants.g_participants.get(position).name+":";
                String individual_report = getIndividualReport(position);

                String full_report_title = "@" + InsertParticipants.event_name + " full:";
                String full_report =  getFullReport();

                full = false;
                builder.setCancelable(true)
                        .setPositiveButton("shar3", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, full ? full_report_title + "\n\n" + full_report
                                        :individual_report_title+ "\n" + individual_report);
                                sendIntent.setType("text/plain");

                                Intent shareIntent = Intent.createChooser(sendIntent, null);
                                context.startActivity(shareIntent);
                            }
                        })
                        .setNeutralButton("full r3port", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //share code
                                Intent sendIntent = new Intent();
                                full = true;
                                builder.setMessage(full_report);
                                builder.setTitle(full_report_title);
                                builder.setNeutralButton("",null);
                                builder.show();
                            }
                        })
                        .setMessage(individual_report)
                        .setTitle(individual_report_title);

                builder.show();

            }
        });


        return convertView;
    }

}

