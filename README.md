# a11y_service

## Usage

- AndroidManifest.xml
```xml
<service
    android:name="com.liasica.a11y_service.A11yService"
    android:exported="true"
    android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE">
    <intent-filter>
        <action android:name="android.accessibilityservice.AccessibilityService" />
    </intent-filter>
    <meta-data
        android:name="android.accessibilityservice"
        android:resource="@xml/accessibilityservice" />
</service>
```

- res/xml/accessibilityservice.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<accessibility-service xmlns:android="http://schemas.android.com/apk/res/android"
    android:accessibilityEventTypes="typeWindowsChanged|typeWindowStateChanged|typeWindowContentChanged"
    android:accessibilityFeedbackType="feedbackVisual"
    android:canPerformGestures="true"
    android:notificationTimeout="300"
    android:accessibilityFlags="flagDefault|flagIncludeNotImportantViews|flagRequestTouchExplorationMode|flagRequestEnhancedWebAccessibility|flagReportViewIds|flagRetrieveInteractiveWindows"
    android:canRetrieveWindowContent="true"
>
</accessibility-service>
```

## Refers
- [简单封装AccessibilityService写个库，助力Android自动化](https://juejin.cn/post/7242623772301180989)