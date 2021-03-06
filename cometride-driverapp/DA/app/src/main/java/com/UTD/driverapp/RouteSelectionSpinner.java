package com.UTD.driverapp;



        import java.util.List;

        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.DialogInterface.OnCancelListener;
        import android.content.DialogInterface.OnMultiChoiceClickListener;
        import android.util.AttributeSet;
        import android.util.Log;
        import android.widget.ArrayAdapter;
        import android.widget.Spinner;
        import android.widget.Toast;

public class RouteSelectionSpinner extends Spinner implements
        OnMultiChoiceClickListener, OnCancelListener {

    private List<String> items;
    private boolean[] selected;
    private String defaultText;
    private MultiSpinnerListener listener;

    public RouteSelectionSpinner(Context context) {
        super(context);
        // this.init();

    }

    public RouteSelectionSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        // this.init();
    }

    public void setListener(MultiSpinnerListener listener) {
        this.listener = listener;
    }

    public interface MultiSpinnerListener {
        // public void onItemsSelected(boolean[] selected);
        public void displaySelectedRoute(int id);

        public void setRouteVisibleFalse();
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (isChecked)
            selected[which] = true;
        else
            selected[which] = false;
    }

    @Override
    public boolean performClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(
                items.toArray(new CharSequence[items.size()]), selected, this);
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.setOnCancelListener(this);
        builder.show();
        return true;
    }

    public void setItems(List<String> items, String allText,
                         MultiSpinnerListener listener) {
        this.items = items;
        this.defaultText = allText;
        this.listener = listener;
        Log.v("Listener1", listener.toString());
        // all selected by default
        selected = new boolean[items.size()];
        for (int i = 0; i < selected.length; i++)
            selected[i] = true;

        // all text on the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, new String[] { allText });
        setAdapter(adapter);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        udpateRoutes();
    }

    public void udpateRoutes() {

        if (listener != null) {
            Log.v("Listener2", listener.toString());
            // Log.v("Listener", listener.toString());
            StringBuffer spinnerBuffer = new StringBuffer();
            boolean someUnselected = false;
            for (int i = 0; i < items.size(); i++) {
                if (selected[i] == true) {
                    spinnerBuffer.append(items.get(i));
                    spinnerBuffer.append(", ");
                } else {
                    someUnselected = true;
                }
            }

            String spinnerText;
            if (someUnselected) {
                spinnerText = spinnerBuffer.toString();
                if (spinnerText.length() > 2)
                    spinnerText = spinnerText.substring(0,
                            spinnerText.length() - 2);
            } else {
                spinnerText = defaultText;
            }
            if (spinnerText == "") {

                spinnerText = "Select Routes Here";
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    getContext(), android.R.layout.simple_spinner_item,
                    new String[] { spinnerText });
            setAdapter(adapter);

            // this.setItems
            listener.setRouteVisibleFalse();
            for (int i = 0; i < items.size(); i++) {
                if (selected[i] == true) {

                    Toast.makeText(getContext(), items.get(i),
                            Toast.LENGTH_SHORT).show();
                    listener.displaySelectedRoute(i);

                }

            }
        }
    }

}