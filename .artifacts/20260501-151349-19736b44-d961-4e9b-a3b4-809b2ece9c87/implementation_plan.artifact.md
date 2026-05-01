# Implementation Plan - Change Proceed Button Colors

This plan outlines the changes needed to change the background color of the "Proceed to Snacks" button to white and its text color to black in the seat selection screen.

## Proposed Changes

### [Drawables]

#### [NEW] [bg_button_white_pill.xml](file:///C:/Users/izena/OneDrive/Desktop/CineFast/app/src/main/res/drawable/bg_button_white_pill.xml)

- Create a new drawable for the white pill-shaped button background.

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/white" />
    <corners android:radius="32dp" />
</shape>
```

---

### [Layouts]

#### [fragment_seat_selection.xml](file:///C:/Users/izena/OneDrive/Desktop/CineFast/app/src/main/res/layout/fragment_seat_selection.xml)

- Update the "Proceed to Snacks" button to use the new white background and set the text color to black.

```diff
         <androidx.appcompat.widget.AppCompatButton
             android:layout_width="0dp"
             android:layout_height="64dp"
             android:layout_marginStart="6dp"
             android:layout_weight="1"
-            android:background="@drawable/bg_button_red_pill"
+            android:background="@drawable/bg_button_white_pill"
             android:text="Proceed to Snacks"
             android:textAllCaps="false"
-            android:textColor="#FFFFFF"
+            android:textColor="@color/black"
             android:textSize="16sp"
             android:stateListAnimator="@null"
             android:padding="0dp" />
```

## Verification Plan

### Manual Verification
- Render the Compose Preview or inspect the layout in the editor if possible.
- Since this is a layout change, I will use `render_compose_preview` if there's a corresponding composable, but this is a traditional XML layout.
- I will verify the changes by inspecting the XML and ensuring the resources are correctly referenced.
- I'll try to use `deploy` to run the app on the device and navigate to the seat selection screen to see the change.
