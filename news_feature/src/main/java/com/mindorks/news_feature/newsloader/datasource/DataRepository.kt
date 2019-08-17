package com.mindorks.news_feature.newsloader.datasource

import com.mindorks.news_feature.newsloader.model.NewsDataClass

object DataRepository {
    fun getNews(): List<NewsDataClass> {
        val output = ArrayList<NewsDataClass>()
        (1..5).forEach { index ->
            output.add(NewsDataClass(index, "News Title $index", "News Description $index", index * 2))
        }

        return output
    }
}
