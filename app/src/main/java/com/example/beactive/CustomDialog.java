package com.example.beactive;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;


public class CustomDialog extends AppCompatDialogFragment {

    public interface CustomDialogListener {
        void applyTexts(String choice);
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
                .setTitle("Gender");

        btnFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.applyTexts("Female");
            }
        });

        btnMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.applyTexts("Male");
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
                    "must implement CustomDialogListener");
        }
    }
}