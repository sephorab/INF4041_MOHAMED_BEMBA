package org.esiea.mohamed_bemba.myapp;

import android.app.Dialog;
import android.content.DialogInterface;

/**
 * Created by BEMBASéphora on 18/12/2016.
 */
public class OnClickListenerDialog implements DialogInterface.OnClickListener  {
    public static int selected = 0;
    @Override
    public void onClick(DialogInterface dialog, int which) {

        switch (which) {
            case Dialog.BUTTON_NEGATIVE: // Cancel button selected, do nothing
                dialog.cancel();
                break;

            case Dialog.BUTTON_POSITIVE: // OK button selected, send the data back
                dialog.dismiss();

                // message selected value to registered callbacks with the
                // selected value.
                //mDialogSelectorCallback.onSelectedOption(mSelectedIndex);
                break;

            default: // choice item selected
                // store the new selected value in the static variable
                selected = which;
                break;
        }
    }

    }

