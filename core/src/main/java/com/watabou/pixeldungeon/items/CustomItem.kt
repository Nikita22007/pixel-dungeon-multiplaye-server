package com.watabou.pixeldungeon.items

import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.bags.CustomBag
import com.watabou.pixeldungeon.network.SendData.SendItemAction
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

open class CustomItem() : Item() {
    protected var descString: String? = null

    protected var actionsList: ArrayList<String> = ArrayList();

    protected var identified = false;

    var showBar: Boolean = false;
    public var ui: UI = UI();

    constructor(obj: JSONObject) : this() {
        cursedKnown = true // todo check it
        update(obj)

    }

    override fun update(obj: JSONObject) {
        val it = obj.keys()
        while (it.hasNext()) {
            val token = it.next()
            when (token) {
                "name" -> {
                    name = obj.getString(token)
                }
                "info" -> {
                    descString = obj.getString(token);
                }
                "image" -> {
                    image = obj.getInt(token);
                }
                "stackable" -> {
                    stackable = obj.getBoolean(token);
                }
                "quantity", "count" -> {
                    quantity = obj.getInt(token)
                }
                "durability" -> {
                    durability = obj.getInt(token)
                }
                "level" -> {
                    level = obj.getInt(token)
                }
                "level_known" -> {
                    levelKnown = obj.getBoolean(token)
                }
                "cursed" -> {
                    cursed = obj.getBoolean(token)
                }
                "identified" -> {
                    identified = obj.getBoolean(token)
                }
                "actions" -> {
                    parseActions(obj.getJSONArray(token))
                }
                "default_action" -> {
                    val action: String = obj.getString(token);
                    defaultAction = if (action == "null") {
                        null
                    } else {
                        action
                    };
                }
                "ui" -> {
                    val uiObj = obj.getJSONObject(token);
                    ui = UI(uiObj)
                }
                "show_bar" -> {
                    showBar = obj.getBoolean(token);
                }
            }
        }
    }

    private fun parseActions(actionsArr: JSONArray) {
        val actions = ArrayList<String>(actionsArr.length());
        for (i in 0 until actionsArr.length()) {
            val action = actionsArr.getString(i);
            actions.add(action)
        }
        actionsList = actions;
    }

    override fun desc(): String {
        return descString ?: "idk,wtf"
    }

    override fun actions(hero: Hero?): ArrayList<String> {
        return actionsList.clone() as ArrayList<String>
    }

    override fun isIdentified(): Boolean {
        return identified;
    }

    override fun execute(hero: Hero, action: String) {
        SendItemAction(this, hero, action)
        //super.execute(hero, action)
    }

    override fun visiblyUpgraded(): Int {
        return level;
    }

    public class UI {
        val topLeft: Label;
        val topRight: Label;
        val bottomRight: Label;

        constructor(obj: JSONObject) {
            val topLeftObj: JSONObject? = obj.optJSONObject("top_left");
            if (topLeftObj == null) {
                topLeft = Label(null, null, false);
            } else {
                var color: Int? = null;
                if (topLeftObj.has("color")) {
                    color = topLeftObj.optInt("color", 0)
                }
                topLeft = Label(
                    color,
                    topLeftObj.optString("text"),
                    topLeftObj.optBoolean("visible", false)
                );
            }

            val topRightObj: JSONObject? = obj.optJSONObject("top_right");
            if (topRightObj == null) {
                topRight = Label(null, null, false);
            } else {
                var color: Int? = null;
                if (topRightObj.has("color")) {
                    color = topRightObj.optInt("color", 0)
                }
                topRight = Label(
                    color,
                    topRightObj.optString("text"),
                    topRightObj.optBoolean("visible", false)
                );
            }

            val bottomRightObj: JSONObject? = obj.optJSONObject("bottom_right");
            if (bottomRightObj == null) {
                bottomRight = Label(null, null, false);
            } else {
                var color: Int? = null;
                if (bottomRightObj.has("color")) {
                    color = bottomRightObj.optInt("color", 0)
                }
                bottomRight = Label(
                    color,
                    bottomRightObj.optString("text"),
                    bottomRightObj.optBoolean("visible", false)
                );
            }
        }

        constructor() {
            topLeft = Label(null, null, false);
            topRight = Label(null, null, false);
            bottomRight = Label(null, null, false);
        }

        public class Label {
            val text: String?;
            val color: Int?;
            val visible: Boolean;

            constructor(color: Int?, text: String?, visible: Boolean) {
                this.text = text;
                this.color = color;
                this.visible = visible;
            }
        }
    }
}