package com.zhangyangjing.weather.sync;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.zhangyangjing.weather.LoginActivity;

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
        final Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle b = new Bundle();
        b.putParcelable(AccountManager.KEY_INTENT, intent);
        return b;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
        Log.d(TAG, "confirmCredentials() called with: accountAuthenticatorResponse = [" + accountAuthenticatorResponse + "], account = [" + account + "], bundle = [" + bundle + "]");
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String s, Bundle bundle) throws NetworkErrorException {
        Log.d(TAG, "getAuthToken() called with: response = [" + response + "], account = [" + account + "], s = [" + s + "], bundle = [" + bundle + "]");
        final AccountManager am = AccountManager.get(mContext);
        String authToken = am.peekAuthToken(account, "com.zhangyangjing.weather");

        if (TextUtils.isEmpty(authToken)) {
            final String password = am.getPassword(account);
            if (password != null) {
                authToken = password;
            }
        }

        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        final Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle b = new Bundle();
        b.putParcelable(AccountManager.KEY_INTENT, intent);
        return b;
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
