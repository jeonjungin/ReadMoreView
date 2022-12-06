# ReadMoreView

유튜브 앱과 같이 '더보기' 기능이 포함된 TextView 입니다.
기존 TextView 와 같이 TextSize, color, font 등의 속성 적용이 가능합니다.

<img src="https://user-images.githubusercontent.com/40448001/204996359-8cac44d6-948a-416a-b800-852c09b91f77.gif">

## dependency

### 최상위 수준 build.gradle

```gradle

allprojects {
    repositories {
      ...
      maven { url 'https://jitpack.io' }
    }
}

```

### app 수준 build.gradle

``` gradle

dependencies {
    implementation 'com.github.jeonjungin:ReadMoreView:Tag'
}
  
```

## xml

```xml

<com.willbegod.readmore.view.ReadMoreView
  ...
  android:textColor="@color/black"
  android:textSize="20sp"
  app:collapseBtnText="축소"
  app:expandBtnText="확장"
  app:btnColor="@color/purple_500"
  app:btnSize="15sp"
  app:colMaxLines="2"
  app:originalText="원하는 텍스트를 넣어주세요 :)"
  />

```
### collapseBtnText

ReadMoreView 내에 표시되는 '축소' 기능 버튼의 String을 정의합니다.
단, 빈 String 을 입력하는 경우 표시되지 않습니다.
기본값은 '닫기' 입니다.
```xml
  app:collapseBtnText="축소"
```

### expandBtnText

ReadMoreView 내에 표시되는 '확장' 기능 버튼의 String을 정의합니다.
단, 빈 String 을 입력하는 경우 표시되지 않습니다.
기본값은 '기더보기' 입니다.

```xml
  app:expandBtnText="확장"
```

### btnColor

ReadMoreView 내에 표시되는 '축소', '확장' 기능 버튼의 color를 정의합니다.
기본값은 Color.Black 입니다.
```xml
  app:btnColor="@color/black"
```

### btnSize

ReadMoreView 내에 표시되는 '축소', '확장' 기능 버튼의 size를 정의합니다.
```xml
  app:btnSize="15sp"
```

### colMaxLines

ReadMoreView 축소 상태시 최대 라인 수를 정의합니다.
기본값은 Int.MAX_VALUE 입니다.
```xml
  app:colMaxLines="2"
```

### originalText

ReadMoreView 에 표시될 String 전문을 정의합니다.
collapse 상태인 경우 colMaxLines 속성 값에 따라 최대 라인 수 만큼만 표시됩니다.
expanded 상태인 경우 String 전문이 표시됩니다.
```xml
  app:originalText="원하는 텍스트를 넣어주세요 :)"
```



## 클래스 필드 및 메서드

### originalText

ReadMoreView 에 표시될 String 전문을 정의합니다.
xml 속성 originalText 와 동일합니다.
```kotlin
  readMoreView.originalText = "원하는 텍스트를 넣어주세요 :)"
```

### isExpanded

ReadMoreView 의 '확장' 상태 여부를 반환합니다. (Boolean)
```kotlin
  readMoreView.isExpanded == true
```

### isCollapsed

ReadMoreView 의 '축소' 상태 여부를 반환합니다. (Boolean)
```kotlin
  readMoreView.isCollapsed == true
```

### expand()

ReadMoreView 의 상태를 '확장' 으로 변경합니다.
이미 확장 상태인 경우 동작하지 않습니다.
```kotlin
  readMoreView.expand()
```

### collapse()

ReadMoreView 의 상태를 '축소' 로 변경합니다.
이미 축소 상태인 경우 동작하지 않습니다.
```kotlin
  readMoreView.collapse()
```

### toggle()

ReadMoreView 의 상태를 '반전' 시킵니다.
```kotlin
  readMoreView.toggle()
```

### setStateChangedListener(listener: ((isExpanded: Boolean) -> Unit)?)

ReadMoreView 의 확장/축소 상태 변경 callback 을 설정합니다.
callback 함수는 확장 상태는 true, 축소 상태로 변경되는 경우 false 가 인자로 인입됩니다.
```kotlin
  readMoreView.setStateChangedLisftener { isExpanded ->
    if (isExpanded) {
      // 필요한 동작
    }
  }
```

### setExpandableChangedListener(listener: ((isExpandable: Boolean) -> Unit?))

ReadMoreView 가 조건을 만족하여 확장/축소 가능 여부 변경 callback 을 설정합니다.
원본 String(originalText) 가 View 의 너비에 맞춰 표현될때, 최대 라인 수 (colMaxLines) 를 넘어서는 경우 callback 함수의 인자로 true 가 인입됩니다.
```kotlin
  readMoreView.setExpandableChangedListener { isExpandable ->
    if (isExpandable) {
      // 필요한 동작
    }
  }
```
