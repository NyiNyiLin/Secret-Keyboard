package com.nyi.secret.keyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.inputmethod.InputConnection;
import android.widget.Toast;

import com.nyi.secret.database.DatabaseHandler;

/**
 * Created by IN-3442 on 09-Oct-15.
 */
public class CustomKeyboardView extends KeyboardView {
    boolean activate = true;
    DatabaseHandler db=new DatabaseHandler(getContext());

    static final int KEYCODE_OPTIONS = -100;
    // TODO: Move this into android.inputmethodservice.Keyboard
    static final int KEYCODE_LANGUAGE_SWITCH = -101;


    public CustomKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean onLongPress(Keyboard.Key key) {


        if (key.codes[0] == Keyboard.KEYCODE_CANCEL) {
            getOnKeyboardActionListener().onKey(KEYCODE_OPTIONS, null);
            return true;
        }else if(key.codes[0]==32){
            getOnKeyboardActionListener().onKey(-32, null);
            return true;
        }
        else if(key.codes[0]>=97 && key.codes[0]<=122){
            getOnKeyboardActionListener().onKey(db.getCustomCodebyKeyCode(key.codes[0]), null);
            return true;
        } 
        else {
            return super.onLongPress(key);
        }
    }
}
