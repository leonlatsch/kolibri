package de.leonlatsch.olivia.boot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.orm.SchemaGenerator;
import com.orm.SugarContext;
import com.orm.SugarDb;

import java.util.Iterator;

import de.leonlatsch.olivia.R;
import de.leonlatsch.olivia.chatlist.ChatListActivity;
import de.leonlatsch.olivia.constants.Values;
import de.leonlatsch.olivia.entity.User;
import de.leonlatsch.olivia.login.LoginActivity;

public class BootActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot);
        SugarContext.init(getApplicationContext());
        // create table if not exists
        SchemaGenerator schemaGenerator = new SchemaGenerator(this);
        schemaGenerator.createDatabase(new SugarDb(this).getDB());

        Intent intent = null;

        if (isUserSaved()) {
            intent = new Intent(getApplicationContext(), ChatListActivity.class);
        } else {
            intent = new Intent(getApplicationContext(), LoginActivity.class);
        }

        // Make it so you cant go back to this activity
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(intent);
    }

    private boolean isUserSaved() {
        Iterator iterator = User.findAll(User.class);
        return iterator.hasNext();
    }

    // May be useful later
    private boolean isFirstBoot() {
        SharedPreferences sharedPreferences = getSharedPreferences(Values.PREF_FIRST_BOOT, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (sharedPreferences.getBoolean(Values.PREF_FIRST_BOOT, true)) {
            editor.putBoolean(Values.PREF_FIRST_BOOT, false);
            editor.apply();
            return true;
        } else {
            return false;
        }
    }
}
