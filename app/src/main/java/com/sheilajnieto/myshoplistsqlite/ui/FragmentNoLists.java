package com.sheilajnieto.myshoplistsqlite.ui;/*
@author sheila j. nieto 
@version 0.1 2024 -01 - 09
*/

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.sheilajnieto.myshoplistsqlite.R;

public class FragmentNoLists extends Fragment {

    private TextView tvMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_empty_shopping_list, container, false);

        // Inicializamos los TextViews
        tvMessage = view.findViewById(R.id.tvMessage);
        return view;
    }

    @Override
    public void onAttach(Context contex) {
        super.onAttach(contex);
        /** Esto hace que se pueda comunicar con cualquier activity */
        //FragmentEscribirCorreo.IOnAttachListener attachListener = (FragmentEmpresa.IOnAttachListener) contex;
        /** Obtenemos los datos*/
        //empresa = attachListener.getEmpresa();
        // clickListener = (IOnClickListener) contex;
    }
}
