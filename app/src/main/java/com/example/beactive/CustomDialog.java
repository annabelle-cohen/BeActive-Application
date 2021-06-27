package com.example.beactive;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import org.jetbrains.annotations.NotNull;


public class CustomDialog extends AppCompatDialogFragment {

    public interface CustomDialogListener {
        void applyTexts(String choice);
        void cancelApply();
    }

    private CustomDialogListener listener;
    private Button btnFemale,btnMale;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_custom_dialog, null);

        btnFemale = view.findViewById(R.id.btn_female);
        btnMale = view.findViewById(R.id.btn_male);

        builder.setView(view)
                .setTitle(getString(R.string.dialogTitle)).
                setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.cancelApply();
            }
        });


        btnFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.applyTexts(getString(R.string.genderFemale));
            }
        });

        btnMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.applyTexts(getString(R.string.genderMale));
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        try {
            listener = (CustomDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    getString(R.string.listener));
        }
    }
}