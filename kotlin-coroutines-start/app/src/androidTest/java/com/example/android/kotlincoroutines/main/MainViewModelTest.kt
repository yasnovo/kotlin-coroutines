/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.kotlincoroutines.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.android.kotlincoroutines.test.util.captureValues
import com.google.common.truth.Truth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MainViewModelTest {

    /**
     * In this test, LiveData will immediately post values without switching threads.
     */
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    var testDispatcher = TestCoroutineDispatcher()
    lateinit var subject: MainViewModel

    @Before
    fun setup() {
        // Dispatchers.Mainをセット
        // 端末外でテストを実行できるようになる
        Dispatchers.setMain(testDispatcher)
        subject = MainViewModel()
    }

    @After
    fun teardown() {
        // テストが終わったらmainをリセット
        Dispatchers.resetMain()
        // TestCoroutineDispatcherが次のテストで状態を
        // 保持してしまわないように以下を呼んでおく
        testDispatcher.cleanupTestCoroutines()
    }



    private fun <T> LiveData<T>.observeForTesting(
            block: () -> Unit) {
        val observer = Observer<T> { Unit }
        try {
            observeForever(observer)
            block()
        } finally {
            removeObserver(observer)
        }
    }

    @Test
    fun whenMainViewModelClicked_showSnackbar() = testDispatcher.runBlockingTest {
        subject.snackbar.observeForTesting {
            subject.onMainViewClicked()
            advanceTimeBy(1_000)
            Truth.assertThat(subject.snackbar.value)
                    .isEqualTo("Hello, from coroutines!!!!")
        }
    }
//    {
//        runBlocking {
//            subject.snackbar.captureValues {
//                subject.onMainViewClicked()
//                assertSendsValues(2_000, "Hello, from coroutines!!!!")
//            }
//        }
//    }

}

