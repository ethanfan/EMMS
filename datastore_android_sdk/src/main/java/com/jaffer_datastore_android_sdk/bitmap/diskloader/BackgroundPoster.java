/*
 * Copyright (C) 2012 Jaffer Junginger, greenrobot (http://greenrobot.de),张涛
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jaffer_datastore_android_sdk.bitmap.diskloader;

import com.jaffer_datastore_android_sdk.bitmap.DiskImageDisplayer;
import com.jaffer_datastore_android_sdk.bitmap.client.BitmapCore;
import com.jaffer_datastore_android_sdk.bitmap.client.BitmapRequestConfig;
import com.jaffer_datastore_android_sdk.rxvolley.client.HttpCallback;
import com.jaffer_datastore_android_sdk.rxvolley.rx.Result;
import com.jaffer_datastore_android_sdk.rxvolley.rx.RxBus;
import com.jaffer_datastore_android_sdk.rxvolley.toolbox.Loger;

import java.util.Collections;

/**
 * 从本地加载一个bitmap的任务,串行异步加载的实现,思路取自EventBus
 *
 */
public class BackgroundPoster extends AsyncPoster {

    private volatile boolean executorRunning;

    public BackgroundPoster(DiskImageDisplayer displayer) {
        super(displayer);
    }

    @Override
    public void enqueue(BitmapRequestConfig config, HttpCallback callback) {
        PendingPost pendingPost = PendingPost.obtainPendingPost(config, callback);
        synchronized (this) {
            queue.enqueue(pendingPost);
            if (!executorRunning) {
                executorRunning = true;
                BitmapCore.getExecutorService().execute(this);
            }
        }
    }

    @Override
    public void run() {
        try {
            try {
                while (true) {
                    PendingPost pendingPost = queue.poll(1000);
                    if (pendingPost == null) {
                        synchronized (this) {
                            // Check again, this time in synchronized
                            pendingPost = queue.poll();
                            if (pendingPost == null) {
                                executorRunning = false;
                                return;
                            }
                        }
                    }
                    byte[] bytes = loadFromFile(pendingPost.config.mUrl, pendingPost.config
                            .maxWidth, pendingPost.config.maxHeight, pendingPost.callback);
                    RxBus.getDefault().post(new Result(pendingPost.config.mUrl,
                            Collections.<String, String>emptyMap(), bytes));
                    PendingPost.releasePendingPost(pendingPost);
                }
            } catch (InterruptedException e) {
                Loger.debug(Thread.currentThread().getName() + " was interruppted" + e.getMessage
                        ());
            }
        } finally {
            executorRunning = false;
        }
    }
}
