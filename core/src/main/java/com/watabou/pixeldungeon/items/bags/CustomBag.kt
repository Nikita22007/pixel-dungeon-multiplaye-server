package com.watabou.pixeldungeon.items.bags

import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.items.CustomItem
import com.watabou.pixeldungeon.items.Item
import org.json.JSONArray
import org.json.JSONObject

class CustomBag(obj: JSONObject) : Bag(obj) {
    init {

        cursedKnown = true // todo check it
        size = obj.getInt("size");
        if (obj.has("owner")){
            owner = Actor.findById(obj.getInt("owner")) as Char?
        }
        if (obj.has("items")) {
            addItemsFromJSONArray(obj.getJSONArray("items"))
        }
    }

    private fun addItemsFromJSONArray(arr: JSONArray) {
        for (i in 0 until arr.length()) {
            val itemObj = arr.getJSONObject(i);
            val item: Item =
            if (itemObj.has("size")) {
                CustomBag(itemObj)
            } else {
                CustomItem(itemObj)
            }
            items.add(item)
        }
    }
}