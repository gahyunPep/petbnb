package pet.eaters.ca.petbnb.pets.postform;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;

public class NonEmptyTextWatcher implements TextWatcher {

    private TextInputLayout mInputLayout;
    private String mErrorMsg;

    public NonEmptyTextWatcher(TextInputLayout inputLayout, String errorMsg) {
        mInputLayout = inputLayout;
        mErrorMsg = errorMsg;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 0) {
            mInputLayout.setError(mErrorMsg);
        } else {
            mInputLayout.setErrorEnabled(false);
        }
    }
}
