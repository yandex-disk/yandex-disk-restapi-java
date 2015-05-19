/*
* (C) 2015 Yandex LLC (https://yandex.com/)
*
* The source code of Java SDK for Yandex.Disk REST API
* is available to use under terms of Apache License,
* Version 2.0. See the file LICENSE for the details.
*/

package com.yandex.disk.rest.example;


import java.util.ArrayList;

import android.content.Context;

import com.facebook.stetho.DumperPluginsProvider;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.dumpapp.DumperPlugin;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        final Context context = this;
        Stetho.initialize(
                Stetho.newInitializerBuilder(context)
                        .enableDumpapp(new SampleDumperPluginsProvider(context))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                        .build());
    }

    private static class SampleDumperPluginsProvider implements DumperPluginsProvider {
        private final Context mContext;

        public SampleDumperPluginsProvider(Context context) {
            mContext = context;
        }

        @Override
        public Iterable<DumperPlugin> get() {
            ArrayList<DumperPlugin> plugins = new ArrayList<DumperPlugin>();
            for (DumperPlugin defaultPlugin : Stetho.defaultDumperPluginsProvider(mContext).get()) {
                plugins.add(defaultPlugin);
            }
//            plugins.add(new HelloWorldDumperPlugin());
            return plugins;
        }
    }
}
