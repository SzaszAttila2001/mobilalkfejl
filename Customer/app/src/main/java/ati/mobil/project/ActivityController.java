package ati.mobil.project;

import android.content.Intent;

public class ActivityController {
    private final android.content.Context caller;
    public static final Class<?> MAIN_ACTIVITY = MainActivity.class;
    public static final Class<?> CUSTOMER_LIST_ACTIVITY = CustomerListActivity.class;
    public static final Class<?> LOGIN_ACTIVITY = LoginActivity.class;
    public static final Class<?> NEW_CUSTOMER_ACTIVITY = NewCustomerActivity.class;
    public static final Class<?> REGISTER_ACTIVITY = RegisterActivity.class;

    public ActivityController(android.content.Context caller){
        this.caller = caller;
    }

    public void to_activity(Class<?> cls){
        Intent intent = new Intent(caller, cls);
        caller.startActivity(intent);
    }
}
