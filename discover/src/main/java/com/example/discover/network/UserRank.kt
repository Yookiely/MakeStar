package com.example.discover.network

import com.example.common.experimental.extensions.QuietCoroutineExceptionHandler
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

object UserRank {
    fun getUserRank(key: Int, userRankCallBack: (List<userData>) -> Unit) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = RankService.getMonthUserRank(key).await()
            if (callback.error_code == -1) {
                userRankCallBack(callback.data!!)
            }

        }
    }

    fun getWorkRank(key: Int, workRankCallBack: (List<workData>) -> Unit) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = RankService.getMonthWorkRank(key).await()
            if (callback.error_code == -1) {
                workRankCallBack(callback.data!!)
            }
        }
    }

    fun getVedioProof(key: String, proofCallBack: (proofData) -> Unit) {
        launch(UI + QuietCoroutineExceptionHandler) {
            val callback = RankService.getProof(key).await()
            if (callback.error_code == -1) {
                proofCallBack(callback.data!!)
            }
        }
    }
}