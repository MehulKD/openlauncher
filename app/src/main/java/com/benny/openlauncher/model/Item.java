package com.benny.openlauncher.model;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Parcel;

import com.benny.openlauncher.R;
import com.benny.openlauncher.activity.Home;
import com.benny.openlauncher.core.interfaces.LabelProvider;
import com.benny.openlauncher.util.AppManager;
import com.benny.openlauncher.util.Tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Item implements com.benny.openlauncher.core.interfaces.Item<Item>, LabelProvider {
    public static final Creator<Item> CREATOR = new Creator<Item>() {

        @Override
        public Item createFromParcel(Parcel parcel) {
            return new Item(parcel);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    // all items need these values
    private int idValue;
    public Type type;
    private String name = "";
    public Drawable icon = Home.launcher.getResources().getDrawable(R.drawable.rip);
    public int x = 0;
    public int y = 0;

    // intent for shortcuts and apps
    public Intent intent;

    // list of items for groups
    public List<Item> items;

    // int value for launcher action
    public int actionValue;

    // widget specific values
    public int widgetValue;
    public int spanX = 1;
    public int spanY = 1;

    public Item() {
        Random random = new Random();
        idValue = random.nextInt();
    }

    public Item(Parcel parcel) {
        idValue = parcel.readInt();
        type = Type.valueOf(parcel.readString());
        name = parcel.readString();
        x = parcel.readInt();
        y = parcel.readInt();
        switch (type) {
            case APP:
            case SHORTCUT:
                intent = Tool.getIntentFromString(parcel.readString());
                break;
            case GROUP:
                List<String> labels = new ArrayList<>();
                parcel.readStringList(labels);
                items = new ArrayList<>();
                for (String s : labels) {
                    items.add((Item) Home.launcher.db.getItem(Integer.parseInt(s)));
                }
                break;
            case ACTION:
                actionValue = parcel.readInt();
                break;
            case WIDGET:
                widgetValue = parcel.readInt();
                spanX = parcel.readInt();
                spanY = parcel.readInt();
                break;
        }
        icon = Tool.getIcon(Home.launcher, Integer.toString(idValue));
    }

    @Override
    public boolean equals(Object object) {
        Item itemObject = (Item) object;
        return object != null && this.idValue == itemObject.idValue;
    }

    public static Item newAppItem(AppManager.App app) {
        Item item = new Item();
        item.type = Type.APP;
        item.name = app.label;
        item.icon = app.icon;
        item.intent = toIntent(app);
        return item;
    }

    public static Item newShortcutItem(Intent intent, Drawable icon, String name) {
        Item item = new Item();
        item.type = Type.SHORTCUT;
        item.name = name;
        item.icon = icon;
        item.spanX = 1;
        item.spanY = 1;
        item.intent = intent;
        return item;
    }

    public static Item newGroupItem() {
        Item item = new Item();
        item.type = Type.GROUP;
        item.name = Home.launcher.getString(R.string.folder);
        item.spanX = 1;
        item.spanY = 1;
        item.items = new ArrayList<>();
        return item;
    }

    public static Item newActionItem(int action) {
        Item item = new Item();
        item.type = Type.ACTION;
        item.spanX = 1;
        item.spanY = 1;
        item.actionValue = action;
        return item;
    }

    public static Item newWidgetItem(int widgetValue) {
        Item item = new Item();
        item.type = Type.WIDGET;
        item.widgetValue = widgetValue;
        item.spanX = 1;
        item.spanY = 1;
        return item;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(idValue);
        out.writeString(type.toString());
        out.writeString(name);
        out.writeInt(x);
        out.writeInt(y);
        switch (type) {
            case APP:
            case SHORTCUT:
                out.writeString(Tool.getIntentAsString(this.intent));
                break;
            case GROUP:
                List<String> labels = new ArrayList<>();
                for (Item i : items) {
                    labels.add(Integer.toString(i.idValue));
                }
                out.writeStringList(labels);
                break;
            case ACTION:
                out.writeInt(actionValue);
                break;
            case WIDGET:
                out.writeInt(widgetValue);
                out.writeInt(spanX);
                out.writeInt(spanY);
                break;
        }
    }

    @Override
    public void reset() {
        Random random = new Random();
        idValue = random.nextInt();
    }

    @Override
    public int getId() {
        return idValue;
    }

    public void setId(int id) {
        idValue = id;
    }

    @Override
    public Intent getIntent() {
        return intent;
    }

    @Override
    public String getLabel() {
        return name;
    }

    @Override
    public void setLabel(String label) {
        this.name = label;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public List<Item> getGroupItems() {
        return items;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getSpanX() {
        return spanX;
    }

    @Override
    public int getSpanY() {
        return spanY;
    }

    @Override
    public void setSpanX(int x) {
        spanX = x;
    }

    @Override
    public void setSpanY(int y) {
        spanY = y;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    private static Intent toIntent(AppManager.App app) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(app.packageName, app.className);
        return intent;
    }
}
