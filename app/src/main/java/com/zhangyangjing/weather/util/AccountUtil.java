package com.zhangyangjing.weather.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import static android.content.Context.ACCOUNT_SERVICE;

/**
 * Created by zhangyangjing on 03/11/2016.
 */

public class AccountUtil {
    public static Account getSyncAccount(Context context) {
        Account newAccount = new Account("sync_normal", "com.zhangyangjing.weather.normal_sync");
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        accountManager.addAccountExplicitly(newAccount, null, null);
        return newAccount;
    }
}
