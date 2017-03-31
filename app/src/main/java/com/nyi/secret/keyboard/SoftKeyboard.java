package com.nyi.secret.keyboard;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.nyi.secret.database.CustomKey;
import com.nyi.secret.database.DatabaseHandler;
import com.nyi.secret.secretkeyboard.CardViewAdapter;
import com.nyi.secret.secretkeyboard.R;

import java.util.ArrayList;

import static android.text.InputType.*;

/**
 * Created by IN-3442 on 09-Oct-15.
 */
public class SoftKeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener{
    private KeyboardView kv;
    private CandidateView mCandidateView;

    private NyiKeyBoard mSymbolsKeyboard;
    private NyiKeyBoard mSymbolsShiftedKeyboard;
    private NyiKeyBoard mQwertyKeyboard;
    private NyiKeyBoard mEncryptKeyboard;
    private NyiKeyBoard mQwerty_Myanmar_Keyboard;
    private NyiKeyBoard mQwerty_Myanmar_Shift_Keyboard;
    private NyiKeyBoard mMyanmar_Symbol_Keyboard;

    private int mLastDisplayWidth;

    private NyiKeyBoard mCurKeyboard;
    private StringBuilder mComposing = new StringBuilder();

    private boolean caps = false;
    private boolean encrypt=false;
    int paste=1;

    private DatabaseHandler db;
    private InputMethodManager imeManager;
    private EditorInfo editorInfo;

    private int shift_count =1; //For shift key

    @Override
    public void onFinishInput() {
        super.onFinishInput();

        mComposing.setLength(0);
        //updateCandicate();
        caps=false;
        encrypt=false;

        //setKeyboard(mQwertyKeyboard);
        mCurKeyboard.setShifted(false);
        //kv.invalidateAllKeys();
        if (kv != null) {
            kv.closing();
        }
        Log.i("Close","Closing");
    }

    /**
     * Main initialization of the input method component.  Be sure to call
     * to super class.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        imeManager=(InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        //kv = (KeyboardView)getLayoutInflater().inflate(R.layout.input, null);
        //mInputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        //mWordSeparators = getResources().getString(R.string.word_separators);

    }

    @Override
    public View onCreateInputView() {
        kv = (KeyboardView)getLayoutInflater().inflate(R.layout.input, null);
        //setKeyboard(mQwertyKeyboard);
        kv.setOnKeyboardActionListener(this);
        return kv;
    }

    /**
     * This is the point where you can do all of your UI initialization.  It
     * is called after creation and any configuration change.
     */
    @Override public void onInitializeInterface() {
        if (mQwertyKeyboard != null) {
            // Configuration changes can happen after the keyboard gets recreated,
            // so we need to be able to re-build the keyboards if the available
            // space has changed.
            int displayWidth = getMaxWidth();
            if (displayWidth == mLastDisplayWidth) return;
            mLastDisplayWidth = displayWidth;
        }
        mQwertyKeyboard = new NyiKeyBoard(this, R.xml.qwerty);
        mSymbolsKeyboard = new NyiKeyBoard(this, R.xml.symbols);
        mSymbolsShiftedKeyboard = new NyiKeyBoard(this, R.xml.symbols_shift);
        mEncryptKeyboard=new NyiKeyBoard(this, R.xml.qwerty_encrypt);
        mQwerty_Myanmar_Keyboard=new NyiKeyBoard(this, R.xml.qwerty_myanmar);
        mQwerty_Myanmar_Shift_Keyboard=new NyiKeyBoard(this, R.xml.qwerty_myanmar_shift);
        mMyanmar_Symbol_Keyboard=new NyiKeyBoard(this, R.xml.myanmar_symbols);
    }


