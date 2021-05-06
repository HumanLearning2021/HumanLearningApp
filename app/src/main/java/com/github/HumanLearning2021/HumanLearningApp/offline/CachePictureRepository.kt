package com.github.HumanLearning2021.HumanLearningApp.offline

import android.content.Context

class CachePictureRepository(dbName: String, context: Context): PictureRepository(dbName, context, context.cacheDir)