# 滑块

## 预览

<img src="https://imgur.com/nOrsa1n.gif" alt="滑块">

## MFXSliders

- 样式类：mfx-slider

- 默认样式表：MFXSlider.css

- 默认皮肤：MFXSliderSkin.java

### 属性

| 属性             | 描述                                                                                                                                 | 类型           |
| ---------------- | ------------------------------------------------------------------------------------------------------------------------------------ | --------------:|
| min              | 指定滑块可以达到的最小值                                                                                                             | Double         |
| max              | 指定滑块可以达到的最大值                                                                                                             | Double         |
| value            | 指定滑块的实际值                                                                                                                     | Double         |
| thumbSupplier    | 指定用于构建滑块滑块的 Supplier。<br/>尝试设置或返回 null 值将回退到默认供应商                                                       | Supplier       |
| popupSupplier    | 指定用于构建滑块弹出窗口的 Supplier。<br/>您还可以设置或返回 null 以移除弹出窗口                                                     | Supplier       |
| popupPadding     | 指定滑块与弹出窗口之间的额外间隙                                                                                                     | Double         |
| decimalPrecision | 指定滑块值的小数位数                                                                                                                 | Integer        |
| enableKeyboard   | 指定是否可以使用键盘调整值                                                                                                           | Boolean        |
| ranges1          | 返回第一个范围列表                                                                                                                   | ObservableList |
| ranges2          | 返回第二个范围列表                                                                                                                   | ObservableList |
| ranges3          | 返回第三个范围列表                                                                                                                   | ObservableList |

### 可样式化属性

| 属性                     | 描述                                                                                                                                                                                                         | CSS 属性                        | 类型                  | 默认值                 |
| ------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ------------------------------- | ---------------------:| ---------------------- |
| sliderMode               | 指定滑块模式。可以是 DEFAULT（自由调整滑块）或 SNAP_TO_TICKS（滑块将始终吸附到刻度）                                                                                                                         | -mfx-slider-mode                | SlideMode[枚举]       | SliderMode.DEFAULT     |
| unitIncrement            | 指定按下箭头键时添加到滑块值或从滑块值减去的值。<br/>箭头键取决于滑块方向：水平：右、左；垂直：上、下                                                                                                        | -mfx-unit-increment             | Double                | 10.0                   |
| alternativeUnitIncrement | 指定按下箭头键和 Shift 或 Ctrl 时添加到滑块值或从滑块值减去的值                                                                                                                                              | -mfx-alternative-unit-increment | Double                | 5.0                    |
| tickUnit                 | 数据单位中每个主要刻度标记之间的值                                                                                                                                                                           | -mfx-tick-count                 | Double                | 25.0                   |
| showMajorTicks           | 指定是否显示主要刻度                                                                                                                                                                                         | -mfx-show-major-ticks           | Boolean               | false                  |
| showMinorTicks           | 指定是否显示次要刻度                                                                                                                                                                                         | -mfx-show-minor-ticks           | Boolean               | false                  |
| showTicksAtEdges         | 指定是否显示滑块边缘的主要刻度。<br/>边缘刻度是代表最小值和最大值的刻度                                                                                                                                      | -mfx-show-ticks-at-edge         | Boolean               | true                   |
| minorTicksCount          | 指定两个主要刻度之间应添加多少个次要刻度                                                                                                                                                                     | -mfx-minor-ticks-count          | Integer               | 5                      |
| animateOnPress           | 当按下滑块的轨道时，根据鼠标事件坐标调整值。<br/>此属性指定进度条调整是否应该动画化                                                                                                                          | -mfx-animate-on-press           | Boolean               | true                   |
| bidirectional            | 如果滑块设置为双向，则进度条将始终从 0 开始。<br/>当值为负数时，进度条向 0 的反方向增长。<br/>这仅在 min 为负数且 max 为正数时有效，否则在布局期间将忽略此选项。请参阅控件文档中的警告 | -mfx-bidirectional              | Boolean               | true                   |
| orientation              | 指定滑块的方向                                                                                                                                                                                               | -mfx-orientation                | Orientation[枚举]     | Orientation.HORIZONTAL |
| popupSide                | 指定弹出窗口的一侧。<br/>水平方向默认为上方，垂直方向默认为左侧。<br/>OTHER_SIDE 水平方向为下方，垂直方向为右侧。                                                                                            | -mfx-popup-side                 | SliderPopupSide[枚举] | SlidePopupSide.DEFAULT |

### CSS 选择器

- .mfx-slider

- .mfx-slider .track

- .mfx-slider .axis（用于刻度）

- .mfx-slider .axis .axis-minor-tick-mark（用于次要刻度）

- .mfx-slider .tick-even（用于偶数主要刻度）

- .mfx-slider .tick-odd（用于奇数主要刻度）

- .mfx-slider .bar

- .mfx-slider .thumb-container

- .mfx-slider .thumb-container .thumb（字体图标）

- .mfx-slider .thumb-container .thumb-radius（字体图标）

- .mfx-slider .thumb-container .mfx-ripple-generator
