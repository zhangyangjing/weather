package com.zhangyangjing.weather.sync;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by zhangyangjing on 03/11/2016.
 */

public class Authenticator extends AbstractAccountAuthenticator {
    private static final String TAG = Authenticator.class.getSimpleName();

    private Context mContext;

    public Authenticator(Context context) {
        super(context);
        mContext = context;
        Log.d(TAG, "Authenticator() called with: context = [" + context + "]");
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
        Log.d(TAG, "editProperties() called with: accountAuthenticatorResponse = [" + accountAuthenticatorResponse + "], s = [" + s + "]");
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Log.d(TAG, "addAccount() called with: accountAuthenticatorResponse = [" + response + "], accountType = [" + accountType + "], authTokenType = [" + authTokenType + "], requiredFeatures = [" + requiredFeatures + "], options = [" + options + "]");
        return null;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
        Log.d(TAG, "confirmCredentials() called with: accountAuthenticatorResponse = [" + accountAuthenticatorResponse + "], account = [" + account + "], bundle = [" + bundle + "]");
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String s, Bundle bundle) throws NetworkErrorException {
        Log.d(TAG, "getAuthToken() called with: response = [" + response + "], account = [" + account + "], s = [" + s + "], bundle = [" + bundle + "]");
        return null;
    }

    @Override
    public String getAuthTokenLabel(String s) {
        Log.d(TAG, "getAuthTokenLabel() called with: s = [" + s + "]");
        return "shenmegui";
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
        Log.d(TAG, "updateCredentials() called with: accountAuthenticatorResponse = [" + accountAuthenticatorResponse + "], account = [" + account + "], s = [" + s + "], bundle = [" + bundle + "]");
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
        Log.d(TAG, "hasFeatures() called with: accountAuthenticatorResponse = [" + accountAuthenticatorResponse + "], account = [" + account + "], strings = [" + strings + "]");
        final Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return result;
    }
}
