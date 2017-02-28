package com.github.privacystreams.accessibility;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.github.privacystreams.core.MultiItemStream;
import com.github.privacystreams.core.providers.MultiItemStreamProvider;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author toby
 * @date 2/28/17
 * @time 11:23 AM
 */
public class AccessibilityEventProvider extends MultiItemStreamProvider {

    private MyAccessibilityService sharedServiceInstance;
    private MultiItemStream stream;
    private int eventTypeMask = 0x0;
    public static final int BASE_ACCESSIBILITY_EVENT_MASK = 0x1, USER_UI_ACTION_ACCESSIBILITY_EVENT_MASK = 0x2, USER_TEXT_ENTRY_ACCESSIBILITY_EVENT_MASK = 0x4;


    public AccessibilityEventProvider(int eventTypeMask){
        this.eventTypeMask = eventTypeMask;
        sharedServiceInstance = MyAccessibilityService.getSharedInstance();
        if(sharedServiceInstance != null)
            sharedServiceInstance.registerProvider(this);
    }

    @Override
    protected void provide(MultiItemStream output) {
        this.stream = output;
    }

    @Override
    protected void onCancel(Void input, MultiItemStream output) {
        super.onCancel(input, output);
        if(sharedServiceInstance != null)
            sharedServiceInstance.unregisterProvider(this);

    }

    public void handleAccessibilityEvent(AccessibilityEvent event, AccessibilityNodeInfo rootNode, Date timeStamp){
        if(stream == null)
            return;
        if((eventTypeMask & USER_TEXT_ENTRY_ACCESSIBILITY_EVENT_MASK) > 0 && UserTextEntryAccessibilityEventItem.isATextEntryAccessibilityEventType(event)){
            stream.write(new UserTextEntryAccessibilityEventItem(event, rootNode, timeStamp));
        }
        else if((eventTypeMask & USER_UI_ACTION_ACCESSIBILITY_EVENT_MASK) > 0 && UserUIActionAccessibilityEventItem.isAUserUIActionAccessibilityEventType(event)){
            stream.write(new UserUIActionAccessibilityEventItem(event, rootNode, timeStamp));
        }
        else if((eventTypeMask & BASE_ACCESSIBILITY_EVENT_MASK) > 0 && BaseAccessibilityEventItem.isABaseAccessibilityEventType(event)){
            stream.write(new BaseAccessibilityEventItem(event, rootNode, timeStamp));
        }
    }
}