package es.uv.jaimediazgonzalez.facilquedar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;

/**
 * Inspired by: http://stackoverflow.com/a/6022474/1521064
 */
public class MultiSpinner extends android.support.v7.widget.AppCompatSpinner {

    private CharSequence[] entries;
    private boolean[] selected;
    private MultiSpinnerListener listener;

    public MultiSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MultiSpinner);
        entries = a.getTextArray(R.styleable.MultiSpinner_android_entries);
        if (entries != null) {
            selected = new boolean[entries.length]; // false-filled by default
        }
        a.recycle();
    }

    private OnMultiChoiceClickListener mOnMultiChoiceClickListener = new OnMultiChoiceClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            selected[which] = isChecked;
        }
    };

    private DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // build new spinner text & delimiter management
            StringBuffer spinnerBuffer = new StringBuffer();
            for (int i = 0; i < entries.length; i++) {
                if (selected[i]) {
                    spinnerBuffer.append(entries[i]);
                    spinnerBuffer.append(", ");
                }
            }

            // Remove trailing comma
            if (spinnerBuffer.length() > 2) {
                spinnerBuffer.setLength(spinnerBuffer.length() - 2);
            }

            // display new text
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_spinner_item,
                    new String[] { spinnerBuffer.toString() });
            setAdapter(adapter);

            if (listener != null) {
                listener.onItemsSelected(selected);
            }

            // hide dialog
            dialog.dismiss();
        }
    };

    @Override
    public boolean performClick() {
        new AlertDialog.Builder(getContext())
                .setMultiChoiceItems(entries, selected, mOnMultiChoiceClickListener)
                .setPositiveButton(android.R.string.ok, mOnClickListener)
                .show();
        return true;
    }

    public void setMultiSpinnerListener(MultiSpinnerListener listener) {
        this.listener = listener;
    }

    public interface MultiSpinnerListener {
        public void onItemsSelected(boolean[] selected);
    }
}