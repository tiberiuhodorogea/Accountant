package com.mainpack.accountant;

import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class Utils {

    static public void ErrMsg(View view, String msg)
    {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
    }

    static public void printMat(double[][] data, int size) {
        for (int i = 0; i < size; i++) {
            String row = "";
            for (int j = 0; j < size; j++)
                row += data[i][j] + " ";
            Log.d("[TH_TAG]", row);
        }
    }


    static double suml(double[] pays, int debtors, int missed) {
        double total = 0;
        for (int i = 0; i < debtors + 1; i++) {
            if (missed != i)
                total += pays[i];
        }
        return total;
    }

    static public int totalToggled(ArrayList<Boolean> toggled){
        int ret = 0;
        for (Boolean b : toggled)
        {
            if(b == true)
                ret++;
        }

        return ret;
    }

    static public int computePayment_v2(double[][] data, int persCount, int currPerson, ArrayList<Boolean> toggled) {
        if (data == null || persCount < 3)
            return -1;

        //debt per person x person
        int size = persCount + 1;
            double currMean = data[size - 1][currPerson] / totalToggled(toggled);
            for (int k = 0; k < persCount; k++) {
                if (toggled.get(k) == true) {
                    data[k][currPerson] = currMean;
                }
        }

        //subtract mirroring debts
        for (int i = 0; i < persCount; i++) {
            for (int j = 0; j < persCount; j++)
                if (j != i) {
                    if (data[i][j] > data[j][i]) {
                        data[i][j] -= data[j][i];
                        data[j][i] = 0;
                    } else {
                        data[j][i] -= data[i][j];
                        data[i][j] = 0;
                    }
                }
        }

        //smart transfer debt
        for (int i = 0; i < persCount; i++) {
            for (int j = 0; j < persCount; j++)
                if (j != i) {
                    for (int k = 0; k < persCount; k++)
                        if (k != i && k != j) {
                            if (data[i][j] > data[j][k]) {
                                data[i][k] += data[j][k];
                                data[i][j] -= data[j][k];
                                data[j][k] = 0;

                            } else {
                                data[i][k] += data[i][j];
                                data[j][k] -= data[i][j];
                                data[i][j] = 0;
                            }

                        }
                }
        }

        //total to give per person
        for (int i = 0; i < persCount; i++) {
            data[i][size - 1] = suml(data[i], persCount - 1, i);
        }

        //global total spent
        data[size - 1][size - 1] += suml(data[size - 1], size - 1, size - 1);
        data[size - 1][currPerson] = 0;

        return 0;
    }



/*
    static public int computePayment(double[][] data, int persCount) {
        if (data == null || persCount < 3)
            return -1;

        int size = persCount + 1;
        for (int i = 0; i < persCount; i++) {
            double currMean = data[size - 1][i] / persCount;
            for (int k = 0; k < persCount; k++) {
                if (k != i) {
                    data[k][i] = currMean;
                }
            }
        }

        for (int i = 0; i < persCount; i++) {
            for (int j = 0; j < persCount; j++)
                if (j != i) {
                    if (data[i][j] > data[j][i]) {
                        data[i][j] -= data[j][i];
                        data[j][i] = 0;
                    } else {
                        data[j][i] -= data[i][j];
                        data[i][j] = 0;
                    }
                }
        }

        for (int i = 0; i < persCount; i++) {
            for (int j = 0; j < persCount; j++)
                if (j != i) {
                    for (int k = 0; k < persCount; k++)
                        if (k != i && k != j) {
                            if (data[i][j] > data[j][k]) {
                                data[i][k] += data[j][k];
                                data[i][j] -= data[j][k];
                                data[j][k] = 0;

                            } else {
                                data[i][k] += data[i][j];
                                data[j][k] -= data[i][j];
                                data[i][j] = 0;
                            }

                        }
                }
        }

        for (int i = 0; i < persCount; i++) {
            data[i][size - 1] = suml(data[i], persCount - 1, i);
        }

        data[size - 1][size - 1] = suml(data[size - 1], size - 1, size - 1);

        for (int i = 0; i < persCount; i++) {
            for (int k = 0; k < persCount; k++)
                if (i == k)
                    data[i][k] += data[size - 1][size - 1] / persCount;
        }

        for (Participant p :
                InsertParticipants.g_participants)
            p.checked = true;

        return 0;
    }
    */

}


