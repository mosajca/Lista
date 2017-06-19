package com.example.marcin.lista;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

public class InputFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle("Dodaj element")
                .setView(getActivity().getLayoutInflater().inflate(R.layout.add, null))
                .setPositiveButton("Dodaj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        ((MainActivity) getActivity()).add(((EditText) getDialog().findViewById(R.id.addEditText1)).getText().toString(),
                                ((EditText) getDialog().findViewById(R.id.addEditText2)).getText().toString());
                        dismiss();
                    }
                })
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dismiss();
                    }
                }).create();
    }
}