    /**
     * This is the main point where we do our initialization of the input method
     * to begin operating on an application.  At this point we have been
     * bound to the client, and are now receiving all of the detailed information
     * about the target of our edits.
     */
    @Override public void onStartInput(EditorInfo attribute, boolean restarting) {
        //kv = (KeyboardView)getLayoutInflater().inflate(R.layout.input, null);
        super.onStartInput(attribute, restarting);
        editorInfo=attribute;
        caps=false;
        encrypt=false;
        shift_count =1;

        if (!restarting) {
            // Clear shift states.
            //mMetaState = 0;
            caps=false;
            encrypt=false;
        }

        // We are now going to initialize our state based on the type of
        // text being edited.
        switch (attribute.inputType & TYPE_MASK_CLASS) {
            case TYPE_CLASS_NUMBER:
            case TYPE_CLASS_DATETIME:
                // Numbers and dates default to the symbols keyboard, with
                // no extra features.
                mCurKeyboard = mSymbolsKeyboard;
                break;

            case TYPE_CLASS_PHONE:
                // Phones will also default to the symbols keyboard, though
                // often you will want to have a dedicated phone keyboard.
                mCurKeyboard = mSymbolsKeyboard;
                break;

            case TYPE_CLASS_TEXT:
                // This is general text editing.  We will default to the
                // normal alphabetic keyboard, and assume that we should
                // be doing predictive text (showing candidates as the
                // user types).
                mCurKeyboard = mQwertyKeyboard;

                // We now look for a few special variations of text that will
                // modify our behavior.
                int variation = attribute.inputType & TYPE_MASK_VARIATION;
                if (variation == TYPE_TEXT_VARIATION_PASSWORD ||
                        variation == TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                }

                if (variation == TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        || variation == TYPE_TEXT_VARIATION_URI
                        || variation == TYPE_TEXT_VARIATION_FILTER) {
                }

                if ((attribute.inputType & TYPE_TEXT_FLAG_AUTO_COMPLETE) != 0) {
                    // If this is an auto-complete text view, then our predictions
                    // will not be shown and instead we will allow the editor
                    // to supply their own.  We only show the editor's
                    // candidates when in fullscreen mode, otherwise relying
                    // own it displaying its own UI.
                }

                // We also want to look at the current state of the editor
                // to decide whether our alphabetic keyboard should start out
                // shifted.
                //updateShiftKeyState(attribute);
                break;

            default:
                // For all unknown input types, default to the alphabetic
                // keyboard with no special features.
                //setKeyboard(mQwertyKeyboard);
                mCurKeyboard = mQwertyKeyboard;
                mCurKeyboard.setShifted(false);
                caps = false;
                encrypt = false;
                //updateShiftKeyState(attribute);
        }
        mCurKeyboard.setImeOptions(getResources(), attribute.imeOptions);

    }
    @Override
    public void onStartInputView(EditorInfo attribute, boolean restarting) {
        super.onStartInputView(attribute, restarting);
        // Apply the selected keyboard to the input view.
        setKeyboard(mCurKeyboard);
        //kv.closing();
        mComposing.setLength(0);
        mCandidateView.clear();
        //final InputMethodSubtype subtype = mInputMethodManager.getCurrentInputMethodSubtype();
        //mInputView.setSubtypeOnSpaceKey(subtype);

    }

    @Override
    public void onPress(int primaryCode) {
    }

