/*
 * Copyright (C) 2018 mutter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wanghong.kutils;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by mutter on 1/20/18.
 */

@RunWith(AndroidJUnit4.class)
public class ShellUtilsTest {
    private static final String TAG = "ShellUtilsTest";

    @Test
    public void shellExec() throws Exception {
        ShellExecResult result = ShellUtils.Companion.exec("ls " + InstrumentationRegistry.getTargetContext().getFilesDir().getAbsoluteFile().getParent(),
                true, true);
        Assert.assertEquals(0, result.getStatus());
        Log.d(TAG, "shellExec: " + result.toString());
    }
}
