package com.github.HumanLearning2021.HumanLearningApp.offline

import android.content.Context

class CachePictureStorage(dbName: String, context: Context): PictureStorage(dbName, context, context.cacheDir)