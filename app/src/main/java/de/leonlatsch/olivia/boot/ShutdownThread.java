package de.leonlatsch.olivia.boot;

import com.orm.SugarContext;

public class ShutdownThread extends Thread {

    @Override
    public void run() {
        SugarContext.terminate();
    }
}
