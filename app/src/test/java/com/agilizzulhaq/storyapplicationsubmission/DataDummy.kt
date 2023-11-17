package com.agilizzulhaq.storyapplicationsubmission

import com.agilizzulhaq.storyapplicationsubmission.data.responses.ListStoryItem

object DataDummy {

    fun generateDummyListStoryItem(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "https://story-api.dicoding.dev/images/stories/photos-9415753193901_dummy-pic.png",
                "Izzy $i",
                "Emang boleh seunit testing ini? $i",
                "2023-08-09T07:32:58.111Z",
                -6.958822,
                110.099870
            )
            items.add(story)
        }
        return items
    }
}