# 进度条

## 预览

<img src="https://imgur.com/2E6X3uJ.gif" alt="进度条">

## MFXProgressBars

- 样式类：mfx-progress-bar

- 默认样式表：MFXProgressBar.css

- 默认皮肤：MFXProgressBarSkin.java

### 属性

| 属性     | 描述               | 类型           |
| -------- | ------------------ | --------------:|
| ranges1  | 第一个范围列表     | ObservableList |
| ranges2  | 第二个范围列表     | ObservableList |
| ranges3  | 第三个范围列表     | ObservableList |

### 可样式化属性

| 属性           | 描述                                 | CSS 属性             | 类型   | 默认值 |
| -------------- | ------------------------------------ | -------------------- | ------:| ------:|
| animationSpeed | 指定不确定动画的速度                 | -mfx-animation-speed | Double | 1.0    |

### CSS 选择器

- .mfx-progress-bar

- .mfx-progress-bar .track

- .mfx-progress-bar .bar1

- .mfx-progress-bar .bar2

## MFXProgressSpinners

- 样式类：mfx-progress-spinner

- 默认样式表：MFXProgressSpinner.css

- 默认皮肤：MFXProgressSpinnerSkin.java

### 属性

*MFXProgressSpinner 具有与 MFXProgressBar 相同的属性*

### 可样式化属性

| 属性          | 描述                                         | CSS 属性            | 类型   | 默认值                                      |
| ------------- | -------------------------------------------- | ------------------- | ------:| -------------------------------------------:|
| color1        | 指定旋转器弧的第一种颜色                     | -mfx-color1         | Color  | Color.web("#4285f4")                        |
| color2        | 指定旋转器弧的第二种颜色                     | -mfx-color2         | Color  | Color.web("#db4437")                        |
| color3        | 指定旋转器弧的第三种颜色                     | -mfx-color3         | Color  | Color.web("#f4b400")                        |
| color4        | 指定旋转器弧的第四种颜色                     | -mfx-color4         | Color  | Color.web("#0F9D58")                        |
| radius        | 指定进度旋转器的半径                         | -mfx-radius         | Double | -1（将使用 prefHeight(width) 的值）         |
| startingAngle | 指定旋转器开始的角度                         | -mfx-starting-angle | Double | 360 - Math.random() * 720                   |

### CSS 选择器

- .mfx-progress-spinner

- .mfx-progress-spinner .track

- .mfx-progress-spinner .arc

- .mfx-progress-spinner .percentage