    @Override
    public void onRelease(int primaryCode) {
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        db=new DatabaseHandler(getApplicationContext());
        InputConnection ic = getCurrentInputConnection();

        //For key press sound
        if(db.getCustomCodebyKeyCode(-999) == 1) playClick(primaryCode);
        
        //For vibrate
        if(db.getCustomCodebyKeyCode(-998) == 1) vibrate();

        //Aftering pasting a data, candicate view has to be clear
        if(paste==0 ) {
            mComposing.setLength(0);
            updateCandicate();
            paste=1;
        }
        else if(encrypt == false) setCandidatesViewShown(false);
        
        switch(primaryCode){
            case -997:

                caps=false;
                encrypt=!encrypt;
                setCandidatesViewShown(encrypt);
                mComposing.setLength(0);
                updateCandicate();
                if(mCurKeyboard==mQwertyKeyboard) setKeyboard(mEncryptKeyboard);
                else setKeyboard(mQwertyKeyboard);
                mCurKeyboard.setShifted(false);
                kv.invalidateAllKeys();

                break;

            case -32:
                imeManager.showInputMethodPicker();
                break;

            case -995:
                imeManager.showInputMethodPicker();
                break;

            case -996:
                getPasteData();
                setCandidatesViewShown(true);
                break;
            
            case Keyboard.KEYCODE_DELETE :
                ic.deleteSurroundingText(1, 0);
                if(mComposing.length()>1){
                    mComposing.delete(mComposing.length() - 1, mComposing.length());
                    updateCandicate();
                }else if(mComposing.length()>0){
                    mComposing.setLength(0);
                    updateCandicate();
                }
                break;

            case Keyboard.KEYCODE_SHIFT:
                //shift_count=shift_count+1;
                if(shift_count == 1) {
                    caps = !caps;
                    shift_count = 2;
                }
                else if(shift_count == 2){
                    caps=true;
                    shift_count =3;
                }
                else if(shift_count == 3){
                    caps=false;
                    shift_count =1;
                }
                if(mCurKeyboard == mQwertyKeyboard){
                    mCurKeyboard.setShifted(caps);
                    kv.invalidateAllKeys();
                }
                else if(mCurKeyboard == mEncryptKeyboard){
                    mCurKeyboard.setShifted(caps);
                    kv.invalidateAllKeys();
            }

                else if(mCurKeyboard==mSymbolsKeyboard )setKeyboard(mSymbolsShiftedKeyboard);
                else if(mCurKeyboard==mSymbolsShiftedKeyboard) setKeyboard(mSymbolsKeyboard);
                else if(mCurKeyboard==mQwerty_Myanmar_Keyboard) setKeyboard(mQwerty_Myanmar_Shift_Keyboard);
                else if(mCurKeyboard==mQwerty_Myanmar_Shift_Keyboard) setKeyboard(mQwerty_Myanmar_Keyboard);
                break;

            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
                mComposing.setLength(0);
                mCandidateView.clear();
                break;

            case Keyboard.KEYCODE_CANCEL:
                requestHideSelf(0);
                kv.closing();
                break;

            case Keyboard.KEYCODE_MODE_CHANGE:
                mComposing.setLength(0);
                setCandidatesViewShown(false);
                encrypt=false;
                updateCandicate();
                Keyboard current = kv.getKeyboard();
                if(current==mQwertyKeyboard || current==mEncryptKeyboard) {
                    setKeyboard(mSymbolsKeyboard);
                    Log.i("Mode Change","Change mode");
                }
                else if(mCurKeyboard==mSymbolsKeyboard || mCurKeyboard==mSymbolsShiftedKeyboard) setKeyboard(mQwertyKeyboard);
                else if(mCurKeyboard==mQwerty_Myanmar_Keyboard||mCurKeyboard==mQwerty_Myanmar_Shift_Keyboard) setKeyboard(mMyanmar_Symbol_Keyboard);
                else if(mCurKeyboard==mMyanmar_Symbol_Keyboard) setKeyboard(mQwerty_Myanmar_Keyboard);
                break;

            case Keyboard.KEYCODE_ALT:
                if(mCurKeyboard==mQwertyKeyboard || mCurKeyboard==mEncryptKeyboard ||mCurKeyboard==mSymbolsKeyboard|| mCurKeyboard==mSymbolsShiftedKeyboard) {
                    setKeyboard(mQwerty_Myanmar_Keyboard);

                }
                else if(mCurKeyboard==mQwerty_Myanmar_Keyboard || mCurKeyboard==mMyanmar_Symbol_Keyboard ||mCurKeyboard==mQwerty_Myanmar_Shift_Keyboard) {
                    setKeyboard(mQwertyKeyboard);
                    shift_count=1;
                    caps=false;
                }
                break;

            default:

                char code = (char)primaryCode;
                if(Character.isLetter(code) && caps && !encrypt){
                    code = Character.toUpperCase(code);
                    ic.commitText(String.valueOf(code), 1);
                    mComposing.append(String.valueOf(code));
                    updateCandicate();

                    //This is for shift count
                    if(shift_count ==2){
                        caps=false;
                        mCurKeyboard.setShifted(caps);
                        kv.invalidateAllKeys();
                        shift_count =1;
                    }
                }

                else if(primaryCode <= -97 && primaryCode >= -122){
                    String hidden=db.getTextByKeyCustomID(primaryCode);
                    ic.commitText(hidden, 1);
                    mComposing.append(hidden);
                    updateCandicate();

                }
                else if(Character.isLetter(code) && encrypt){
                    String temp;

                    //This is for shift count. This is same as the above function
                    if(shift_count ==2){
                        caps=false;
                        mCurKeyboard.setShifted(caps);
                        kv.invalidateAllKeys();
                        shift_count =1;
                    }

                    if (caps){
                        temp=db.getEncryptTextByKeyID(primaryCode).toUpperCase();
                        mComposing.append(String.valueOf(code).toUpperCase());
                    }

                    else {
                        temp = db.getEncryptTextByKeyID(primaryCode);
                        mComposing.append(String.valueOf(code));
                    }
                    ic.commitText(temp, 1);

                    updateCandicate();
                }

                else {
                    mComposing.append(String.valueOf(code));
                    updateCandicate();
                    ic.commitText(String.valueOf(code), 1);
                }
        }
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {
        requestHideSelf(0);
        kv.closing();
    }

    @Override
    public void swipeUp() {

    }

    /*
    Helper function to set Keyboard
     */
    private void setKeyboard(NyiKeyBoard nextKeyboard) {
        /*final boolean shouldSupportLanguageSwitchKey =
                mInputMethodManager.shouldOfferSwitchingToNextInputMethod(getToken());
        nextKeyboard.setLanguageSwitchKeyVisibility(shouldSupportLanguageSwitchKey);*/

        nextKeyboard.setImeOptions(getResources(), editorInfo.imeOptions);
        Log.i("Mode Change2", "Change mode2");
        mCurKeyboard=nextKeyboard;
        kv.setKeyboard(nextKeyboard);
        //kv.invalidateAllKeys();

    }


    /**
     * Called by the framework when your view for showing candidates needs to
     * be generated, like {@link #onCreateInputView}.
     */
    //This method is to create text bar at the top of the keyboard
    @Override
    public View onCreateCandidatesView() {
        LayoutInflater li=(LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.candicate_layout,null);
        LinearLayout ll=(LinearLayout) v.findViewById(R.id.wordbar);

        mCandidateView=new CandidateView(this);
        mCandidateView.setService(this);
        setCandidatesViewShown(false);
        mCandidateView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ll.addView(mCandidateView);

        return v;
    }
    protected void updateCandicate(){
        if (mComposing.length() > 0) {
            ArrayList<String> list = new ArrayList<String>();
            list.add(mComposing.toString());
            mCandidateView.setSuggestions(list, true, true);
        }else {
            mCandidateView.setSuggestions(null, false, false);
        }
    }

/*
    *//*
     * Deal with the editor reporting movement of its cursor.
     *//*
    @Override
    public void onUpdateSelection(int oldSelStart, int oldSelEnd,
                                            int newSelStart, int newSelEnd,
                                            int candidatesStart, int candidatesEnd) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
                candidatesStart, candidatesEnd);

        // If the current selection in the text view changes, we should
        // clear whatever candidate text we have.
        if (mComposing.length() > 0 && (newSelStart != candidatesEnd
                || newSelEnd != candidatesEnd)) {
            mComposing.setLength(0);
            updateCandicate();
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                ic.finishComposingText();
            }
        }
    }*/

/*
    for vibrate
 */
    private void vibrate() {
        Vibrator vibe= (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE) ;
        vibe.vibrate(50);
    }

    /*
     This method is for play sound
     */
    private void playClick(int keyCode){
        AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);
        float vol = (float) 0.5;
        switch(keyCode){
            case 32:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR,vol);
                break;
            case Keyboard.KEYCODE_DONE:
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN,vol);
                break;
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE,vol);
                break;
            default: am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD,vol);

        }
    }

    /*
    This is from nga pyin keyboard
     */
    private void soundOnPress() {
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        float vol = (float) 0.5;
        am.playSoundEffect(AudioManager.FX_KEY_CLICK, vol);
    }
    private void getPasteData(){
        mComposing.setLength(0);
        paste=0;
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        CharSequence pasteData = "";

        /*// If the clipboard doesn't contain data, disable the paste menu item.
        // If it does contain data, decide if you can handle the data.
            if (!(clipboard.hasPrimaryClip())) {

            mPasteItem.setEnabled(false);

        } else if (!(clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {

            // This disables the paste menu item, since the clipboard has data but it is not plain text
            mPasteItem.setEnabled(false);
        } else {

            // This enables the paste menu item, since the clipboard contains plain text.
            mPasteItem.setEnabled(true);
        }*/

        // Examines the item on the clipboard. If getText() does not return null, the clip item contains the
        // text. Assumes that this application can only handle one item at a time.
        if (clipboard.hasPrimaryClip()) {
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);


            // If the string contains data, then the paste operation is done
            if (pasteData != null) {
                // Gets the clipboard as text.
                pasteData = item.getText();
                String decrypt = decrypt(pasteData);
                mComposing.append(decrypt);
                updateCandicate();
                return;

                // The clipboard does not contain text. If it contains a URI, attempts to get data from it
            } else {
                Uri pasteUri = item.getUri();
                return;
            }

        }

    }
    private String decrypt(CharSequence pasteData){
        String decryptText="";
        for(int i=0;i<pasteData.length(); i++){
            char word=pasteData.charAt(i);

            if(Character.isLetter(word)){
                int code=Character.valueOf(word);
                Log.i("code",code+"");
                if(code>=97 && code<=122){
                    //code 97 is a and code 122 is z

                Log.i("decrypt","Decrypt");
                if(Character.isUpperCase(word)){
                    word=Character.toLowerCase(word);

                    decryptText=decryptText+db.getRealWordByEncryptWord(word).toUpperCase();
                }
                else decryptText=decryptText+db.getRealWordByEncryptWord(word);
            }else{
                    decryptText=decryptText+word;
                }
            }
            else{
                decryptText=decryptText+word;
            }
        }
        return decryptText;

    }




}
