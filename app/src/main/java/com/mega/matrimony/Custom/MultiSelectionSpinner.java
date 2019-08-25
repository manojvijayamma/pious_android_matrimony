package com.mega.matrimony.Custom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.support.v7.widget.AppCompatSpinner;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MultiSelectionSpinner extends AppCompatSpinner implements
        OnMultiChoiceClickListener {

    public interface OnMultipleItemsSelectedListener{
        void selectedIndices(List<Integer> indices);
        void selectedStrings(List<String> strings);
    }
    private OnMultipleItemsSelectedListener listener;

    String popup_title="Select Option";
    String[] _items = null;
    Integer[] _ids = null;
    String[] _ids_string = null;
    boolean[] mSelection = null;
    boolean[] mSelectionAtStart = null;
    String _itemsAtStart = null;

    ArrayAdapter<String> simple_adapter;

    public MultiSelectionSpinner(Context context) {
        super(context);

        simple_adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item);
        super.setAdapter(simple_adapter);
    }

    public MultiSelectionSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        simple_adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item);
        super.setAdapter(simple_adapter);
    }

    public void setListener(OnMultipleItemsSelectedListener listener){
        this.listener = listener;
    }

    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        Log.d("changlst","Position: "+which);
         if (which==0){
            for (int i = 0; i < mSelection.length; i++) {
                mSelection[i] = false;
                mSelectionAtStart[i] = false;
            }
          //  Log.d("changlst","Position: "+mSelection.length);
            simple_adapter.notifyDataSetChanged();
        }else {
            if (mSelection != null && which < mSelection.length) {
                for (int i=0;i<_items.length;i++){
                    if (_items[i].equals("Doesn't Matter")){
                        if (mSelection[i]){
                            mSelection[i]=false;
                        }
                    }else {
                        mSelection[which] = isChecked;
                        simple_adapter.clear();
                        simple_adapter.add(buildSelectedItemString());
                    }
                }
                simple_adapter.notifyDataSetChanged();
               // Log.d("changlst","Position: "+mSelection.length);
            }
        }

         /* if (mSelection != null && which < mSelection.length) {
                mSelection[which] = isChecked;
                simple_adapter.clear();
                simple_adapter.add(buildSelectedItemString());
            } else {
                throw new IllegalArgumentException(
                        "Argument 'which' is out of bounds.");
            }*/
    }

    @Override
    public boolean performClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(popup_title);
        builder.setMultiChoiceItems(_items, mSelection,new OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
                final AlertDialog alert = (AlertDialog)dialogInterface;
                final ListView list = alert.getListView();
                Log.d("changlst","Position: "+mSelection.length);
                if (which==0){
                    mSelection[which] = isChecked;
                    if (isChecked){
                        int i = 0;
                        while(i < _items.length) {
                            list.setItemChecked(i, false);
                            mSelection[i] = false;
                            mSelection[0] = true;
                            i++;
                        }
                    }
                    list.setItemChecked(which, isChecked);
                    simple_adapter.clear();
                    simple_adapter.add(buildSelectedItemString());

                }else {
                    if (mSelection != null && which < mSelection.length) {
                        mSelection[which] = isChecked;
                        if (isChecked){
                            list.setItemChecked(0, false);
                            mSelection[0] = false;
                        }

                        list.setItemChecked(which, isChecked);
                        simple_adapter.clear();
                        simple_adapter.add(buildSelectedItemString());
                        // Log.d("changlst","Position: "+mSelection.length);
                    }
                }
                Log.d("changlst","Position: "+mSelection.length);
                simple_adapter.notifyDataSetChanged();
            }
        });
        _itemsAtStart = getSelectedItemsAsString();
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.arraycopy(mSelection, 0, mSelectionAtStart, 0, mSelection.length);
               // listener.selectedIndices(getSelectedIndices());
                listener.selectedStrings(getSelectedStrings());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                simple_adapter.clear();
                simple_adapter.add(_itemsAtStart);
                System.arraycopy(mSelectionAtStart, 0, mSelection, 0, mSelectionAtStart.length);
            }
        });
        builder.show();
        return true;
        /* new OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
                final AlertDialog alert = (AlertDialog)dialogInterface;
                final ListView list = alert.getListView();
                //Log.d("changlst","Position: "+mSelection.length);
                if (which==0){
                    mSelection[which] = isChecked;
                    if (isChecked){
                        int i = 0;
                        while(i < _items.length) {
                            list.setItemChecked(i, false);
                            mSelection[i] = false;
                            mSelection[0] = true;
                            i++;
                        }
                    }
                    list.setItemChecked(which, isChecked);
                    simple_adapter.clear();
                    simple_adapter.add(buildSelectedItemString());

                }else {
                    if (mSelection != null && which < mSelection.length) {
                        mSelection[which] = isChecked;
                        if (isChecked){
                            list.setItemChecked(0, false);
                            mSelection[0] = false;
                        }

                        list.setItemChecked(which, isChecked);
                        simple_adapter.clear();
                        simple_adapter.add(buildSelectedItemString());
                        // Log.d("changlst","Position: "+mSelection.length);
                    }
                }
                simple_adapter.notifyDataSetChanged();
            }
        }*/
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException(
                "setAdapter is not supported by MultiSelectSpinner.");
    }

    public void setItems(String[] items) {
        _items = items;
        mSelection = new boolean[_items.length];
        mSelectionAtStart = new boolean[_items.length];
        simple_adapter.clear();
        simple_adapter.add(_items[0]);
        Arrays.fill(mSelection, false);
        mSelection[0] = true;
        mSelectionAtStart[0] = true;
    }

    public void setItems(List<String> items,List<Integer> ids,String title) {
        popup_title=title;
        _items = items.toArray(new String[items.size()]);
        _ids=ids.toArray(new Integer[items.size()]);
        mSelection = new boolean[_items.length];
        mSelectionAtStart  = new boolean[_items.length];
        simple_adapter.clear();
        simple_adapter.add(_items[0]);
        Arrays.fill(mSelection, false);
        mSelection[0] = true;
    }

    public void setItems_string_id(List<String> items,List<String> ids,String title) {
        popup_title=title;
        _items = items.toArray(new String[items.size()]);
        _ids_string=ids.toArray(new String[items.size()]);
        mSelection = new boolean[_items.length];
        mSelectionAtStart  = new boolean[_items.length];
        simple_adapter.clear();
        simple_adapter.add(_items[0]);
        Arrays.fill(mSelection, false);
        mSelection[0] = true;
    }

    public void setSelection(String[] selection) {

        //Log.d("selmul",selection[0]+" ");
        if (selection[0].equals("null") || selection[0].equals("")){
            setSelection(0);
        }else {
            for (int i = 0; i < mSelection.length; i++) {
                mSelection[i] = false;
                mSelectionAtStart[i] = false;
            }
            for (String cell : selection) {

                for (int j = 0; j < _ids_string.length; ++j) {//_items
                    if (_ids_string[j].equals(cell)) {//_items
                        mSelection[j] = true;
                        mSelectionAtStart[j] = true;
                    }
                }
            }
            simple_adapter.clear();
            simple_adapter.add(buildSelectedItemString());
        }

    }

    public void setSelection(List<String> selection) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
            mSelectionAtStart[i] = false;
        }
        for (String sel : selection) {
            for (int j = 0; j < _items.length; ++j) {
                if (_items[j].equals(sel)) {
                    mSelection[j] = true;
                    mSelectionAtStart[j] = true;
                }
            }
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public void setSelection(int index) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
            mSelectionAtStart[i] = false;
        }
        if (index >= 0 && index < mSelection.length) {
            mSelection[index] = true;
            mSelectionAtStart[index] = true;
        } else {
            throw new IllegalArgumentException("Index " + index
                    + " is out of bounds.");
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public void setSelection(int[] selectedIndices) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
            mSelectionAtStart[i] = false;
        }
        for (int index : selectedIndices) {
            if (index >= 0 && index < mSelection.length) {
                mSelection[index] = true;
                mSelectionAtStart[index] = true;
            } else {
                throw new IllegalArgumentException("Index " + index
                        + " is out of bounds.");
            }
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public List<String> getSelectedStrings() {

        List<String> selection = new LinkedList<>();
        for (int i = 0; i < _ids_string.length; ++i) {
            if (mSelection[i]) {
                selection.add(_ids_string[i]);
            }
        }
        if (selection.size()==0){
            selection.add("0");
            setSelection(0);
        }
        //Log.d("changlst",selection.size()+"");
        return selection;
    }

    public List<Integer> getSelectedIndices() {
        List<Integer> selection = new LinkedList<>();
        for (int i = 0; i < _ids.length; ++i) {
            if (mSelection[i]) {
                selection.add(_ids[i]);
            }
        }
        return selection;
    }

    public List<String> getSelectedIdString() {
        List<String> selection = new LinkedList<>();
        for (int i = 0; i < _ids_string.length; ++i) {
            if (mSelection[i]) {
                selection.add(_ids_string[i]);
            }
        }
        return selection;
    }

    private String buildSelectedItemString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;

                sb.append(_items[i]);
            }
        }
        //Log.d("changlst","Position: "+sb);
        return sb.toString();
    }

    public String getSelectedItemsAsString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;
                sb.append(_items[i]);
            }
        }
        return sb.toString();
    }

}