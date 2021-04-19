package com.github.HumanLearning2021.HumanLearningApp.offline

import android.content.Context
import android.net.Uri
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreCategorizedPicture

class CachePictureRepository(dbName: String, context: Context): PictureRepository(dbName, context, context.cacheDir)